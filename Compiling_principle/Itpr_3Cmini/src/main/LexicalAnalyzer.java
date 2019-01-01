//
// LexicalAnalyzer.java
// Itpr_3Cmini
// 语法分析器
//
// Created by Li子青 on 2018/10/19.
// Copyright © 2018年 Li子青. All rights reserved.
//
package main;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import Tokens.*;

public class LexicalAnalyzer {
	private BufferedReader reader; // 编译器输入->Cmini源码
	private ArrayList<Token> tokens; // 【target】词法单元
	private String error;
	private int errCount;// 【target2】Cmini报错
	private TreeNode root;// 【target3】词法树=>用于输出词法分析结果
	private ArrayList<Token> displayTokens;// 【target4】设置编译器字体,除了多出注释、“”的部分其他都和tokens一样

	private boolean isAnnotation;// 当前是否在多行注释中

	/* 封装 */
	public ArrayList<Token> getTokens() {
		return tokens;
	}

	public String getError() {
		return this.error;
	}

	public int getErrCount() {
		return this.errCount;
	}

	public TreeNode getRoot() {
		return this.root;
	}

	public ArrayList<Token> getDisplayTokens() {
		return displayTokens;
	}

	public LexicalAnalyzer(String codeText) {
		StringReader strReader = new StringReader(codeText);
		reader = new BufferedReader(strReader);
		this.tokens = new ArrayList<Token>();
		this.error = "";
		this.errCount = 0;
		this.root = new TreeNode("SourceCode");
		this.displayTokens = new ArrayList<Token>();

		this.isAnnotation = false;

		process();
	}
	// 每一个新的程序一定要刷新一下

	public void process() {
		String line = "";
		int lineNo = 1;

		while (line != null) {
			try {
				line = reader.readLine();
			} catch (IOException e) {
				// 控制台报错，不属于Cmini错误
				e.printStackTrace();
				System.err.println("【error】Exception occurred when read the source code!");
			}
			// 对多行注释处理 && 词法分析输出部分仍然输出
			if (line != null) {
				line += "\n";
				if (isAnnotation && !line.contains("*/")) {
					String content = "Line" + lineNo + ":" + line;
					TreeNode temp = new TreeNode(content);
					temp.add(new TreeNode("Comment"));
					root.add(temp);
					displayTokens.add(new Token(lineNo, 1, TYPE.COMMENT, line.substring(0, line.length() - 1)));
					displayTokens.add(new Token(lineNo, line.length() - 1, TYPE.NL, "\n"));
					lineNo++;
					continue;
				} else
					root.add(scanLine(line, lineNo));
			}
			lineNo++;// 可能出现空行
		}
	}

	// method: 扫描一行，逐个读取当前行字符，进行分析，如果不能判定，向前多看k位，获得词法单元
	// 可能出现的错误
	// *** 1.标志符：不合法标志符 (1)以数字开头
	// *** 2.数字：不合法整数、不合法实数
	// *** 3.字符串""不成对
	// *** 4.不合法字符
	private TreeNode scanLine(String line, int lineNo) {
		String content = "Line" + lineNo + ":" + line;
		TreeNode node = new TreeNode(content);
		int length = line.length();

		// 0.无 1.* 2./ 3.= 4.< 5.letter 6.digit 7."
		int state = 0; // 会有二义性的前k位信息

		// (截取用)1.标识符.. 2.数字.. 3.字符串
		int begin = 0; // 从0开始
		int end = 0;

		for (int i = 0; i < length; i++) {
			char ch = line.charAt(i);

			if (isAnnotation) {
				// 处理多行注释结尾部分 => e.g." /*...*/ int a =10;"
				if (ch == '*')
					state = 1;
				else if (ch == '/' && state == 1) {
					end = i + 1;
					isAnnotation = false;
					node.add(new TreeNode("Comment"));
					displayTokens.add(new Token(lineNo, begin + 1, TYPE.COMMENT, line.substring(begin, end)));
					begin = 0;
					end = 0;
					state = 0;
				} else
					state = 0;
			} else {// 0.无 1.* 2./ 3.= 4.< 5.letter 6.digit 7."
				if (isValid(ch)) {
					switch (state) {
					case 0:
						if (ch == '(' || ch == ')' || ch == ';' || ch == '{' || ch == '}' || ch == '[' || ch == ']'
								|| ch == ',') {
							node.add(new TreeNode("Delimiter: " + ch));
							Token de = new Token(lineNo, i + 1, TYPE.DELIMITER, String.valueOf(ch));
							tokens.add(de);
							displayTokens.add(de);
						} else if (ch == '+' || ch == '-') {// operator//符号数
							if (!tokens.isEmpty()) {
								int t = tokens.get(tokens.size() - 1).getType();
								String v = tokens.get(tokens.size() - 1).getValue();
								if (t == TYPE.INTEGER || t == TYPE.REAL || t == TYPE.STRING
										|| t == TYPE.IDENTIFIER || v.equals(")") || v.equals("]")) {
									node.add(new TreeNode("Operator：" + ch));
									Token op = new Token(lineNo, i + 1, TYPE.OP, String.valueOf(ch));
									tokens.add(op);
									displayTokens.add(op);
								}
							} else {
								state = 6;
								begin = i;
							}
						} else if (ch == '*' || ch == '>') {
							node.add(new TreeNode("Operator：" + ch));
							Token op = new Token(lineNo, i + 1, TYPE.OP, String.valueOf(ch));
							tokens.add(op);
							displayTokens.add(op);
						} else if (ch == '/')
							state = 2;
						else if (ch == '=')
							state = 3;
						else if (ch == '<')
							state = 4;
						else if (isLetter(ch)) {
							state = 5;
							begin = i;
						} else if (isDigit(ch)) {
							state = 6;
							begin = i;
						} else if (String.valueOf(ch).equals("\"")) {// 双引号"
							state = 7;
							begin = i + 1;
							Token qm = new Token(lineNo, begin, TYPE.DELIMITER, "\"");
							tokens.add(qm);
							displayTokens.add(qm);
							node.add(new TreeNode("Delimiter: " + ch));		
						} else if (String.valueOf(ch).equals(" "))
							displayTokens.add(new Token(lineNo, i + 1, TYPE.SPACE, " "));
						else if (String.valueOf(ch).equals("\n"))
							displayTokens.add(new Token(lineNo, i + 1, TYPE.NL, "\n"));
						else if (String.valueOf(ch).equals("\r"))// 回车符
							displayTokens.add(new Token(lineNo, i + 1, TYPE.RT, "\r"));
						else if (String.valueOf(ch).equals("\t"))// 制表符
							displayTokens.add(new Token(lineNo, i + 1, TYPE.TB, "\t"));
						break;
					case 2: // 1.除号 2./* 3.//
						if (ch == '/') {
							node.add(new TreeNode("Comment"));
							displayTokens.add(new Token(lineNo, i, TYPE.COMMENT, line.substring(i - 1, length - 1)));
							i = length - 2;
							state = 0;
						} else if (ch == '*') {
							node.add(new TreeNode("Comment"));
							isAnnotation = true;
							begin = i - 1;
							state = 0;
						} else {
							node.add(new TreeNode("Operator：/"));
							Token op = new Token(lineNo, i, TYPE.OP, "/");
							tokens.add(op);
							displayTokens.add(op);
							i--;
							state = 0;
						}
						break;
					case 3: // 1.== 2.=
						if (ch == '=') {
							node.add(new TreeNode("Operator：=="));
							Token op = new Token(lineNo, i, TYPE.OP, "==");
							tokens.add(op);
							displayTokens.add(op);
							state = 0;
						} else {
							node.add(new TreeNode("Operator：="));
							Token op = new Token(lineNo, i, TYPE.OP, "=");
							tokens.add(op);
							displayTokens.add(op);
							i--;
							state = 0;
						}
						break;
					case 4: // 1.<> 2.<
						if (ch == '>') {
							node.add(new TreeNode("Operator：<>"));
							Token op = new Token(lineNo, i, TYPE.OP, "<>");
							tokens.add(op);
							displayTokens.add(op);
							state = 0;
						} else {
							node.add(new TreeNode("Operator：<"));
							Token op = new Token(lineNo, i, TYPE.OP, "<");
							tokens.add(op);
							displayTokens.add(op);
							i--;
							state = 0;
						}
						break;
					case 5:// 1.关键字 2.标志符 3.报错
						if (isLetter(ch) || isDigit(ch))
							state = 5;
						else {
							end = i;
							String id = line.substring(begin, end);
							if (isKey(id)) {
								node.add(new TreeNode("Keyword：" + id));
								Token kw = new Token(lineNo, begin + 1, TYPE.KEYW, id);
								tokens.add(kw);
								displayTokens.add(kw);
							} else if (matchID(id)) {
								node.add(new TreeNode("Identifier：" + id));
								Token idt = new Token(lineNo, begin + 1, TYPE.IDENTIFIER, id);
								tokens.add(idt);
								displayTokens.add(idt);
							} else {
								errCount++;
								error += "Error(line " + lineNo + ", position" + (begin + 1) + "): \"" + id
										+ "\" is an invalid identifier!" + "\n";
								node.add(new TreeNode("ERROR：\"" + id + "\" is an invalid identifier."));
								displayTokens.add(new Token(lineNo, begin + 1, TYPE.ERROR, id));
							}
							i--;
							begin = 0;
							end = 0;
							state = 0;
						}
						break;
					case 6:// 1.整数 2.实数
						if (isDigit(ch) || String.valueOf(ch).equals("."))
							state = 6;
						else {
							if (isLetter(ch))
								state = 5;
							else {
								end = i;
								String num = line.substring(begin, end);
								if (!num.contains(".")) {
									if (matchInteger(num)) {
										node.add(new TreeNode("Integer: " + num));
										Token ig = new Token(lineNo, begin + 1, TYPE.INTEGER, num);
										tokens.add(ig);
										displayTokens.add(ig);
									} else {
										errCount++;
										error += "Error(line " + lineNo + ", position" + (begin + 1) + "): \"" + num
												+ "\" is an invalid integer!" + "\n";
										node.add(new TreeNode("ERROR：\"" + num + "\" is an invalid integer."));
										displayTokens.add(new Token(lineNo, begin + 1, TYPE.ERROR, num));
									}
								} else {
									if (matchReal(num)) {
										node.add(new TreeNode("Real: " + num));
										Token re = new Token(lineNo, begin + 1, TYPE.REAL, num);
										tokens.add(re);
										displayTokens.add(re);
									} else {
										errCount++;
										error += "Error(line " + lineNo + ", position" + (begin + 1) + "): \"" + num
												+ "\" is an invalid real number!" + "\n";
										node.add(new TreeNode("ERROR：\"" + num + "\" is an invalid real number."));
										displayTokens.add(new Token(lineNo, begin + 1, TYPE.ERROR, num));
									}
								}
								i--;
								begin = 0;
								end = 0;
								state = 0;
							}
						}
						break;
					case 7:
						if (ch == '"') {
							end = i;
							String str = line.substring(begin, end);
							node.add(new TreeNode("String：" + str));
							Token st = new Token(lineNo, begin, TYPE.STRING, str);
							tokens.add(st);
							displayTokens.add(st);

							Token qm = new Token(lineNo, i + 1, TYPE.DELIMITER, "\"");
							tokens.add(qm);
							displayTokens.add(qm);
							node.add(new TreeNode("Delimiter：" + ch));
							begin = 0;
							end = 0;
							state = 0;
						} else if (i == length - 1) {
							String str = line.substring(begin, length - 1);
							errCount++;
							error += "Error(line " + lineNo + ", position" + (begin + 1) + "): String \"" + str
									+ "\" lacks quotation marks！" + "\n";
							node.add(new TreeNode("ERROR: String \"" + str + "\" lacks quotation marks."));
							displayTokens.add(new Token(lineNo, i + 1, TYPE.ERROR, str));
						}
					}
				} else {
					errCount++;
					error += "Error(line " + lineNo + ", position" + (i + 1) + "): Symbol \'" + ch
							+ "\' is an invalid symbol." + "\n";
					node.add(new TreeNode("ERROR: \'" + ch + "\' is an invalid symbol"));
					if (state == 0)
						displayTokens.add(new Token(lineNo, i + 1, TYPE.ERROR, String.valueOf(ch)));
				}
			}
		}
		return node;
	}

	/* helper function */
	private static boolean isLetter(char c) {
		return ((c >= 'a' && c <= 'z') || (c >= 'A' && c <= 'Z') || c == '_');
	}

	private static boolean isDigit(char c) {
		return (c >= '0' && c <= '9');
	}

	private static boolean isValid(char ch) {
		if (ch == '(' || ch == ')' || ch == ';' || ch == '{' || ch == '}' || ch == '[' || ch == ']' || ch == ','
				|| ch == '+' || ch == '-' || ch == '*' || ch == '/' || ch == '=' || ch == '<' || ch == '>' || ch == '"'
				|| ch == '.' || ch == '!' || isLetter(ch) || isDigit(ch) || String.valueOf(ch).equals(" ")
				|| String.valueOf(ch).equals("\n") || String.valueOf(ch).equals("\r")
				|| String.valueOf(ch).equals("\t"))
			return true;
		return false;
	}

	private static boolean matchInteger(String input) {
		return input.matches("^-?\\d+$") && !input.matches("^-?0{1,}\\d+$");
	}

	private static boolean matchReal(String input) {
		return input.matches("^(-?\\d+)(\\.\\d+)+$") && !input.matches("^(-?0{1,}\\d+)(\\.\\d+)+$");
	}

	private static boolean matchID(String input) {
		return input.matches("^\\w+$") && !input.endsWith("_") && input.substring(0, 1).matches("[A-Za-z]");
	}

	private static boolean isKey(String str) {
		if (str.equals(KEYWORD.IF) || str.equals(KEYWORD.ELSE) || str.equals(KEYWORD.WHILE) //|| str.equals(KEYWORD.READ)
				|| str.equals(KEYWORD.WRITE) || str.equals(KEYWORD.INT) || str.equals(KEYWORD.REAL)
				|| str.equals(KEYWORD.BOOL) || str.equals(KEYWORD.STRING) || str.equals(KEYWORD.TRUE)
				|| str.equals(KEYWORD.FALSE) || str.equals(KEYWORD.FOR))
			return true;
		return false;
	}
}