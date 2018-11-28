package com;

public class Token {
    int indent_num;
    int line_num;
    int type;// 1.key_value  2.value  3.key  4.sign
    String keyName;
    String value;
    int value_type;//0.value值为空 1.int 2.float 3.String 4.boolean 5.科学计数法
    
    Token(int indent, int type, int ln_num) {
        this(indent, 4, null, null, 0, ln_num);
    }
    Token(int indent, int type, String keyVal, String val, int val_t, int ln_num) {
        this.indent_num = indent;
        this.type = type;
        this.keyName = keyVal;
        this.value = val;
        this.value_type = val_t;
        this.line_num = ln_num;
    }
    
    public int getIndent_num() {
		return indent_num;
	}
    public int getType() {
		return type;
	}
    public String getKeyName() {
		return keyName;
	}
    public String getValue() {
		return value;
	}
    public int getValue_type() {
		return value_type;
	}
    public int getLine_num() {
		return line_num;
	}
};

class Line {
	private int line_num;
	private String sentence;
	private int size;
	public Line(int ln, String stc, int size){
		this.line_num = ln;
		this.sentence = stc;
		this.size = size;
	}
	
	public int getLine_num() {
		return line_num;
	}
	public String getSentence() {
		return sentence;
	}
	public int getSize() {
		return size;
	}
}


