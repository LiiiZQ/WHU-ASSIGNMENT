//
//  VarNode.java
//  Itpr_3Cmini
//	语义分析时存放变量
//
//  Created by Li子青 on 2018/11/13.
//  Copyright © 2018年 Li子青. All rights reserved.
//
package Tokens;

public class VarNode {
	private int type;
	private String name;// 变量标识符	
	private int field;	// 作用域：field越高、作用域越小
	//1.integer=>自定
	//2.real=>自定
	//3.bool=> true/false
	//4.string=> 自定
	private String intValue;		//元素的整形数值
	private String realValue;		//元素的浮点型数值
	private String stringValue;		//元素的字符串值
	private int arrayElementsNum;	//0-不是数组,1~n-数组的大小
	
	public VarNode(int t, String n, int field) {
		this.type = t;
		this.name = n;
		this.field = field;
		
		this.intValue = "";
		this.realValue = "";
		this.stringValue = "";
		this.arrayElementsNum = 0;
	}
	
	
	/* 封装 */
	public int getType() { return this.type; }
	public String getName() { return this.name; }
	public int getField() { return this.field; }
	public String getIntValue() { return this.intValue; }
	public void setIntValue(String intValue) { this.intValue = intValue; }
	public String getRealValue() { return this.realValue; }
	public void setRealValue(String realValue) { this.realValue = realValue; }
	public String getStringValue() { return this.stringValue; }
	public void setStringValue(String stringValue) { this.stringValue = stringValue; }
	public int getArrayElementsNum() { return this.arrayElementsNum; } 
	public void setArrayElementsNum(int arrayElementsNum) { this.arrayElementsNum = arrayElementsNum; }
	
	public boolean equals(Object object) {
		VarNode element = (VarNode)object;
		return this.toString().equals(element.toString());
	}

}
