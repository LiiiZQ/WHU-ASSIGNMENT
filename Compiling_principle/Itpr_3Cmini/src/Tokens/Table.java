//
//  VarTable.java
//  Itpr_3Cmini
//	存放变量表
//
//  Created by Li子青 on 2018/11/13.
//  Copyright © 2018年 Li子青. All rights reserved.
//
package Tokens;

import java.util.Vector;

public class Table {
	private Vector<VarNode> variableTable = new Vector<VarNode>();/* 存放变量 => 符号表 */
	
	
	/*作用域维护*/
	//field越大作用域越小 最外层作用域为0 在当前层和外层寻找变量//当前层的小内层中声明的变量没有关系
	public VarNode getVariable(String name, int field) {
		while (field >= 0) {
			for (VarNode element : variableTable) {
				if (element.getName().equals(name) && element.getField() == field)
					return element;
			}
			field--;
		}
		return null;
	}
	public void cleanLocalVar(int field) {
		for (int i = 0; i < size(); i++)
			if (get(i).getField() > field) remove(i);
	}
	
	
	public boolean add(VarNode element) {
		return variableTable.add(element);
	}

	public void remove(int index) {
		variableTable.remove(index);
	}

	
	
	public VarNode get(int index) { return variableTable.get(index); }	
	public int size() { return variableTable.size(); }
	public void removeAll() { variableTable.clear(); }	
	public void remove(String name, int field) {
		for (int i = 0; i < size(); i++) {
			if (get(i).getName().equals(name) && get(i).getField() == field) {
				remove(i);
				return;
			}
		}
	}

}
