package com;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

//输入格式
//Enter the grammar: G[N]
//Enter VN: N, D(space+逗号)
//Enter production principle: N::=ND|D
//                            D::=0|1|2|3|4|5|6|7|8|9

public class Chomsky {
	/* 需要储存的信息 */
	private char start;// 开始符号
	private int chomNum;// 文法类别，几型文法
	private String grammar;// 文法名
	private ArrayList<Character> VNList = new ArrayList<>();// 非终结符号列表
	private ArrayList<Character> VTList = new ArrayList<>();// 终结符号列表
	private ArrayList<Production> pList = new ArrayList<>();// 产生式列表，包含每一个产生式信息：产生式，产生式左部，产生式右部每一个值
	private final char EMPTY = 'ε';
	private BufferedReader input;	
	
	public boolean SourceCode(String path) {
		try {
			input = new BufferedReader(new FileReader(path));
			// line1
			grammar = input.readLine().trim();
			if (grammar.charAt(1) != '[' || grammar.charAt(3) != ']'){
			//if (grammar.length() != 4 || grammar.charAt(1) != '[' || grammar.charAt(3) != ']') {
				System.out.println("错误：语法名输入格式错误！");
				System.out.println("示例：\"G[N]\"");
				return false;
			}
			if (grammar.charAt(2) >= 'A' && grammar.charAt(2) <= 'Z')
				start = grammar.charAt(2);
			else {
				System.out.println("错误：开始符号无效！");
				return false;
			}

			// line2
			String temp1 = input.readLine().trim();
			String[] vnStr = temp1.split(", ");
			boolean isMatch = false;
			char vn;
			for (int i = 0; i < vnStr.length; i++) {
				if (vnStr[i].length() != 1) {
					System.out.println("错误：非终结符号输入格式错误！");
					System.out.println("示例：\"N, A, S, P\"");
					return false;
				}
				vn = vnStr[i].charAt(0);
				if (vn >= 'A' && vn <= 'Z') {
					if (vn == start)
						isMatch = true;
					/////
					if(VNList.contains((Character)vn)){
						System.out.println("错误：重复输入同一非终结符号！");
					}
					VNList.add(vn);
				} else {
					System.out.println("错误：非终结符号无效！");
					return false;
				}
			}
			if (!isMatch) {
				System.out.println("错误：开始符号不在非终结符号集中！");
				return false;
			}
			
			String temp2 = "";
			// line3+
			while (true) {
				temp2 = input.readLine();
				if (temp2 != null) {
					String line = new String(temp2);
					String left = temp2.split("::=")[0];
					for (int i = 0; i < left.length(); i++) {
						if (left.charAt(i) == EMPTY) {
							System.out.println("错误：产生式无效！");
							return false;
						}
						if (!VNList.contains((Character)left.charAt(i)) && 'A' <= left.charAt(i) && 'Z' >= left.charAt(i)) {
							System.out.println("错误：产生式无效！");
							return false;
						}
					}

					String rightStr = temp2.split("::=")[1];
					String[] varStr = rightStr.split("\\|");
					ArrayList<String> vList = new ArrayList<>();
					for (int i = 0; i < varStr.length; i++) {
						String var = varStr[i];
						if(var.length() == 0){
							System.out.println("错误：产生式无效！");
						}
						if (var.length() > 1) {
							for (int j = 0; j < var.length(); j++) {
								if (var.charAt(j) == EMPTY) {
									System.out.println("错误：产生式无效！");
									return false;
								}
							}
						} // 避免如 ‘Abbε’
						vList.add(var);
						//添加终结符号列表
						for (int j = 0; j < var.length(); j++) {
							char n = var.charAt(j);
							if (!VNList.contains((Character) n)
									&& !VTList.contains((Character) n) 
									&& n != EMPTY) {
								VTList.add(var.charAt(j));
							}
						}
					}
					pList.add(new Production(line, left, vList));
				} else
					break;
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return true;
	}

	// 0 α→β (α至少包含一个非终结符号)
	// 1 α→β,|β|>=|α| (α→ε也满足)   A→ε
	// 2 A→β
	// 3 A→α|αB（右线性）或A→α|Bα（左线性）

	// 1.ZERO: left至少包含一个VN
	// 2.ONE: left.length <= right.length
	// 3.TWO: left.length = 1, left属于VN
	// 4.THREE: right.length = 1 -> right属于VT
	// **** right.length =2 -> right[0]属于VT则right[1]属于VN 右线性文法
	// **** 后续length=2时必须...
	// **** 左线性文法类似

	public boolean ZERO() {
		String str = "";
		int j;
		boolean flag = false;
		// 检查每一个产生式左部都至少有一个VN
		for (int i = 0; i < pList.size(); i++) {
			str = pList.get(i).left;
			for (j = 0; j < str.length(); j++) {
				if (str.charAt(j) == start){
					flag = true;
				}
				if (VNList.contains((Character) str.charAt(j))) {
					break;
				}
			}
			if (j == str.length() || !flag) {
				chomNum = -1;
				return false;
			}
		}
		chomNum = 0;
		return true;
	}
	
	public boolean ONE() {
		if (ZERO()) {
			for (int i = 0; i < pList.size(); i++) {
				// 对每个产生式，左部长度要小于右部每一个值
				int leftSize = pList.get(i).left.length();
				for (int j = 0; j < pList.get(i).vList.size(); j++) {
					if (leftSize > pList.get(i).vList.get(j).length()) {
						return false;
					}
				}
			}
		}
		else
			return false;
		chomNum = 1;
		return true;
	}

	public boolean TWO() {
		if (ONE()) {
			for (int i = 0; i < pList.size(); i++) {
				// 对每个产生式，左部为单个非终结符号
				if (pList.get(i).left.length() != 1 || !VNList.contains((Character) pList.get(i).left.charAt(0)))
					return false;
			}
		}
		else
			return false;
		chomNum = 2;
		return true;
	}

	public void THREE() {
		int n = 0;// 0-未出现 1-右线性 2-左线性
		if (TWO()) {
			for (int i = 0; i < pList.size(); i++) {
				for (String var : pList.get(i).vList) {
					if (var.length() == 1 && (VTList.contains((Character) var.charAt(0)) || var.charAt(0) == EMPTY)){
						continue;
					}
					if (var.length() == 2) {
						if (VTList.contains((Character) var.charAt(0)) && VNList.contains((Character) var.charAt(1))) {
							// 右线性文法
							if (n == 2)
								return;
							n = 1;
							continue;
						}
						if (VNList.contains((Character) var.charAt(0)) && VTList.contains((Character) var.charAt(1))) {
							// 左线性文法	
							if (n == 1){
								return;	
							}
							n = 2;
							continue;
						}
					} 
					else{
						return;
					}
				}
			}
			
		}
		else 
			return;
		chomNum = 3;
		return;
	}

	// 输出格式：
	// 文法G[N]=({N,D},{0,1,2,3,4,5,6,7,8,9},P,N)
	// P: N::=ND|D
	// D::=0|1|2|3|4|5|6|7|8|9
	// 该文法是Chomsky2型文法

	public void output() {
		if (chomNum == -1) {
			System.out.println("该文法不是Chomsky文法");
			return;
		}
		String line1 = "文法" + grammar + "=({" + start;
		for (int i = 1; i < VNList.size(); i++) {
			line1 += ",";
			line1 += VNList.get(i);
		}
		line1 += "},{";
		line1 += VTList.get(0);
		for (int i = 1; i < VTList.size(); i++) {
			line1 += ",";
			line1 += VTList.get(i);
		}
		line1 = line1 + "},P," + start + ")";
		System.out.println(line1);
		
		
		System.out.println("P: " + pList.get(0).line);
		for (int i = 1; i < pList.size(); i++) {
			System.out.println("   " + pList.get(i).line);
		}
		System.out.println("该文法是Chomsky" + chomNum + "型文法");
	}
	
	
	
	public static void main(String[] args) {
		try {
			Chomsky chomsky = new Chomsky();
			chomsky.SourceCode(args[0]);
			chomsky.THREE();
			chomsky.output();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}

class Production {
	// 产生式信息：产生式，左部，右部每一个值, 每一个文法可归的类型
	public Production(String line, String l, ArrayList<String> v) {
		this.line = line;
		this.left = l;
		this.vList = v;
	}

	String line;
	String left;
	ArrayList<String> vList;
}
