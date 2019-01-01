//
//  Token.java
//  Itpr_3Cmini
//	词法分析时存放词法单元
//
//  Created by Li子青 on 2018/10/19.
//  Copyright © 2018年 Li子青. All rights reserved.
//
package Tokens;
/*
 * For lexical analyzer.
 */
public class Token {
    private int lineNo;
    private int column;
    //1.integer 2.real 4.string //6.identifier 10.operator 11.delimiter 12.keyword 
    private int type;
    private String value;
    
    public Token(int l, int c, int t, String v) {
        this.lineNo = l; this.column = c; this.type = t; this.value = v;
    }
    public int getLineNo() { return this.lineNo; }
    public int getColumn() { return this.column; }
    public int getType() { return this.type; }
    public String getValue() { return this.value; }
}