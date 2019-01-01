//
//  SemanticAnalyzer.java
//  Itpr_3Cmini
//	语义分析器
//
//  Created by Li子青 on 2018/11/13.
//  Copyright © 2018年 Li子青. All rights reserved.
//
package main;

import java.awt.Color;
import java.math.BigDecimal;

import javax.swing.JOptionPane;
import javax.swing.JPanel;
import Tokens.*;

public class SemanticAnalyzer extends Thread {
	private Table table; // 【target1】变量表
	private int errorNum;
	private String errorInfo; // 【target2】Cmini报错
	private int field;

	public SemanticAnalyzer(TreeNode root) {
		this.table = new Table();
		this.errorNum = 0;
		this.errorInfo = "";

		this.field = 0;
		statement(root);
	}

	/* 封装 */
	public Table getTable() {
		return this.table;
	}

	public String getErrorInfo() {
		return this.errorInfo;
	}

	public int getErrorNum() {
		return this.errorNum;
	}

	public void run() {
		Form.problemArea.append("\n");
		Form.problemArea.append("RUN TIME ERRORS:\n");
		if (errorNum != 0) {
			Form.problemArea.append(errorInfo);
			Form.problemArea.append("Semantic error number:" + errorNum + "\n");
			Form.proAndConPanel.setSelectedIndex(1);
			JOptionPane.showMessageDialog(new JPanel(),
					"Exception occurs in the semantic analysis！Please modify the program!", "Semantic Analysis",
					JOptionPane.ERROR_MESSAGE);
		} else {
			Form.problemArea.append("Semantic error number:" + errorNum + "\n");
			Form.proAndConPanel.setSelectedIndex(0);
		}
	}

	// 语法树应该不会有问题(?
	// 1.声明-类型 2.赋值-标识符 3.for 4.if 5.while 6.write 7.read
	private void statement(TreeNode statementNode) {
		int count = statementNode.getChildCount();
		for (int i = 0; i < count; i++) {
			TreeNode currentNode = statementNode.getChildAt(i);
			String content = currentNode.getValue();
			if (content.equals(TreeNode.DECLARATION))
				declare_impt(currentNode);
			else if (content.equals(TreeNode.ASSIGNMENT))
				assign_preprocess(currentNode, false);
			else if (content.equals(TreeNode.WRITE))
				write_impt(currentNode);
			else if (content.equals(TreeNode.FOR))
				for_impt(currentNode);
			else if (content.equals(TreeNode.IF))
				if_impt(currentNode);
			else if (content.equals(TreeNode.WHILE))
				while_impt(currentNode);
			// 剩下的只有emptyNode => do nothing
		}
	}

	// 【1】声明=>有一个到多个被声明变量
	// **1.是否有初始化
	// **2.变量是否未被声明过
	// **3.支持数组/数组不可以直接赋值
	private void declare_impt(TreeNode declareNode) {
		String temp = declareNode.getChildAt(0).getValue();
		int dataType = -1;
		if (temp.equals(KEYWORD.INT))
			dataType = TYPE.INTEGER;
		else if (temp.equals(KEYWORD.REAL))
			dataType = TYPE.REAL;
		else if (temp.equals(KEYWORD.STRING))
			dataType = TYPE.STRING;
		else if (temp.equals(KEYWORD.BOOL))
			dataType = TYPE.BOOL;
		TreeNode curchild;
		int index = 1;
		int varCount = declareNode.getChildCount() - 1;
		while (index <= varCount) {
			curchild = declareNode.getChildAt(index);
			String value = curchild.getValue();
			if (value.equals(TreeNode.ASSIGNMENT)) {// 声明+赋值 int a = 5;
				TreeNode identifierNode = curchild.getChildAt(0);
				String id = identifierNode.getValue();
				int t = identifierNode.getType();
				int l = identifierNode.getLineNo();
				if (table.getVariable(id, field) != null) {// 检查变量是否被声明过
					setError("The variable " + id + " has already been declared, please rename it!", l);
					return;
				}
				if (t == TYPE.ARRAY) {
					setError("The initialization of array is not supported!", l);
					return;
				}
				table.add(new VarNode(dataType, id, field));
				assign_preprocess(curchild, true);
				index++;
			} else {
				int type = curchild.getType();
				int line = curchild.getLineNo();
				if (table.getVariable(value, field) != null) {
					setError("The variable " + value + " has already been declared, please rename it!", line);
					return;
				}
				if (type == TYPE.IDENTIFIER)
					table.add(new VarNode(dataType, value, field));
				else if (type == TYPE.ARRAY)
					array_declare(curchild, dataType);
				index++;
			}
		}
	}

	private void array_declare(TreeNode arrayNode, int dataType) {
		// 1.检查数组长度
		TreeNode sizeNode = arrayNode.getChildAt(0);
		String sizeValue = sizeNode.getValue();
		int sizeType = sizeNode.getType();
		int lineNo = arrayNode.getLineNo();
		if (sizeType == TYPE.IDENTIFIER || sizeType == TYPE.ARRAY) {
			if (!isInitializedVar(sizeNode, field))
				return;
			VarNode valueVar = table.getVariable(sizeValue, field);
			if (sizeType == TYPE.ARRAY) {// 如果是数组元素
				String index = getArrayIndex(sizeNode.getChildAt(0), valueVar.getArrayElementsNum());
				if (index == null)
					return;
				sizeValue += "@" + index;
			}
			valueVar = table.getVariable(sizeValue, field);
			VarNode sizeVar = table.getVariable(sizeValue, field);
			if (sizeVar.getType() != TYPE.INTEGER) {
				setError("The size of an array must be integer!", lineNo);
				return;
			}
			sizeValue = sizeVar.getIntValue();
		} else if (sizeType == TYPE.ATH_OP) {
			sizeValue = doOperation(sizeNode);
			if (sizeValue == null)
				return;
			if (!matchInteger(sizeValue)) {
				setError("The size of an array must be integer!", lineNo);
				return;
			}
		} else if (sizeType != TYPE.INTEGER) {
			setError("The size of the array is invalid!", lineNo);
			return;
		}
		int size = Integer.parseInt(sizeValue);
		if (size < 1) {
			setError("The size of an array must be positive!", lineNo);
			return;
		}

		String arrName = arrayNode.getValue();
		VarNode arrayVar = new VarNode(dataType, arrName, field);
		arrayVar.setArrayElementsNum(size);
		table.add(arrayVar);
		for (int i = 0; i < size; i++)
			table.add(new VarNode(dataType, arrName + "@" + i, field));
	}

	// 【2】赋值 b = 10; a[3] = 5;
	// => 1.是否被声明过 2.支持数组元素赋值 3.赋值类型匹配 小—>大
	// dataType: int, real, string, bool -->valueType:整数,实数,字符串,布尔值,标识符
	private void assign_preprocess(TreeNode assignNode, boolean isInit) {
		// ** 检查等号左值 ** => var
		TreeNode leftNode = assignNode.getChildAt(0);
		String name = leftNode.getValue();
		int lineNo = leftNode.getLineNo();
		// 1.是否被声明过
		VarNode var = table.getVariable(name, field);
		if (var == null) {
			setError("Variable " + name + " hasn't been declared!", lineNo);
			return;
		}
		// 2.是否为数组
		if (!isInit && leftNode.getType() == TYPE.ARRAY) {
			String index = getArrayIndex(leftNode, var.getArrayElementsNum());
			if (index == null)
				return;
			name += "@" + index;
			var = table.getVariable(name, field);
		}

		// ** 检查等号右值 ** => type, value
		TreeNode rightNode = assignNode.getChildAt(1);
		int type = rightNode.getType();
		String value = rightNode.getValue();
		if (type == TYPE.IDENTIFIER || type == TYPE.ARRAY) {
			if (!isInitializedVar(rightNode, field))
				return;
			VarNode valueVar = table.getVariable(value, field);
			if (type == TYPE.ARRAY) {// 如果是数组元素
				String index = getArrayIndex(rightNode.getChildAt(0), valueVar.getArrayElementsNum());
				if (index == null)
					return;
				value += "@" + index;
				valueVar = table.getVariable(value, field);
			}

			type = valueVar.getType();
			if (type == TYPE.INTEGER)
				value = valueVar.getIntValue();
			else if (type == TYPE.REAL)
				value = valueVar.getRealValue();
			else if (type == TYPE.BOOL || type == TYPE.STRING)
				value = valueVar.getStringValue();
		} else if (type == TYPE.ATH_OP) {
			String result = doOperation(rightNode);
			if (result == null)
				return;
			value = result;
			if (matchInteger(result))
				type = TYPE.INTEGER;
			else if (matchReal(result))
				type = TYPE.REAL;
		} else if (type == TYPE.CPN_OP) {
			boolean result = doComparison(rightNode);
			value = String.valueOf(result);
			type = TYPE.BOOL;
		}// else if (value.equals("read")) {
		//	value = read_impt(rightNode);
		//	type = TYPE.STRING;
		//}
		assign_impt(var, type, value, lineNo);
	}

	private void assign_impt(VarNode var, int type, String value, int lineNo) {
		int dataType = var.getType();
		if (dataType == TYPE.INTEGER) {
			if (type == TYPE.INTEGER)
				var.setIntValue(value);
			else if (type == TYPE.REAL) {
				setError("Can't assign real to integer variable!", lineNo);
				return;
			} else if (type == TYPE.BOOL) {
				setError("Can't assign boolean to integer variable!", lineNo);
				return;
			} else if (type == TYPE.STRING) {
				setError("Can't assign string to integer variable!", lineNo);
				return;
			}
		} else if (dataType == TYPE.REAL) {
			if (type == TYPE.INTEGER)
				var.setRealValue(String.valueOf(Double.parseDouble(value)));
			else if (type == TYPE.REAL)
				var.setRealValue(value);
			else if (type == TYPE.BOOL) {
				setError("Can't assign boolean to real variable!", lineNo);
				return;
			} else if (type == TYPE.STRING) {
				setError("Can't assign string to real variable!", lineNo);
				return;
			}
		} else if (dataType == TYPE.BOOL) {
			if (type == TYPE.INTEGER) {
				int i = Integer.parseInt(value);
				if (i <= 0)
					var.setStringValue(KEYWORD.FALSE);
				else
					var.setStringValue(KEYWORD.TRUE);
			} else if (type == TYPE.REAL) {
				setError("Can't assign real to boolean variable!", lineNo);
				return;
			} else if (type == TYPE.BOOL)
				var.setStringValue(value);
			else if (type == TYPE.STRING) {
				setError("Can't assign string to boolean variable!", lineNo);
				return;
			}
		} else if (dataType == TYPE.STRING) {
			if (type == TYPE.INTEGER) {
				setError("Can't assign integer to string variable!", lineNo);
				return;
			} else if (type == TYPE.REAL) {
				setError("Can't assign real to string variable!", lineNo);
				return;
			} else if (type == TYPE.BOOL) {
				setError("Can't assign boolean to string variable!", lineNo);
				return;
			} else if (type == TYPE.STRING) {
				var.setStringValue(value);
			}
		}
	}

	// 【3】for statement |(1)初始化(2)判断--->|(3)程序体|(4)change(5)回到2 |--->(6)跳出
	private void for_impt(TreeNode forNode) {
		field++;
		TreeNode initNode = forNode.getChildAt(0);
		TreeNode judgeNode = forNode.getChildAt(1); // 根结点Judgment
		TreeNode changeNode = forNode.getChildAt(2); // 根结点Change
		TreeNode statementNode = forNode.getChildAt(3); // 根结点Statements

		if (initNode.getChildAt(0).getValue().equals("declaration"))
			declare_impt(initNode.getChildAt(0));
		else if (initNode.getChildAt(0).getValue().equals("assignment")) {
			int num = initNode.getChildCount();
			for (int i = 0; i < num; i++)
				assign_preprocess(initNode.getChildAt(i), false);
		} // 剩下的只有emptyNode

		boolean isEmpty = false;
		int count = 1;
		if (changeNode.getChildAt(0).getValue().equals(TreeNode.EMPTY))
			isEmpty = true;
		else
			count = changeNode.getChildCount();

		while (doComparison(judgeNode.getChildAt(0))) {// judge中元素每次会改变
			field++;
			statement(statementNode);
			table.cleanLocalVar(--field);
			if (!isEmpty)
				for (int i = 0; i < count; i++)
					this.assign_preprocess(changeNode.getChildAt(i), false);
		}

		table.cleanLocalVar(--field);
	}

	// 【4】if statement
	private void if_impt(TreeNode ifNode) {
		TreeNode condition = ifNode.getChildAt(0).getChildAt(0);
		TreeNode ifStatementNode = ifNode.getChildAt(1);
		if (doComparison(condition)) {
			field++;
			statement(ifStatementNode);
			table.cleanLocalVar(--field);
		} else if (ifNode.getChildCount() == 3) { // else语句
			TreeNode elseStatementNode = ifNode.getChildAt(2).getChildAt(0);
			field++;
			statement(elseStatementNode);
			table.cleanLocalVar(--field);
		}
	}

	// 【5】while statement
	private void while_impt(TreeNode whileNode) {
		TreeNode judgeNode = whileNode.getChildAt(0);
		TreeNode statementNode = whileNode.getChildAt(1);

		while (doComparison(judgeNode.getChildAt(0))) {
			field++;
			statement(statementNode);
			table.cleanLocalVar(--field);
		}
	}

	// 【6】write(value);
	private void write_impt(TreeNode writeNode) {
		TreeNode contentNode = writeNode.getChildAt(0);
		String value = contentNode.getValue(); // 要写的内容
		int type = contentNode.getType();
		if (type == TYPE.IDENTIFIER || type == TYPE.ARRAY) { // 标识符
			if (!isInitializedVar(contentNode, field))
				return;
			VarNode varNode = table.getVariable(value, field);
			// 数组元素
			if (type == TYPE.ARRAY) {
				String index = getArrayIndex(writeNode.getChildAt(0),
						table.getVariable(value, field).getArrayElementsNum());
				if (index == null)
					return;
				value += "@" + index;
				varNode = table.getVariable(value, field);
			}

			type = varNode.getType();
			if (type == TYPE.INTEGER)
				value = varNode.getIntValue();
			else if (type == TYPE.INTEGER)
				value = varNode.getRealValue();
			else
				value = varNode.getStringValue();
		} else if (type == TYPE.ATH_OP) {
			String result = doOperation(writeNode);
			if (result != null)
				value = result;
		}

		// io写
		Form.setControlArea(Color.BLACK, false);
		Form.consoleArea.setText(Form.consoleArea.getText() + value + "\n");
	}

	// 【7】read();
	//private String read_impt(TreeNode readNode) {
	//	Form.setControlArea(Color.GREEN, true);
	//	System.out.println("asdfagdsfgadfaa");
	//	String value = readInput();
	//	System.out.println("A");
	//	System.out.println(value);
	//	return value;
	//}

	//public synchronized String readInput() {
	//	String result = null;
	//	try {
	//		while (userInput == null) {
	//			System.out.println("A");
	//			wait();
	//		}
	//	} catch (InterruptedException ie) {
	//		ie.printStackTrace();
	//	}
	//	result = userInput;
	//	userInput = null;
	//	return result;
	//}

	// 【8】支持运算
	// 1.比较
	// > < <> == true false 布尔变量
	private boolean doComparison(TreeNode opNode) {
		String value = opNode.getValue();
		int type = opNode.getType();

		if (value.equals(KEYWORD.TRUE)) {
			return true;
		} else if (value.equals(KEYWORD.FALSE)) {
			return false;
		} else if (type == TYPE.IDENTIFIER || type == TYPE.ARRAY) {
			if (isInitializedVar(opNode, field))
				return false;
			VarNode varNode = table.getVariable(value, field);
			if (type == TYPE.ARRAY) {
				String index = getArrayIndex(opNode.getChildAt(0), varNode.getArrayElementsNum());
				if (index == null)
					return false;
				value += "@" + index;
				varNode = table.getVariable(value, field);
			}
			if (varNode.getType() != TYPE.BOOL) {
				String error = "Can't set " + value + " as judge condition!";
				setError(error, opNode.getLineNo());
			}
			value = varNode.getStringValue();
			if (value.equals(KEYWORD.TRUE))
				return true;
			else
				return false;
		} else if (type == TYPE.CPN_OP) {
			// 存放两个待比较对象的值
			int count = 2;// opNode.getChildCount();
			String[] results = new String[2];
			for (int i = 0; i < count; i++) {
				int t = opNode.getChildAt(i).getType();
				String v = opNode.getChildAt(i).getValue();
				if (t == TYPE.INTEGER || t == TYPE.REAL) { // 常量
					results[i] = v;
				} else if (t == TYPE.IDENTIFIER || t == TYPE.ARRAY) { // 标识符
					if (!isInitializedVar(opNode.getChildAt(i), field))
						return false;
					VarNode varNode = table.getVariable(v, field);
					if (t == TYPE.ARRAY) {
						String index = getArrayIndex(opNode.getChildAt(i).getChildAt(0), varNode.getArrayElementsNum());
						if (index == null)
							return false;
						v += "@" + index;
						varNode = table.getVariable(v, field);
					}
					if (varNode.getType() == TYPE.INTEGER) {
						results[i] = varNode.getIntValue();
					} else if (varNode.getType() == TYPE.REAL) {
						results[i] = varNode.getRealValue();
					} else {
						setError("Can't use " + v + " in judge condition!", opNode.getLineNo());
						return false;
					}
				} else if (t == TYPE.ATH_OP) { // 表达式
					String result = doOperation(opNode.getChildAt(i));
					if (result == null)
						return false;
					results[i] = result;
				}
			}
			if (!results[0].equals("") && !results[1].equals("")) {
				double element1 = Double.parseDouble(results[0]);
				double element2 = Double.parseDouble(results[1]);
				if (value.equals(">") && element1 > element2) {
					return true;
				} else if (value.equals("<") && element1 < element2) {
					return true;
				} else if (value.equals("==") && element1 == element2)
					return true;
				else if (value.equals("<>") && element1 != element2)
					return true;
				else
					return false;
			}
		}
		return false;// 语义分析出错或者分析条件结果为假返回false
	}

	// 2.四则运算
	private String doOperation(TreeNode opNode) {
		boolean isInt = true;
		int count = opNode.getChildCount();// =>2
		String value = opNode.getValue();
		String[] results = new String[count];
		for (int i = 0; i < count; i++) {
			int t = opNode.getChildAt(i).getType();
			String v = opNode.getChildAt(i).getValue();
			if (t == TYPE.INTEGER) { // 整数
				results[i] = v;
			} else if (t == TYPE.REAL) { // 实数
				results[i] = v;
				isInt = false;
			} else if (t == TYPE.IDENTIFIER || t == TYPE.ARRAY) { // 标识符
				if (!isInitializedVar(opNode.getChildAt(i), field))
					return null;
				VarNode varNode = table.getVariable(v, field);
				if (t == TYPE.ARRAY) {
					String index = getArrayIndex(opNode.getChildAt(i).getChildAt(0), varNode.getArrayElementsNum());
					if (index == null)
						return null;
					v += "@" + index;
					varNode = table.getVariable(v, field);

				}
				if (varNode.getType() == TYPE.INTEGER) {
					results[i] = varNode.getIntValue();
				} else if (varNode.getType() == TYPE.REAL) {
					results[i] = varNode.getRealValue();
					isInt = false;
				}

			} else if (t == TYPE.ATH_OP) { // 表达式
				String result = doOperation(opNode.getChildAt(i));
				if (result == null)
					return null;
				results[i] = result;
				if (matchReal(result))
					isInt = false;
			}
		}
		if (isInt) {
			int e1 = Integer.parseInt(results[0]);
			int e2 = Integer.parseInt(results[1]);
			if (value.equals("+"))
				return String.valueOf(e1 + e2);
			else if (value.equals("-"))
				return String.valueOf(e1 - e2);
			else if (value.equals("*"))
				return String.valueOf(e1 * e2);
			else
				return String.valueOf(e1 / e2);
		} else {
			double e1 = Double.parseDouble(results[0]);
			double e2 = Double.parseDouble(results[1]);
			BigDecimal bd1 = new BigDecimal(e1);
			BigDecimal bd2 = new BigDecimal(e2);
			if (value.equals("+"))
				return String.valueOf(bd1.add(bd2).floatValue());
			else if (value.equals("-"))
				return String.valueOf(bd1.subtract(bd2).floatValue());
			else if (value.equals("*"))
				return String.valueOf(bd1.multiply(bd2).floatValue());
			else
				return String.valueOf(bd1.divide(bd2, 3, BigDecimal.ROUND_HALF_UP).floatValue());
		}
	}

	/* 变量表辅助函数 */
	// 使用变量被声明过
	private boolean isInitializedVar(TreeNode idNode, int field) {
		String idName = idNode.getValue();// 标识符名字
		int lineNo = idNode.getLineNo();
		if (table.getVariable(idName, field) == null) {
			setError("Using undeclared variable " + idName + "!", lineNo);
			return false;
		} else {
			if (idNode.getType() == TYPE.ARRAY) {
				String index = getArrayIndex(idNode.getChildAt(0),
						table.getVariable(idName, field).getArrayElementsNum());
				if (index == null)
					return false;
				idName += "@" + index;
			}
			VarNode temp = table.getVariable(idName, field);
			if (temp.getIntValue().equals("") && temp.getRealValue().equals("") && temp.getStringValue().equals("")) {
				setError("Variable " + idName + " hasn't been initiated before use!", lineNo);
				return false;
			} else {
				return true;
			}
		}
	}

	// 取数组下标的值
	private String getArrayIndex(TreeNode arrayNode, int size) {
		TreeNode indexNode = arrayNode.getChildAt(0);
		int type = arrayNode.getType();
		String value = indexNode.getValue();
		int lineNo = arrayNode.getLineNo();
		if (type == TYPE.IDENTIFIER) {
			if (!isInitializedVar(indexNode, field)) {
				setError("The variable " + value + " hasn't been initialized!", lineNo);
				return null;
			}
			VarNode indexVar = table.getVariable(value, field);
			if (indexVar.getType() != TYPE.INTEGER) {
				setError("The index of array must be integer!", lineNo);
				return null;
			}
			value = indexVar.getIntValue();
		} else if (type == TYPE.ATH_OP) { // 表达式
			value = doOperation(indexNode);
			if (value == null)
				return null;
			if (!matchInteger(value)) {
				setError("The index of array must be integer!", lineNo);
				return null;
			}
		} else if (type != TYPE.INTEGER) {
			setError("The index of the array is invalid!", lineNo);
			return null;
		}

		int index = Integer.parseInt(value);
		if (index > -1 && index < size) {
			return value;
		} else if (index < 0) {
			setError("The index of an array can't be negative!", lineNo);
			return null;
		} else {
			setError("The index is out of bound!", lineNo);
			return null;
		}
	}

	/* 辅助函数 */
	public void setError(String info, int line) {
		errorInfo += "Error(line " + line + "):" + info + "\n";
		errorNum++;
	}

	private static boolean matchInteger(String input) {
		return (input.matches("^-?\\d+$") && !input.matches("^-?0{1,}\\d+$"));
	}

	private static boolean matchReal(String input) {
		return (input.matches("^(-?\\d+)(\\.\\d+)+$") && !input.matches("^(-?0{2,}+)(\\.\\d+)+$"));
	}

}