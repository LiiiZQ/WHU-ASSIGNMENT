//
//  TreeNode.java
//  Itpr_3Cmini
//	分析时语法树使用，*辅助节点
//
//  Created by Li子青 on 2018/10/19.
//  Copyright © 2018年 Li子青. All rights reserved.
//
package Tokens;

import javax.swing.tree.DefaultMutableTreeNode;

//n元树
//语法识别阶段
public class TreeNode extends DefaultMutableTreeNode {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private int lineNo = -1; // 当前结点所在行号
	private int type = -1; // 当前结点类型
	// 1.integer=>自定
	// 2.real=>自定
	// 3.bool=> true/false
	// 4.string=> 自定
	// 5.dataType=>int, bool, real, string
	// 6.identifier=>自定
	// 7.arrayNode=>自定(id)
	// 8. ath-op => + - * / =
	// 9. cpn-op => == > < <>
	private String value = ""; // 当前结点内容

	public final static String STATEMENTS = "statements";	// statements
	public final static String DECLARATION = "declaration";	// declaration
	public final static String ASSIGNMENT = "assignment";	// assignment
	public final static String FOR = KEYWORD.FOR;			// for
	public final static String INIT = "initialization";		// initialization
	public final static String JUDGE = "judgement"; 		// judgement
	public final static String CHANGE = "change";			// change
	public final static String IF = KEYWORD.IF;				// if
	public final static String ELSE = KEYWORD.ELSE;			// else
	public final static String WHILE = KEYWORD.WHILE;		// while
	public final static String WRITE = KEYWORD.WRITE;		// write
	public final static String EMPTY = "empty";				// emptyStatement

	/* 封装 */
	public int getType() {
		return this.type;
	}

	public int getLineNo() {
		return lineNo;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String d) {
		this.value = d;
	}

	// 语法分析 行号,类型,值
	// 叶子结点
	public TreeNode(int l, int t, String d) {
		super(d);
		this.lineNo = l;
		this.type = t;
		this.value = d;
	}

	// 树结点
	public TreeNode(int l, String d) {
		super(d);
		this.lineNo = l;
		this.value = d;
	}

	// 给词法部分用
	public TreeNode(String d) {
		super(d);
		this.value = d;
	}

	/* 功能 */
	public void add(TreeNode childNode) {
		super.add(childNode);
	}

	public TreeNode getChildAt(int index) {
		return (TreeNode) super.getChildAt(index);
	}
}
