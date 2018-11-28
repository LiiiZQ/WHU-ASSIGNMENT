package com;

public class YamlException extends Exception{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private String message;
	private Integer line;
	private Integer position;// 从零开始，算单个字符

	public YamlException(String msg, Integer line, Integer pos) {
		this.message = msg;
		this.line = line;
		this.position = pos;
	}

	public void print() {
		System.out.println("Error(line " + line + ", position " + position + "): " + message);
	}
}
