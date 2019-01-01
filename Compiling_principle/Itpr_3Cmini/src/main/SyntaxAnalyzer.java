//
//  SyntexAnalyser.java
//	Itpr_3Cmini
//	语法分析器
//
//  Created by Li子青 on 2018/10/19.
//  Copyright © 2018年 Li子青. All rights reserved.
//
package main;

import java.util.ArrayList;
import Tokens.*;

public class SyntaxAnalyzer {
	private ArrayList<Token> tokens; // 词法分析器->词法单元
	private TreeNode root; // 【target】语法树
	private String error;
	private int errCount; // 【target2】Cmini报错
	private int index;// 当前处理到的token编号
	private Token currentToken = null;// 当前处理到的token

	/* 封装 */
	public TreeNode getRoot() {
		return this.root;
	}

	public int getErrCount() { return this.errCount; }
	public String getError() { return error; }

	public SyntaxAnalyzer(ArrayList<Token> tokens) {
		this.tokens = tokens;
		this.root = new TreeNode("PROGRAM");
		this.error = "";
		this.errCount = 0;
		this.index = 0;
		if (tokens.size() != 0)
			this.currentToken = tokens.get(index);
		while (index < tokens.size()) // 如果当前token的index值越界，则不会往后执行
			this.root.add(statement());
	}
	// TODO
	// 每一个新的程序一定要刷新一下
	// 完成当前步骤一定要往后推一个Token
	// 查空！查空！查空！ ***在缺少的元素处停下当前语句
	// 1.出错：中断当前语句剩余部分(for语句等无法根据;停止) 2.currentToken == null
	// 函数送入时不可为空//*检查送入的token

	// method: 检查第一个元素，送入结点分析函数
	// 1.声明-类型 2.赋值-标识符 3.for 4.if 5.while 6.write /// 7.read 8.运算->必须和赋值绑定
	private final TreeNode statement() {
		String val = currentToken.getValue();
		if (val.equals(";")) {
			TreeNode es = new TreeNode(currentToken.getLineNo(), TreeNode.EMPTY);
			nextToken();
			return es;
		} else if (val.equals(KEYWORD.INT) || val.equals(KEYWORD.REAL) || val.equals(KEYWORD.BOOL)
				|| val.equals(KEYWORD.STRING))
			return declare_stm(false);
		else if (currentToken.getType() == TYPE.IDENTIFIER)
			return assign_stm(false);
		else if (val.equals(KEYWORD.FOR))
			return for_stm();
		else if (val.equals(KEYWORD.IF))
			return if_stm();
		else if (val.equals(KEYWORD.WHILE))
			return while_stm();
		else if (val.equals(KEYWORD.WRITE))
			return write_stm();
		setError("The statement starts with wrong token.");
		while (currentToken != null && !currentToken.getValue().equals(";"))
			nextToken();
		nextToken();
		return new TreeNode("ERROR: the statement starts with wrong token");
	}

	// 支持数组 array[x+1] array[10]
	private final TreeNode array(TreeNode arrayId) {
		TreeNode arrayNode = new TreeNode(arrayId.getLineNo(), TYPE.ARRAY, arrayId.getValue());
		nextToken();
		if (currentToken == null) {
			setError("The array is incomplete!");
			arrayNode.add(new TreeNode("ERROR：The for statement is incomplete!"));
			return arrayNode;
		}
		arrayNode.add(E());
		matchElement("]", arrayNode);
		return arrayNode;
	}

	// 【1】声明 string b; int a = 3, b;
	// *****支持同时声明多个变量
	// *****支持声明+赋值
	private final TreeNode declare_stm(boolean isFor) {
		TreeNode declareNode = new TreeNode(currentToken.getLineNo(), TreeNode.DECLARATION);
		TreeNode dataTypeNode = new TreeNode(currentToken.getLineNo(), TYPE.DATATYPE, currentToken.getValue());
		declareNode.add(dataTypeNode);
		do {
			nextToken();
			if (currentToken == null) {
				setError("The declaration statement is incomplete!");
				declareNode.add(new TreeNode("ERROR: The declaration statement is incomplete!"));
				return declareNode;
			}
			declareNode = processDeclare(declareNode);
			if (currentToken == null)
				break;
		} while (currentToken.getValue().equals(","));

		if (!isFor)
			matchElement(";", declareNode);
		return declareNode;
	}

	// *****不支持数组初始化int a[]=...;
	private final TreeNode processDeclare(TreeNode rootNode) {
		if (currentToken.getType() == TYPE.IDENTIFIER) {
			TreeNode idNode = new TreeNode(currentToken.getLineNo(), TYPE.IDENTIFIER, currentToken.getValue());
			nextToken();
			// 可以int a = 10, b;
			// 1.数组 2.声明+赋值(*不会把数组输入到初始化中) 3.普通 4.异常
			if (currentToken != null) {
				if (currentToken.getValue().equals("[")) {
					idNode = array(idNode);
					rootNode.add(idNode);
					if (currentToken != null && currentToken.getValue().equals("=")) {
						setError("Array initialization is not supported!");
						idNode.add(new TreeNode("ERROR: Array initialization is not supported!"));
					}
					while (currentToken != null && !currentToken.getValue().equals(";")
							&& !currentToken.getValue().equals(","))
						nextToken();
					return rootNode;
				}
				if (currentToken.getValue().equals("=")) {
					// ps.此处根本没有调用assign_stm直接实现了赋值结点
					TreeNode assignNode = new TreeNode(currentToken.getLineNo(), TreeNode.ASSIGNMENT);
					rootNode.add(assignNode);
					assignNode.add(idNode);
					nextToken();
					if (currentToken == null) {
						setError("The assignment statement lacks value!");
						assignNode.add(new TreeNode("ERROR: The assignment statement lacks value!"));
					} else
						assignNode.add(C());
					return rootNode;
				}
			}
			rootNode.add(idNode);
			return rootNode;
		} else {
			setError("The declaration statement lacks valid identifier!");
			rootNode.add(new TreeNode("ERROR: The declaration statement lacks valid identifier!"));
			while (currentToken != null && !currentToken.getValue().equals(";"))
				nextToken();
			// 出现错误，跳过当前语句的剩余部分
		}
		return rootNode;
	}

	// 【2】赋值 a = 10; b = "asb";
	private final TreeNode assign_stm(boolean isFor) {
		TreeNode assignNode = new TreeNode(currentToken.getLineNo(), TreeNode.ASSIGNMENT);
		TreeNode idNode = new TreeNode(currentToken.getLineNo(), TYPE.IDENTIFIER, currentToken.getValue());
		assignNode.add(idNode);
		nextToken();
		if (currentToken == null) {
			matchElement("=", assignNode);
			return assignNode;
		}

		if (currentToken.getValue().equals("["))// 支持数组
			idNode = array(idNode);

		matchElement("=", assignNode);
		if ((!isFor && currentToken.getValue().equals(";")) || (isFor && currentToken.getValue().equals(")"))
				|| currentToken == null) {
			setError("The assignment statement lacks value!");
			assignNode.add(new TreeNode("ERROR: The assignment statement lacks value!"));
			nextToken();
			return assignNode;
		}
		assignNode.add(C());

		if (!isFor)
			matchElement(";", assignNode);
		return assignNode;
	}

	// 【3】for(init;judge;change) statement
	private final TreeNode for_stm() {
		TreeNode forNode = new TreeNode(currentToken.getLineNo(), TreeNode.FOR);
		nextToken();

		if (currentToken == null) {
			matchElement("(", forNode);
			return forNode;
		}
		matchElement("(", forNode);
		if (currentToken == null) {
			setError("The for statement is incomplete!");
			forNode.add(new TreeNode("ERROR：The for statement is incomlete!"));
			return forNode;
		}

		// initializaiton: 1.如果是声明+赋值(可以不带赋值) 2.如果是直接赋值 3.可以有多条赋值 3.初始化部分为空
		TreeNode initNode = new TreeNode(currentToken.getLineNo(), TreeNode.INIT);
		forNode.add(initNode);
		if (currentToken.getValue().equals(KEYWORD.INT) || currentToken.getValue().equals(KEYWORD.REAL)
				|| currentToken.getValue().equals(KEYWORD.BOOL) || currentToken.getValue().equals(KEYWORD.STRING))
			initNode.add(declare_stm(true));
		else if (currentToken.getType() == TYPE.IDENTIFIER) {
			initNode.add(assign_stm(true));
			for (; currentToken != null && currentToken.getValue().equals(","); nextToken())
				initNode.add(assign_stm(true));
		} else if (currentToken.getValue().equals(";")) {
			TreeNode empNode = new TreeNode(currentToken.getLineNo(), TreeNode.EMPTY);
			initNode.add(empNode);
		} else {
			setError("Exception occurs in the initialization part of the for statement!");
			initNode.add(new TreeNode("ERROR: Exception occurs in the initialization part of the for statement!"));
			nextToken();
		}

		if (currentToken == null) {
			setError("The for statement is incomplete!");
			forNode.add(new TreeNode("ERROR：The for statement is incomlete!"));
			return forNode;
		}
		matchElement(";", forNode);
		if (currentToken == null) {
			setError("The for statement is incomplete!");
			forNode.add(new TreeNode("ERROR：The for statement is incomlete!"));
			return forNode;
		}

		// loop conditon: 1.循环条件只可以有一项不可以有',' 2.循环条件不可以为空
		TreeNode judgeNode = new TreeNode(currentToken.getLineNo(), TreeNode.JUDGE);
		forNode.add(judgeNode);
		if (currentToken.getValue().equals(";")) {
			setError("The loop condition of for statement can't be empty!");
			judgeNode.add(new TreeNode("ERROR: The loop condition of for statement can't be empty!"));
			nextToken();
		} else {
			judgeNode.add(C());
			if (currentToken == null) {
				setError("The for statement is incomplete!");
				forNode.add(new TreeNode("ERROR：The for statement is incomplete!"));
				return forNode;
			}
			matchElement(";", forNode);
			if (currentToken == null) {
				setError("The for statement is incomplete!");
				forNode.add(new TreeNode("ERROR：The for statement is incomplete!"));
				return forNode;
			}
		}

		// change：1.可以为空 2.可以有多条赋值
		TreeNode changeNode = new TreeNode(currentToken.getLineNo(), TreeNode.CHANGE);
		if (currentToken.getValue().equals(")")) {
			TreeNode empNode = new TreeNode(currentToken.getLineNo(), TreeNode.EMPTY);
			changeNode.add(empNode);
			nextToken();
		} else {
			changeNode.add(assign_stm(true));
			for (; currentToken != null && currentToken.getValue().equals(","); nextToken())
				changeNode.add(assign_stm(true));
			if (currentToken == null) {
				setError("The for statement is incomplete!");
				forNode.add(new TreeNode("ERROR：The for statement is incomplete!"));
				return forNode;
			}
			matchElement(")", forNode);
		}
		forNode.add(changeNode);

		if (currentToken == null) {
			setError("The for statement is incomlete!");
			forNode.add(new TreeNode("ERROR：The for statement is incomplete!"));
			return forNode;
		}
		// 1.单条语句 2.多条语句，有{}
		TreeNode statementNode = new TreeNode(currentToken.getLineNo(), TreeNode.STATEMENTS);
		forNode.add(statementNode);
		boolean hasBrace = true;
		if (currentToken.getValue().equals("{")) {
			nextToken();
			if (currentToken == null) {
				matchElement("}", forNode);
				return forNode;
			}
		} else
			hasBrace = false;

		if (hasBrace) {
			if (currentToken.getValue().equals("}"))
				statementNode.add(new TreeNode(currentToken.getLineNo(), TreeNode.EMPTY));
			while (!currentToken.getValue().equals("}")) {
				statementNode.add(statement());
				if (currentToken == null)
					break;
			}
			matchElement("}", forNode);
		} else
			statementNode.add(statement());

		return forNode;
	}

	// 【4】if(..) statement
	private final TreeNode if_stm() {
		TreeNode ifNode = new TreeNode(currentToken.getLineNo(), TreeNode.IF);
		nextToken();
		if (currentToken == null) {
			setError("The if statement is incomlete!");
			ifNode.add(new TreeNode("ERROR：The if statement is incomplete!"));
			return ifNode;
		}
		matchElement("(", ifNode);
		if (currentToken == null) {
			setError("The if statement is incomlete!");
			ifNode.add(new TreeNode("ERROR：The if statement is incomplete!"));
			return ifNode;
		}

		// loop condition: 1.只可以有一条 2.不可以为空
		// 会在C中被处理么？？？
		TreeNode judgeNode = new TreeNode(currentToken.getLineNo(), TreeNode.JUDGE);
		ifNode.add(judgeNode);
		judgeNode.add(C());

		if (currentToken == null) {
			setError("The if statement is incomlete!");
			ifNode.add(new TreeNode("ERROR：The if statement is incomplete!"));
			return ifNode;
		}
		matchElement(")", ifNode);
		if (currentToken == null) {
			matchElement(";", ifNode);
			return ifNode;
		}

		// if语句：1.单条语句 2.多条语句，有{}
		TreeNode ifStatementNode = new TreeNode(currentToken.getLineNo(), TreeNode.STATEMENTS);
		ifNode.add(ifStatementNode);
		boolean hasIfBrace = true;
		if (currentToken.getValue().equals("{")) {
			nextToken();
			if (currentToken == null) {
				matchElement("}", ifNode);
				return ifNode;
			}
		} else
			hasIfBrace = false;

		if (hasIfBrace) {
			if (currentToken.getValue().equals("}"))
				ifStatementNode.add(new TreeNode(currentToken.getLineNo(), TreeNode.EMPTY));
			while (!currentToken.getValue().equals("}")) {
				ifStatementNode.add(statement());
				if (currentToken == null)
					break;
			}
			matchElement("}", ifNode);
		} else
			ifStatementNode.add(statement());

		// **匹配else
		// 只可以有一个else
		if (currentToken != null && currentToken.getValue().equals(KEYWORD.ELSE)) {
			TreeNode elseNode = new TreeNode(currentToken.getLineNo(), TreeNode.ELSE);
			ifNode.add(elseNode);
			nextToken();
			if (currentToken == null) {
				matchElement(";", elseNode);
				return ifNode;
			}

			// else语句：1.单条语句 2.多条语句，有{}
			TreeNode elseStatementNode = new TreeNode(currentToken.getLineNo(), TreeNode.STATEMENTS);
			elseNode.add(elseStatementNode);
			boolean hasElseBrace = true;
			if (currentToken.getValue().equals("{")) {
				nextToken();
				if (currentToken == null) {
					matchElement("}", ifNode);
					return ifNode;
				}
			} else
				hasElseBrace = false;

			if (hasElseBrace) {
				if (currentToken.getValue().equals("}"))
					elseStatementNode.add(new TreeNode(currentToken.getLineNo(), TreeNode.EMPTY));
				while (!currentToken.getValue().equals("}")) {
					elseStatementNode.add(statement());
					if (currentToken == null)
						break;
				}
				matchElement("}", elseNode);
			} else
				elseStatementNode.add(statement());
		}
		return ifNode;
	}

	// 【5】while(..) statement
	private final TreeNode while_stm() {
		TreeNode whileNode = new TreeNode(currentToken.getLineNo(), TreeNode.WHILE);
		nextToken();
		if (currentToken == null) {
			setError("The while statement is incomlete!");
			whileNode.add(new TreeNode("ERROR：The while statement is incomplete!"));
			return whileNode;
		}
		matchElement("(", whileNode);
		if (currentToken == null) {
			setError("The while statement is incomlete!");
			whileNode.add(new TreeNode("ERROR：The while statement is incomplete!"));
			return whileNode;
		}

		// loop condition: 1.只可以有一条 2.不可以为空
		TreeNode judgeNode = new TreeNode(currentToken.getLineNo(), TreeNode.JUDGE);
		whileNode.add(judgeNode);
		if (currentToken.getValue() == ")") {
			setError("The loop condition of the while statement can't be empty!");
			judgeNode.add(new TreeNode("ERROR：The loop condition of the while statement can't be empty!"));
			nextToken();
		} else {
			judgeNode.add(C());
			matchElement(")", whileNode);
		}

		if (currentToken == null) {
			setError("The while statement is incomlete!");
			whileNode.add(new TreeNode("ERROR：The while statement is incomplete!"));
			return whileNode;
		}
		// while语句：1.单条语句 2.多条语句，有{}
		TreeNode statementNode = new TreeNode(currentToken.getLineNo(), TreeNode.STATEMENTS);
		whileNode.add(statementNode);
		boolean hasBrace = true;
		if (currentToken.getValue().equals("{")) {
			nextToken();
			if (currentToken == null) {
				matchElement("}", whileNode);
				return whileNode;
			}
		} else
			hasBrace = false;

		if (hasBrace) {
			while (!currentToken.getValue().equals("}")) {
				statementNode.add(statement());
				if (currentToken == null)
					break;
			}
			if (statementNode.getChildCount() == 0) {
				whileNode.remove(whileNode.getChildCount() - 1);
				statementNode.setValue("emptyStatment");
				whileNode.add(statementNode);
			}
			matchElement("}", whileNode);
		} else
			statementNode.add(statement());

		return whileNode;
	}

	// 【6】read();
	//private final TreeNode read_stm() {
	//	TreeNode readNode = new TreeNode(currentToken.getLineNo(), TreeNode.READ);
	//	nextToken();
	//	matchElement("(", readNode);
	//	matchElement(")", readNode);
	//	return readNode;
	//}

	// 【7】write(value);
	private final TreeNode write_stm() {
		TreeNode writeNode = new TreeNode(currentToken.getLineNo(), TreeNode.WRITE);
		nextToken();
		matchElement("(", writeNode);
		if (currentToken.getValue().equals(")")) {
			setError("The output value of the write statement can't be empty!");
			writeNode.add(new TreeNode("ERROR：The output value of the write statement can't be empty!"));
			nextToken();
		} else {
			writeNode.add(E());// **匹配输出值，支持嵌套运算
			matchElement(")", writeNode);
		}
		matchElement(";", writeNode);
		return writeNode;
	}

	// 【8】运算
	// 1.四则运算 2.比较运算
	// C->C==E|C<>E|C>E|C<E|E
	// E->E+T|E-T|T
	// T->T*F|T/F|F
	// F->(E)|d
	
	private final TreeNode C() {
		TreeNode cpn = E();
		if (currentToken != null) {
			String val = currentToken.getValue();
			if (val.equals("==") || val.equals("<>") || val.equals("<") || val.equals(">")) {
				TreeNode comparisonNode = operation();
				comparisonNode.add(cpn);
				comparisonNode.add(E());
				cpn = comparisonNode;
				return cpn;
			}
		}
		return cpn;
	}

	private final TreeNode E() {
		TreeNode exp = T();
		while (currentToken != null && (currentToken.getValue().equals("+") || currentToken.getValue().equals("-"))) {
			TreeNode addNode = operation();
			addNode.add(exp);
			addNode.add(T());
			exp = addNode;
		}
		return exp;
	}

	private final TreeNode T() {
		TreeNode term = F();
		while (currentToken != null && (currentToken.getValue().equals("*") || currentToken.getValue().equals("/"))) {
			TreeNode mulNode = operation();
			mulNode.add(term);
			mulNode.add(F());
			term = mulNode;
		}
		return term;
	}

	// F->(E)|d
	private final TreeNode F() {
		TreeNode factor = null;
		if (currentToken != null) {
			String val = currentToken.getValue();
			int typ = currentToken.getType();
			// if (val.equals(KEYWORD.READ)) {
			// return read_stm();
			// } else
			if (typ == TYPE.INTEGER) {
				factor = new TreeNode(currentToken.getLineNo(), TYPE.INTEGER, val);
				nextToken();
				return factor;
			} else if (typ == TYPE.REAL) {
				factor = new TreeNode(currentToken.getLineNo(), TYPE.REAL, val);
				nextToken();
				return factor;
			} else if ((val.equals(KEYWORD.TRUE) || val.equals(KEYWORD.FALSE))) {
				factor = new TreeNode(currentToken.getLineNo(), TYPE.BOOL, val);
				nextToken();
				return factor;
			} else if (val.equals("\"")) {// && val.equals("\"")
				nextToken();
				factor = new TreeNode(currentToken.getLineNo(), TYPE.STRING, currentToken.getValue());
				nextToken();
				if (currentToken == null || !currentToken.getValue().equals("\"")) {
					setError("The string lacks quotation marks!");
					return new TreeNode("ERROR: The string lacks quotation marks!");
				}
				nextToken();
				return factor;
			} else if (typ == TYPE.IDENTIFIER) {
				factor = new TreeNode(currentToken.getLineNo(), TYPE.IDENTIFIER, val);
				nextToken();
				if (currentToken != null && currentToken.getValue().equals("["))// 支持数组
					factor = array(factor);
				return factor;
			} else if (val.equals("(")) {
				nextToken();
				factor = C();
				if (currentToken == null || !currentToken.getValue().equals(")")) {
					setError("The arithmetic factor lacks right parenthese!");
					return new TreeNode("ERROR: The arithmetic factor lacks right parenthese!");
				}
				nextToken();
				return factor;
			}
		}
		setError("Unepected value!");
		if (currentToken != null && !currentToken.getValue().equals(";")) {
			nextToken();
		}
		return new TreeNode("ERROR: Exception occurs at the arithmetic factor!");
	}

	/* 辅助函数 */
	private void nextToken() {
		index++;
		if (index > tokens.size() - 1) {
			currentToken = null;
			if (index > tokens.size())
				index--;
			return;
		}
		currentToken = tokens.get(index);
	}

	private void setError(String info) {
		int pos, li;
		this.errCount++;
		if (currentToken == null) {
			Token previous = tokens.get(index - 1);
			pos = previous.getColumn();
			li = previous.getLineNo();
		} else {
			pos = currentToken.getColumn();
			li = currentToken.getLineNo();
		}
		this.error += "Error(line " + li + ", position" + pos + "): " + info + "\n";
	}

	private boolean matchElement(String e, TreeNode rootNode) {
		if (currentToken == null || !currentToken.getValue().equals(e)) {
			String info = "The " + rootNode.getValue() + " statement lacks \"" + e + "\"!";
			setError(info);
			rootNode.add(new TreeNode("ERROR：" + info));
			return false;
		}
		nextToken();
		return true;
	}

	private final TreeNode operation() {
		if (currentToken != null) {
			TreeNode tempNode = null;
			String val = currentToken.getValue();
			if (val.equals("+") || val.equals("-") || val.equals("*") || val.equals("/"))
				tempNode = new TreeNode(currentToken.getLineNo(), TYPE.ATH_OP, val);
			else if (val.equals("<") || val.equals(">") || val.equals("==") || val.equals("<>"))
				tempNode = new TreeNode(currentToken.getLineNo(), TYPE.CPN_OP, val);
			nextToken();
			return tempNode;
		}
		setError("Exception occurs at the operator!");
		return new TreeNode("ERROR：Exception occurs at the operator!");
	}
}
