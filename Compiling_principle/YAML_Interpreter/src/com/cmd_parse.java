package com;

import java.io.File;

public class cmd_parse {
	public static void main(String[] args) {
		String filepath = "";
		String instruction = "";
		if (args.length == 1) {
			filepath = args[0];
		} 
		else if (args.length == 2) {
			if (args[0].equals("-parse"))
				filepath = args[1];
			else if (args[0].equals("-json")) {
				filepath = args[1];
				System.out.println("this function haven't been realized!");

			} 
			else {
				System.out.println("yamlite [option [value]] file");
				System.exit(0);
			}
		} 
		else if (args.length == 3) {
			if (args[0].equals("-find")) {
				instruction = args[1];
				filepath = args[2];
			} else {
				System.out.println("yamlite [option [value]] file");
				System.exit(0);
			}
		}
		else {
			System.out.println("yamlite [option [value]] file");
			System.exit(0);
		}
		if (!args[0].equals("-json")) {
			File file = new File(filepath);
			if (!file.isFile() || !file.exists()) {
				System.out.println("No file exists!");
				System.exit(0);
			} 
			else {
				YAMLite lexer = new YAMLite(filepath);
				try {
					lexer.GetLexicalUnit(line, myToken);
					if (args.length == 1 || args[0].equals("-parse")) {
						System.out.println("valid");
					}
					String string = "string";
					if (instruction != "") {
						lexer.find(instruction);
					}

				} catch (YamlException e) {
					e.printStackTrace();
				}
			}
		}
	}
}
