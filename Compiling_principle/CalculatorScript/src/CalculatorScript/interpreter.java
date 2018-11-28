package CalculatorScript;

import java.io.IOException;
import java.io.Writer;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Stack;
import java.util.Vector;


public class interpreter {
	public static HashMap<String, myNumber> identifiers = new HashMap<String, myNumber>();
	public static int lineCounter = 0;
	public static final String[] ELEMENT_LETTER = new String[] { "a", "b", "c", "d", "e", "f", "g", "h", "i", "j", "k",
			"l", "m", "n", "o", "p", "q", "r", "s", "t", "u", "v", "w", "x", "y", "z", "A", "B", "C", "D", "E", "F",
			"G", "H", "I", "J", "K", "L", "M", "N", "O", "P", "Q", "R", "S", "T", "U", "V", "W", "X", "Y", "Z", "0",
			"1", "2", "3", "4", "5", "6", "7", "8", "9" };
	public static final String[] ELEMENT_OP = new String[] { "=", "+", "-", "*", "/", "(", ")" };
	public static final HashSet<String> letter = new HashSet<String>(Arrays.asList(ELEMENT_LETTER));
	public static final HashSet<String> op = new HashSet<String>(Arrays.asList(ELEMENT_OP));

	// method1: 获得词法单元
	// 把输入string截成一个个token放入Token Vector。
	// *****(1)number [integer/double]
	// *****(2)标志符
	// *****(3)operator
	static public Vector<Token> getLexicalUnit(String sentence) throws TokenException {
		Vector<Token> myTokens = new Vector<Token>();
		lineCounter++;

		for (int a = 0; a < sentence.length() - 1; a++) {
			// ***空格
			if (sentence.charAt(a) == ' ') continue;

			// ****1.number
			if (Character.isDigit(sentence.charAt(a))) {
				Boolean isDouble = false;
				StringBuffer num = new StringBuffer();
				num.append(sentence.charAt(a++));
				while (Character.isDigit(sentence.charAt(a)) || sentence.charAt(a) == '.') {
					if (sentence.charAt(a) == '.')
						isDouble = true;
					num.append(sentence.charAt(a++));
				}
				try {
					myNumber temp;
					if (isDouble)
						temp = new myNumber(Double.parseDouble(num.toString()));
					else
						temp = new myNumber(Integer.parseInt(num.toString()));
					myTokens.add(new Token('c', "", temp));
				} catch (NumberFormatException e) {
					throw new TokenException("the number is invalid", lineCounter, a);
				}
			}

			// ***2.标志符
			if ((sentence.charAt(a) >= 'a' && sentence.charAt(a) <= 'z')
					|| (sentence.charAt(a) >= 'A' && sentence.charAt(a) <= 'Z')) {
				StringBuffer var = new StringBuffer();
				var.append(sentence.charAt(a++));
				while ((sentence.charAt(a) >= 'a' && sentence.charAt(a) <= 'z')
						|| (sentence.charAt(a) >= 'A' && sentence.charAt(a) <= 'Z') 
						|| Character.isDigit(sentence.charAt(a))) {
					var.append(sentence.charAt(a++));
				}

				String v1 = var.toString();
				if (v1.equals("print")) {
					myTokens.add(new Token('p', v1));
					continue;
				}
				myTokens.add(new Token('i', v1));
				continue;
			}

			// ***3.operator
			if (op.contains(String.valueOf(sentence.charAt(a)))) {
				if (sentence.charAt(a) == '=') {
					myTokens.add(new Token('e', "="));
					continue;
				}
				if (sentence.charAt(a) == '(') {
					myTokens.add(new Token('l', "("));
					continue;
				}
				if (sentence.charAt(a) == ')') {
					myTokens.add(new Token('r', ")"));
					continue;
				}
				myTokens.add(new Token('o', sentence.substring(a, a + 1)));
				continue;
			}

			// ***不在范围内符号：异常处理
			throw new TokenException("unexpected token \"" + sentence.substring(a, a + 1) + "\"", lineCounter, a);
		} // the end of loop.
		return myTokens;
	}// the end of getLexicalUnit.

	
	// method2: 语法分析
	// 1.赋值
	// ****计算part：建立语法树
	// ****变量part：binding
	// 2.print()
	// ****内部计算
	// ****输出
	public String Analysis(Vector<Token> myTokens) throws TokenException, IOException {
		final int size = myTokens.size();

		// ***空句,过短句
		if (size == 0)
			return "";
		if (size < 3) {
			throw new TokenException("the sentence is incomplete", lineCounter, 0);
		}

		// ***【1】赋值操作
		// ******** 标志符开头 + ‘=’
		// ******** 确定类型：int 还是 double
		if (myTokens.get(0).type == 'i') {
			if (myTokens.get(1).type != 'e')
				throw new TokenException("unexpected token", lineCounter, 1);

			Vector<Token> formula = new Vector<Token>(myTokens.size());
			formula.setSize(myTokens.size());
			Collections.copy(formula, myTokens);
			formula.remove(1);
			formula.remove(0);

			myNumber value = calculator(formula, 2);
			identifiers.put(myTokens.get(0).symbol, value);
			return myTokens.get(0).symbol;
		}

		// ****【2】print()操作
		if (myTokens.get(0).type == 'p') {
			if (size < 4 || myTokens.get(size - 1).type != 'r')
				throw new TokenException("unexpected token", lineCounter, size - 1);
			else if (myTokens.get(1).type != 'l')
				throw new TokenException("unexpected token", lineCounter, 1);

			Vector<Token> formula = new Vector<Token>(size);
			formula.setSize(myTokens.size());
			Collections.copy(formula, myTokens);
			formula.remove(size - 1);
			formula.remove(1);
			formula.remove(0);

			myNumber value = calculator(formula, 2);
			double val = value.dVal;
			if (value.type == 1)
				val = (double) value.iVal;
			Writer wr = new calculatorEngineFactory().getScriptEngine().getContext().getWriter();
			wr.write(val + "\n");
			return "";
		}

		// ****异常处理
		throw new TokenException("unecpected token", lineCounter, 0);
	}// the end of Analysis

	
	// helperFunction: 建立语法树 & 生成三段地址代码
	// ***1.算符优先分析、递归下降
	// ***2.二叉树??->栈
	// ***
	public static myNumber calculator(Vector<Token> formula, Integer pos) throws TokenException {
		// **算式合法性检查**//
		Boolean nextToBeAmt = true;
		int i = 0, leftPrt = 0, rightPrt = 0;
		while (i < formula.size()) {
			if (formula.get(i).type == 'l') {
				i++;
				leftPrt++;
				nextToBeAmt = true;
				continue;
			}
			if (formula.get(i).type == 'i' || formula.get(i).type == 'c') {
				if (!nextToBeAmt)
					throw new TokenException("the expression is invalid", lineCounter, i + pos);
				i++;
				nextToBeAmt = false;
				continue;
			}
			if (formula.get(i).type == 'o') {
				if (nextToBeAmt)
					throw new TokenException("the expression is invalid", lineCounter, i + pos);
				i++;
				nextToBeAmt = true;
				continue;
			}
			if (formula.get(i).type == 'r') {
				if (nextToBeAmt)
					throw new TokenException("the expression is invalid", lineCounter, i + pos);
				i++;
				rightPrt++;
				nextToBeAmt = false;
				continue;
			}
		}
		if (nextToBeAmt)
			throw new TokenException("unexpected end of the expression", lineCounter, i + pos);
		if (leftPrt != rightPrt)
			throw new TokenException("the parentheses does not match", lineCounter, i + pos);

		// **stack->计算器**//
		// 利用栈
		Stack<myNumber> numStack = new Stack<myNumber>();
		Stack<Operator> opStack = new Stack<Operator>();
		i = 0;

		while (i < formula.size()) {
			Token token = formula.get(i);
			if (token.type == 'l') {
				opStack.push(new Operator("(", 3));
				i++;
				continue;
			}
			if (token.type == 'i') {
				myNumber num = identifiers.get(token.symbol);
				if (num != null) {
					i++;
					numStack.push(num);
				} else
					throw new TokenException("undefined identifier: " + token.symbol, lineCounter, i + pos);
				continue;
			}
			if (token.type == 'c') {
				numStack.push(token.num);
				i++;
				continue;
			}

			if (token.type == 'o') {
				i++;
				final String curOp = token.symbol;
				Integer curPty;
				if (curOp.equals("+") || curOp.equals("-"))
					curPty = 1;
				else
					curPty = 2;

				if (opStack.empty() || opStack.peek().priority == 3) {
					opStack.push(new Operator(curOp, curPty));
					continue;
				}

				final Integer prePty = opStack.peek().priority;
				final String preOp = opStack.peek().symbol;
				if (curPty > prePty)
					opStack.push(new Operator(curOp, curPty));
				else {
					myNumber num2 = numStack.pop();
					myNumber num1 = numStack.pop();
					opStack.pop();

					numStack.push(num2.DoOperation(num1, preOp, lineCounter, pos, i));
					opStack.push(new Operator(curOp, curPty));
				}
				continue;
			} // the end of if token = '+-*/'

			if (token.type == 'r') {
				while (!opStack.empty() && opStack.peek().symbol != "(") {
					final String op = opStack.pop().symbol;
					final myNumber num2 = numStack.pop();
					final myNumber num1 = numStack.pop();
					numStack.push(num2.DoOperation(num1, op, lineCounter, pos, i));
				}
				if (opStack.empty())
					throw new TokenException("unexpected right parenthesis ')'", lineCounter, i + pos);
				else {
					opStack.pop();
					i++;
					continue;
				}
			} // the end of if token = ')'
		}
		while (!opStack.empty()) {
			String op = opStack.pop().symbol;
			myNumber num2 = numStack.pop();
			myNumber num1 = numStack.pop();
			numStack.push(num2.DoOperation(num1, op, lineCounter, pos, i));
		}
		return numStack.pop();
	}// the end of Calculator
}

class Operator {
	public String symbol;
	public Integer priority;

	public Operator(String ele, Integer pri) {
		symbol = ele;
		priority = pri;
	}
}

class myNumber {
	public int type;// 1->integer 2->double
	public int iVal;
	public double dVal;

	public myNumber(Integer iv) {
		iVal = iv;
		type = 1;
	}

	public myNumber(Double dv) {
		dVal = dv;
		type = 2;
	}

	public myNumber DoOperation(myNumber a, String op, int stc, int pos, int i) throws TokenException {
		if (a.type == 1 && this.type == 1) {
			int temp = 0;
			switch (op) {
			case "+":
				temp = a.iVal + this.iVal;
				break;
			case "-":
				temp = a.iVal - this.iVal;
				break;
			case "*":
				temp = a.iVal * this.iVal;
				break;
			case "/":
				if (Math.abs(this.iVal) < 1e-9)
					throw new TokenException("Divided by zero", stc, pos + i);
				temp = a.iVal / this.iVal;
				break;
			}
			return new myNumber(temp);
		} else {
			double temp = 0.0;
			switch (op) {
			case "+":
				temp = a.dVal + this.dVal;
				break;
			case "-":
				temp = a.dVal - this.dVal;
				break;
			case "*":
				temp = a.dVal * this.dVal;
				break;
			case "/":
				if (Math.abs(this.dVal) < 1e-9)
					throw new TokenException("divided by zero", stc, pos + i);
				temp = a.dVal / this.dVal;
				break;
			}
			return new myNumber(temp);
		}

	}// end of DoOperation
}

class Token {
	public char type; // 0.标志符 1.const 2.+ - * ／ 3.print 4.= 5.( 6.)
	public String symbol; // id, op, lp, rp, semicolon
	public myNumber num;

	public Token(char iType, String strSym) {
		this(iType, strSym, null);
	}

	public Token(char iType, String strSym, myNumber ele) {
		type = iType;
		symbol = strSym;
		num = ele;
	}
}
