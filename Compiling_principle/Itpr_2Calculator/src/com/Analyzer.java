package com;

import java.util.ArrayList;
import java.util.Scanner;
import java.util.Stack;

public class Analyzer {
	private String input;
	private ArrayList<String> ntoken;
	private ArrayList<Integer> ntype;
	private ArrayList<String> token;
	private ArrayList<Integer> type;
	private boolean isLexicalError;
	private boolean isSyntaxError;

	public Analyzer(String input) {
		this.input = input;
		this.ntoken = new ArrayList<String>();
		this.ntype = new ArrayList<Integer>();
		this.token = new ArrayList<String>();
		this.type = new ArrayList<Integer>();
		this.isLexicalError = false;
		this.isSyntaxError = false;
	}

	public void getnToken() {
		String str = "";
		boolean isFloat = false;
		boolean isNumber = false;
		if (input == null)
			this.ntoken.add("输入为空");
		for (int i = 0; i < input.length(); i++) {
			if (Character.isDigit(input.charAt(i)) || input.charAt(i) == '.') {
				isNumber = true;
				if (input.charAt(i) == '.') {
					if (isFloat) {
						str = "出错:第" + (ntoken.size() + 1) + "个ntoken浮点数小数点多余";
						break;
					}
					isFloat = true;
				}
				str += input.charAt(i);
			} else if (input.charAt(i) == '+' || input.charAt(i) == '-' || input.charAt(i) == '*'
					|| input.charAt(i) == '/' || input.charAt(i) == '(' || input.charAt(i) == ')') {
				if (isNumber) {
					isFloat = false;
					isNumber = false;
					if (str.charAt(0) == '.' || str.charAt(str.length() - 1) == '.') {
						str = "出错:第" + (ntoken.size() + 1) + "个ntoken出错：浮点数小数点位置不合法";
						break;
					}
					this.ntoken.add(str);
					str = "";
				}
				this.ntoken.add(String.valueOf(input.charAt(i)));
			} else {// 其他情况：报错
				if (isNumber) {
					isFloat = false;
					isNumber = false;
					if (str.charAt(0) == '.' || str.charAt(str.length() - 1) == '.') {
						str = "出错:第" + (ntoken.size() + 1) + "个ntoken浮点数小数点位置不合法";
						break;
					}
					this.ntoken.add(str);
				}
				str = "出错:第" + (ntoken.size() + 1) + "个ntoken无效";
				break;
			}
		}
		if (str != "") {
			if (isNumber) {
				if (str.charAt(0) == '.' || str.charAt(str.length() - 1) == '.') {
					str = "出错:第" + (ntoken.size() + 1) + "个ntoken浮点数小数点位置不合法";
				}
			}
			this.ntoken.add(str);
		}
	}

	// 1.integer 2.float 3.+ 4.- 5.* 6./ 7.( 8.)
	public void setnType() {
		for (int i = 0; i < ntoken.size(); i++) {
			ntype.add(-1);
			try {
				if (Character.isDigit(ntoken.get(i).charAt(0))) {
					if (!ntoken.get(i).contains("."))
						ntype.set(i, 1);
					else
						ntype.set(i, 2);
				} else if (ntoken.get(i).charAt(0) == '+') {
					ntype.set(i, 3);
				} else if (ntoken.get(i).charAt(0) == '-') {
					ntype.set(i, 4);
				} else if (ntoken.get(i).charAt(0) == '*') {
					ntype.set(i, 5);
				} else if (ntoken.get(i).charAt(0) == '/') {
					ntype.set(i, 6);
				} else if (ntoken.get(i).charAt(0) == '(') {
					ntype.set(i, 7);
				} else if (ntoken.get(i).charAt(0) == ')') {
					ntype.set(i, 8);
				} else {
					ntype.set(i, -1);
				}
			} catch (Exception e) {
				e.printStackTrace();
				ntype.set(i, -1);
			}
		}
	}

	/* 功能：为整数/浮点数加上sign */
	// 1.开头出现的数
	// 2.中间出现的数
	public void checkMinus() {
		int i;
		for (i = 0; i < ntype.size(); i++) {
			if (i == 0 && ntype.size() >= 2 && (ntype.get(0) == 3 || ntype.get(0) == 4) // 0位置为‘+’/‘-’
					&& (ntype.get(1) == 1 || ntype.get(1) == 2)) { // 1位置为数字
				// 一开始出现sign -1
				if (ntype.get(1) == 1) {
					this.type.add(1);
					this.token.add(ntoken.get(0) + ntoken.get(1));
				}
				if (ntype.get(1) == 2) {
					this.type.add(2);
					this.token.add(ntoken.get(0) + ntoken.get(1));
				}
				i++;
			} else if ((i + 2) < ntype.size() && ntype.get(i) == 7 // i位置为‘(’
					&& (ntype.get(i + 1) == 3 || ntype.get(i + 1) == 4)
					&& (ntype.get(i + 2) == 1 || ntype.get(i + 2) == 2)) {
				// 中间出现sign (+1
				this.type.add(7);
				this.token.add("(");
				if (ntype.get(i + 2) == 1) {
					this.type.add(1);
					this.token.add(ntoken.get(i + 1) + ntoken.get(i + 2));
				}
				if (ntype.get(i + 2) == 2) {
					this.type.add(2);
					this.token.add(ntoken.get(i + 1) + ntoken.get(i + 2));
				}
				i += 2;
			} else {
				this.type.add(ntype.get(i));
				this.token.add(ntoken.get(i));
			}
		}
	}

	public String printLexicalAnalysis() {
		getnToken();
		setnType();
		checkMinus();
		String str = "------词法分析开始------\n";
		try {
			for (int i = 0; i < token.size() && !isLexicalError; i++) {
				str += token.get(i) + ": ";
				switch (type.get(i)) {
				case 1:
					str += "整数";
					break;
				case 2:
					str += "浮点数";
					break;
				case 3:
					str += "算术运算符";
					break;
				case 4:
					str += "算术运算符";
					break;
				case 5:
					str += "算术运算符";
					break;
				case 6:
					str += "算术运算符";
					break;
				case 7:
					str += "括号";
					break;
				case 8:
					str += "括号";
					break;
				default:
					str += "未识别" + token.get(i) + "\n【提示：词法分析中断，请检查后重新输入】";
					this.isLexicalError = true;
					break;
				}
				str += "\n";
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		if (!isLexicalError) {
			str += "词法正确\n";
			str += "------词法分析结束------\n\n";
		}
		return str;
	}

	int curr = 0;
	int theType;

	// 获得下一个token的种类编码
	public void Next() {
		if (curr < type.size())
			theType = type.get(curr++);
		else
			theType = 0;
	}

	public void E() {
		T();
		while (theType == 3 || theType == 4) {
			Next();
			T();
		}

	}

	public void T() {
		F();
		while (theType == 5 || theType == 6) {
			Next();
			F();
		}
	}

	public void F() {
		if (theType == 1 || theType == 2)
			Next();
		else if (theType == 7) {
			Next();
			E();
			if (theType == 8)
				Next();
			else {
				isSyntaxError = true;
				System.out.println("出错:第" + curr + "个词法单元错误");
			}
		} else {
			isSyntaxError = true;
			System.out.println("出错:第" + curr + "个词法单元错误");
		}
	}

	public String printSyntaxAnalysis() {
		if (isLexicalError) {
			return "【提示：词法分析错误，无法进行语法分析】";
		}
		String str = "";

		Next();
		E();
		if (theType == 0 && !isSyntaxError) {
			str = "------语法检查开始------\n\n";
			str += "语法正确\n\n";
			str += "------语法检查结束------\n";
		} else {
			if (theType != 0) {
				str = str + "出错：第" + curr + "个词法单元错误";
			}
			str += "【提示：语法检查错误，分析停止】\n\n";
		}
		return str;
	}

	/*private int priority(int type) {
		switch (type) {
		case 5:
		case 6:
			return 2;
		case 3:
		case 4:
			return 1;
		}
		return 0;
	}
	1+(-1)
	-1+1
	-11+
	
	// 变成后缀 2*(3+1) 231+* 
	public String getPostfix() throws Exception {
		Stack st = new Stack();
		String postFix = "";
		for (int j = 0; j < type.size() && !isSyntaxError; j++) {
			if (type.get(j) == 7) { //(
				st.push(j);
			} else if (type.get(j) == 8) { //)
				String thepop = String.valueOf((Integer)st.pop());
				while (type.get(Integer.parseInt(thepop)) != 7) {
					postFix += thepop;
					thepop = String.valueOf((Integer)st.pop());
				}
			} else if (type.get(j) > 2 && type.get(j) < 7) { //+-*'/'
				if (!st.isEmpty()) {
					String thepop = String.valueOf((Integer)st.pop());
					while (thepop != null && priority(type.get(Integer.parseInt(thepop))) > priority(type.get(j))) {
						postFix += thepop;
						thepop = String.valueOf((Integer)st.pop());
					}
					if (thepop != null) {
						thepop = String.valueOf((Integer)st.pop());;
						st.push(thepop);
					}
				}
				st.push(j);
			} else { //数字
				postFix += j;
			}

		}
		while (!st.isEmpty()) {
			postFix += st.pop().toString();
		}
		return postFix;
	}


	public double Calculate(String postFix) throws Exception {
		Stack st = new Stack();// 操作数栈
		for (int i = 0; i < postFix.length(); i++) {
			int cur = Integer.parseInt(String.valueOf(postFix.charAt(i)));//顺序号
			if (type.get(cur) > 2 && type.get(cur) < 7) {
				double d2 = Double.valueOf((String)st.pop());
				double d1 = Double.valueOf((String)st.pop());
				double d3 = 0;
				switch (type.get(cur)) {
				case 3: //'+'
					d3 = d1 + d2;
					break;
				case 4: //'-'
					d3 = d1 - d2;
					break;
				case 5: //'*'
					d3 = d1 * d2;
					break;
				case 6: //'/'
					d3 = d1 / d2;
					break;
				default:
					break;
				}
				st.push(d3); // 将操作后的结果入栈
			} else { // 当是操作数时，就直接进操作数栈
				st.push(token.get(cur)); 
			}

		}

		return (double) st.pop();
	}
	*/

	
	public static void main(String[] args) {
		try {
			while (true) {
				System.out.println("输入表达式或 \"q\" 退出");
				Scanner scanner = new Scanner(System.in);
				String input = scanner.nextLine();
				if (input.equals("q"))
					break;
				Analyzer analyser = new Analyzer(input);
				System.out.println(analyser.printLexicalAnalysis());
				System.out.println(analyser.printSyntaxAnalysis());
				//String postfix = analyser.getPostfix();
				//System.out.println("计算结果为: " + analyser.Calculate(postfix) + "\n\n");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
