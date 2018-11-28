import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Vector;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.lang.Character;

public class Lexer {
	private SourceCode code;
	private ArrayList<TokenNode> TokenList;
	private String currentLine;
	private String TokenFather = "";
	private int arraynum = 0;
	private int arraynum2 = -2;
	public static final String[] SET_VALUE_FOR_KEY = new String[] { "a", "b", "c", "d", "e", "f", "g", "h", "i", "j",
			"k", "l", "m", "n", "o", "p", "q", "r", "s", "t", "u", "v", "w", "x", "y", "z", "A", "B", "C", "D", "E",
			"F", "G", "H", "I", "J", "K", "L", "M", "N", "O", "P", "Q", "R", "S", "T", "U", "V", "W", "X", "Y", "Z",
			"0", "1", "2", "3", "4", "5", "6", "7", "8", "9", "_" };
	public static final String[] Quotaiton = new String[] { "\"" };
	public static final HashSet<String> keyletters = new HashSet<String>(Arrays.asList(SET_VALUE_FOR_KEY));

	public Lexer(String path) {
		code = new SourceCode(path);
		TokenList = new ArrayList<TokenNode>();
	}

	// **** helper function ****
	// 帮助find取得string的有效部分
	public String Value(String value) {
		if (value.startsWith("\"")) {
			value = value.substring(1);
			int index = value.indexOf("\"");
			value = value.substring(0, index);
		}
		return value;
	}

	// 检查value字符串是否合法
	// "int"指示value类型，其中不合法为-1
	// (1)int (2)float (3)String (4)boolean (5)科学计数
	private int CheckValue(String value) {
		int type = -1;
		boolean isSci = false;
		boolean isDouble = false;
		int size = value.length();

		if (value.startsWith("\"")) {
			type = 3;// String
		} else if (value.charAt(0) == 't' || value.charAt(0) == 'f') {
			if (value.equals("true") || value.equals("false"))
				type = 4;// boolean
		} else if (value.charAt(0) == '-' || (Character.isDigit(value.charAt(0)) && value.charAt(0) != '0')) {
			// 除数字以外符号：".","e","E","-"
			for (int i = 1; i < size; i++) {
				if (value.charAt(i) == '.') {
					if (isDouble || isSci)
						return -1;
					isDouble = true;
					continue;
				}
				if (value.charAt(i) == 'e' || value.charAt(i) == 'E') {
					if (i == size - 1 || isSci)
						return -1;
					isSci = true;
					i++;
					if (value.charAt(i) == '-' || Character.isDigit(value.charAt(i)))
						continue;
					else
						return -1;
				}
				if (!Character.isDigit(value.charAt(i)))
					return -1;

			}
			if (isSci)
				type = 5;// 科学
			else if (isDouble)
				type = 2;// double
			else
				type = 1;// int
		}
		return type;// 如果type没能得到更新则为-1无效值
	}

	// (不在数组中)kv类型，放入Tokenlist中
	public void put_kv(int type, Token cur_token, int indent) {
		switch (type) {
		case 1:// int
			int invalue = Integer.parseInt(cur_token.getValue());
			if (indent == 0)
				TokenList.add(new TokenNode(TokenType.KEYVALUE, cur_token.getKeyName(), invalue));
			else if (indent == 2) {
				TokenList.add(
						new TokenNode(TokenType.ARRAYCHILD, cur_token.getKeyName(), invalue, TokenFather, arraynum));
				arraynum++;
			} else if (indent == 3) {
				arraynum2++;
				TokenList.add(new TokenNode(TokenType.ARRAYCHILD, cur_token.getKeyName(), invalue, TokenFather,
						arraynum, arraynum2));
			}
			break;
		case 2:// double
			double dbvalue = Double.parseDouble(cur_token.getValue());
			if (indent == 0)
				TokenList.add(new TokenNode(TokenType.KEYVALUE, cur_token.getKeyName(), dbvalue));
			else if (indent == 2) {
				TokenList.add(
						new TokenNode(TokenType.ARRAYCHILD, cur_token.getKeyName(), dbvalue, TokenFather, arraynum));
				arraynum++;
			} else if (indent == 3) {
				arraynum2++;
				TokenList.add(new TokenNode(TokenType.ARRAYCHILD, cur_token.getKeyName(), dbvalue, TokenFather,
						arraynum, arraynum2));
			}
			break;
		case 3:// String
			if (indent == 0)
				TokenList.add(new TokenNode(TokenType.KEYVALUE, cur_token.getKeyName(), cur_token.getValue()));
			else if (indent == 2) {
				TokenList.add(new TokenNode(TokenType.ARRAYCHILD, cur_token.getKeyName(), cur_token.getValue(),
						TokenFather, arraynum));
				arraynum++;
			} else if (indent == 3) {
				arraynum2++;
				TokenList.add(new TokenNode(TokenType.ARRAYCHILD, cur_token.getKeyName(), cur_token.getValue(),
						TokenFather, arraynum, arraynum2));
			}
			break;
		case 4:
			boolean blvalue = Boolean.parseBoolean(cur_token.getValue());
			if (indent == 0)
				TokenList.add(new TokenNode(TokenType.KEYVALUE, cur_token.getKeyName(), blvalue));
			else if (indent == 2) {
				TokenList.add(
						new TokenNode(TokenType.ARRAYCHILD, cur_token.getKeyName(), blvalue, TokenFather, arraynum));
				arraynum++;
			} else if (indent == 3) {
				arraynum2++;
				TokenList.add(new TokenNode(TokenType.ARRAYCHILD, cur_token.getKeyName(), blvalue, TokenFather,
						arraynum, arraynum2));
			}
			break;
		case 5:
			BigDecimal bgvalue = new BigDecimal(cur_token.getValue());
			if (indent == 0)
				TokenList.add(new TokenNode(TokenType.KEYVALUE, cur_token.getKeyName(), bgvalue));
			else if (indent == 2) {
				TokenList.add(
						new TokenNode(TokenType.ARRAYCHILD, cur_token.getKeyName(), bgvalue, TokenFather, arraynum));
				arraynum++;
			} else if (indent == 3) {
				arraynum2++;
				TokenList.add(new TokenNode(TokenType.ARRAYCHILD, cur_token.getKeyName(), bgvalue, TokenFather,
						arraynum, arraynum2));
			}
			break;
		default:
			break;
		}
	}

	// ******** 处理yml文件语句 ********
	// **preprocess:对sentence预处理->变为更易处理的line对象
	// 1.忽略注释:“#“以后无效化
	// 2.忽略结尾的空格
	// 3.检查前面空格段的数量->是否为偶数
	// 4.检查字符间的空格->只有一个space且该space为
	public Line Preprocess(int line_num, String sentence) throws LexicalException {
		int count = 0;
		int length = sentence.length();
		if (length == 0)
			return new Line(line_num, "", 0);
		// 忽略注释
		while (count < length) {
			if (sentence.charAt(count) == '#')
				break;
			count++;
		}
		int space = 0;
		// 忽略该行后的space
		while (count > 0 && Character.isWhitespace(sentence.charAt(count - 1))) {
			space++;
			count--;
		}
		if (count > 0) {
			String temp = sentence.substring(0, count);
			sentence = temp;
		}
		// 检查该行前的indent是否合法(为2的倍数)
		int curr = 0;
		for (; curr < count; curr++) {
			if (sentence.charAt(curr) != ' ') {
				if (curr % 2 != 0)
					throw new LexicalException("Invalid indent.", line_num, curr + 1);
				break;
			}
		}
		// 检查字符间space是否合法("- xxx"或": xxx")
		// ******* 暂待
		// 当value为字符串，字符串中间的space? 检查“"”的话如果字符串中间出现“\"”?
		boolean space_occur = false;
		boolean in_string_val = false;
		int chr_begain = curr;
		for (; curr < count; curr++) {
			if (sentence.charAt(curr) == ' ' && !in_string_val) {
				if (space_occur)
					throw new LexicalException("Unexpected ' '" + count + space, line_num, curr + 1);
				else if (curr == chr_begain + 1) {
					if (sentence.charAt(chr_begain) != '-')
						throw new LexicalException("Expect '-'", line_num, chr_begain + 1);
				} else if (sentence.charAt(curr - 1) != ':')
					throw new LexicalException("Expect ':'", line_num, chr_begain + 1);
				space_occur = true;
			} else if (sentence.charAt(curr) == '\"') {
				if (!in_string_val)
					in_string_val = true;
				else if (curr == count)
					in_string_val = false;
			}
		}
		return new Line(line_num, sentence, count);
	}

	// **method1:GetLexicalUnit 获得词法单元
	// 扫描每一行，一行就是一个元素，把该行按类别放进Token Vector【indent, type, key_str, value_str,
	// value_type, line_num】
	// 类别判断依据:
	// ** 1.indent部分:indent数量->普通元素/有数组等级元素(要求呼应的等级指示)(indent = 2*space)
	// ** 2.归类部分: 判断其类型，并为其赋相应值
	// ** 3.异常检查
	// 字符四个type:
	// ** (1)"key: value"【key_value】: 键值对
	// ** (2)"- value”【value】:数组值
	// ** (3)"key:"【key】: 数组的键<functional>:数组等级加一层
	// ** (4)"-"【sign】:换行指示数组中接下来的值是数组/键值对<functional>:数组等级加一层／下一行kv
	// 可能出现的异常
	// ** key/value名称是否合法
	// ****
	public void GetLexicalUnit(Line line, Vector<Token> myToken) throws LexicalException {
		int size = line.getSize();
		int line_num = line.getLine_num();
		String sentence = line.getSentence();
		int indent = 0;
		int cur_pos = 0;

		if (size > 0) {
			// indent数
			while (cur_pos < size) {
				if (!Character.isWhitespace(sentence.charAt(cur_pos)))
					break;
				cur_pos++;
			}
			if (cur_pos > 0)
				indent = cur_pos / 2;

			int chr_start = cur_pos;
			// 判断类型
			if (sentence.charAt(chr_start) == '-') {
				// type4【sign】(-)
				if (cur_pos == size - 1) {
					myToken.add((new Token(indent, 4, line_num)));
					return;
				}
				// type2【value】(- value)
				else {
					cur_pos++;
					if (!Character.isWhitespace(sentence.charAt(cur_pos))) {
						throw new LexicalException("expect ' '", line_num, cur_pos + 1);
					}
					String str = sentence.substring(++cur_pos, size);
					int val_type = CheckValue(str);
					if (val_type == -1) {
						throw new LexicalException("invaild value", line_num, cur_pos + 1);
					} else {
						myToken.add(new Token(indent, 2, null, str, val_type, line_num));
						return;
					}
				}
			} else if (sentence.contains(":")) {
				String[] separate = sentence.split(":", 2);
				String key_str = separate[0].substring(chr_start);
				String temp = separate[1];

				// 检查key值是否合法
				for (int i = 0; i < key_str.length(); i++) {
					if (!keyletters.contains(String.valueOf(key_str.charAt(i))))
						throw new LexicalException("invalid key:" + key_str + key_str.length(), line_num,
								chr_start + i + 1);
				}
				// type3【key】(key:)
				if (sentence.endsWith(":")) {
					myToken.add(new Token(indent, 3, key_str, null, 0, line_num));
					return;
				} else {// type4【key-value】(key: value)
					int val_start = chr_start + key_str.length() + 2;
					if (!Character.isWhitespace(temp.charAt(0)))
						throw new LexicalException("expected ' '", line_num, val_start);
					String value_str = temp.substring(1);
					int value_type = CheckValue(value_str);
					if (value_type == -1)
						throw new LexicalException("invaild value", line_num, val_start + 1);
					else {
						if (key_str != null && value_str != null) {
							Token token = new Token(indent, 1, key_str, value_str, value_type, line_num);
							myToken.add(token);
						}
						return;
					}
				}
			} else
				throw new LexicalException("the sentence is invalid", line_num, cur_pos + 1);
		}
	}

	// **method2: Analysis语义分析
	// 从纯字面储存模式变成有意义的数据->(1)键值(2)数组
	// 1.检查indent数是否符合数组的等级
	// *** (1)"key: value"【key_value】
	// indent = 0, indent_max = 0;
	// 2 <= indent <= indent_max, preToken为【sign】, indent = preIndent+1,
	// indent_max = indent-1;
	// *** (2)"- value”【value】: 1 ≤ indent ≤ indent_max, indent_max = indent;
	// *** (3)"key:"【key】: indent = 0, indent_max = 1;
	// *** (4)"-"【sign】: 1 ≤ indent ≤ indentMax, indent_max = indent + 1;
	// 2.根据数组等级重新储存(怎么解决数组层级问题)(支持数组为空)
	// *** 1.有意义的量:1、3
	// *** 2.indent亦是数组等级，kv例外
	// 可能出现的异常：
	// *** indent数不符合数组等级关系(把大部分问题都丢给前面了所以这部分也比较轻松)
	public void Analysis(Vector<Token> myTokens) throws LexicalException {
		int line_num;
		int type;
		int indent;
		Token cur_token = null;

		int cur = 0;
		int indent_max = 0;
		int size = myTokens.size();
		while (cur < size) {
			cur_token = myTokens.get(cur);

			type = cur_token.getType();
			line_num = cur_token.getLine_num();
			indent = cur_token.getIndent_num();

			if (type == 1) {
				if (indent == 0) {
					indent_max = 0;
					int val_type = cur_token.getValue_type();
					put_kv(val_type, cur_token, indent);
				} else if (indent == 2) {
					if (cur == 0) {
						throw new LexicalException("Unexpected ' '", line_num, 1);
					} else if (myTokens.get(cur - 1).getType() != 4) {
						throw new LexicalException("the key-value element in array does't match with '-'", line_num, 5);
					} else if ((indent - 1) != myTokens.get(cur - 1).getIndent_num()) {
						int temp = myTokens.get(cur - 1).getIndent_num();
						throw new LexicalException("the key-value element in array does't match with '-'", line_num,
								temp * 2 + 1);
					}
					arraynum2--;
					indent_max = indent - 1;
					int val_type = cur_token.getValue_type();
					put_kv(val_type, cur_token, indent);

				} else if (indent == 3) {
					if (myTokens.get(cur - 1).getType() != 4) {
						throw new LexicalException("the key-value element in array does't match with '-'", line_num,
								indent * 2 + 1);
					} else if ((indent - 1) != myTokens.get(cur - 1).getIndent_num()) {
						throw new LexicalException("the key-value element in array does't match with '-'", line_num,
								indent * 2 + 1);
					}
					indent_max = indent - 1;
					int val_type = cur_token.getValue_type();
					put_kv(val_type, cur_token, indent);
				} else
					throw new LexicalException("indentation is invalid", line_num, indent * 2);
				// 最多支持两层嵌套的数组
			} else if (type == 2) {
				if (cur == 0) {
					throw new LexicalException("Unexpected '-'", line_num, indent * 2);
				} else if (!(1 <= indent && indent <= indent_max)) {
					throw new LexicalException("the array value is invalid", line_num, indent * 2);
				}
				indent_max = indent;
				if (indent == 1) {
					TokenList.add(new TokenNode(TokenType.ARRAYCHILD, cur_token.getValue(), TokenFather, arraynum));
					arraynum++;
				}
				if (indent == 2) {
					arraynum2++;
					TokenList.add(new TokenNode(TokenType.ARRAYCHILD, cur_token.getValue(), TokenFather, arraynum,
							arraynum2));
				}
			} else if (type == 3) {
				if (indent != 0) {
					throw new LexicalException("the key of array value is invalid", line_num, indent * 2);
				}
				indent_max = 1;
				TokenFather = cur_token.getKeyName();
				arraynum = 0;
				arraynum2 = -2;
			} else if (type == 4) {
				if (cur == 0) {
					throw new LexicalException("Unexpected '-'", line_num, indent * 2 + 1);
				}
				if (!(1 <= indent && indent <= indent_max)) {
					throw new LexicalException("the '-' has invalid indent", line_num, indent * 2 + 1);
				}
				arraynum2++;
				indent_max = indent + 1;
			}
			cur++;
		}
	}

	// ***option实现****
	public void parse() throws LexicalException {
		Vector<Token> myToken = new Vector<>();
		try {
			while (code.nextLine() != null) {
				currentLine = code.getLine();
				GetLexicalUnit(Preprocess(code.getLineNo(), currentLine), myToken);
			}
			Analysis(myToken);
			System.out.println("valid");
		} catch (LexicalException e) {
			throw e;
		}
	}

	public void find(String string) {
		// 包含key的情况
		if (string.contains(".")) {
			String arrayN;
			String array;
			String key;
			int num;
			arrayN = string.split("\\.", 2)[0];
			// System.out.println(arrayN);
			int count = -1;
			array = arrayN.split("\\[", 2)[0];
			// value = value.split("#",2)[0];
			num = Integer.parseInt(arrayN.split("\\[", 2)[1].split("\\]", 2)[0]);
			key = string.split("\\.", 2)[1];
			// System.out.println(key);
			for (TokenNode list : TokenList) {
				if (list.type == TokenType.ARRAYCHILD) {
					if (list.tokenFather.equals(array)) {
						if (list.arraynum == num && list.key.equals(key)) {
							System.out.println(Value(list.value));
							count++;
						}
					}
				}
			}
			if (count == -1) {
				System.out.println("null");
			}
			// 二重数组
		} else if (string.contains("][")) {
			int num;
			int num1;
			int count = -1;
			String array;
			array = string.split("\\[", 3)[0];
			num = Integer.parseInt(string.split("\\[")[1].split("\\]", 2)[0]);
			num1 = Integer.parseInt(string.split("\\[")[2].split("\\]", 2)[0]);
			for (TokenNode list : TokenList) {
				if (list.type == TokenType.ARRAYCHILD) {
					if (list.tokenFather.equals(array)) {
						if (list.arraynum == num && list.key == null) {
							if (list.arraynum2 == num1) {
								System.out.println(Value(list.value));
								count++;
							}
						}
					}
				}
			}
			if (count == -1) {
				System.out.println("null");
			}
		} else if (string.contains("[")) {
			int count = -1;
			int num;
			String array;
			array = string.split("\\[", 2)[0];
			// value = value.split("#",2)[0];
			num = Integer.parseInt(string.split("\\[")[1].split("\\]", 2)[0]);
			for (TokenNode list : TokenList) {
				if (list.type == TokenType.ARRAYCHILD) {

					if (list.tokenFather.equals(array)) {
						if (list.arraynum == num && list.key == null && list.arraynum2 < 0) {
							System.out.println(Value(list.value));
							count++;
						}

					}
				}
			}
			if (count == -1) {
				System.out.println("null");
			}
		} else {
			// kv情况
			int count = -1;
			for (TokenNode list : TokenList) {
				if (list.type == TokenType.KEYVALUE) {
					if (list.key.equals(string)) {
						count++;
						if (list.invalue != 0) {
							System.out.println(list.invalue);
						} else if (list.dbvalue != 0.0) {
							System.out.println(list.dbvalue);
						} else if (list.value != null) {
							System.out.println(Value(list.value));
						} else if (list.bgvalue != null) {
							System.out.println(list.bgvalue);
						} else {
							System.out.println(list.blvalue);
						}
					}
				}
			}
			if (count == -1) {
				System.out.println("null");
			}
		}
	}

	public void json(String file_str) throws LexicalException, FileNotFoundException, UnsupportedEncodingException {
		Vector<Token> myToken = new Vector<>();
		try {
			while (code.nextLine() != null) {
				currentLine = code.getLine();
				GetLexicalUnit(Preprocess(code.getLineNo(), currentLine), myToken);
			}
			Analysis(myToken);
			String filename = file_str + ".json";
			File file = new File(filename);

			OutputStreamWriter osw = null;
			if (file.exists()) {
				try {
					file.createNewFile();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			try {
				osw = new OutputStreamWriter(new FileOutputStream(file));

				int type;
				int indent;
				int cur = 0;
				String key_str = null;
				String value_str = null;
				int size = myToken.size();
				Token cur_token = null;
				osw.write("{\n");
				while (cur < size) {
					cur_token = myToken.get(cur);
					type = cur_token.getType();
					key_str = cur_token.getKeyName();
					value_str = cur_token.getValue();
					indent = cur_token.getIndent_num();
					if (type == 1) {
						if (indent == 0) {
							if (cur + 1 < size)
								osw.write("  \"" + key_str + "\": " + value_str + ",\n");
							else{
								osw.write("  \"" + key_str + "\": " + value_str + "\n");
								osw.write("}\n");
							}
						} else if (indent == 2) {
							osw.write("      \"" + key_str + "\": " + value_str + "\n");
							osw.write("    }\n");
							if (cur == size - 1) {
								osw.write("  ]\n");
								osw.write("}\n");
							}
							else if (myToken.get(cur + 1).getIndent_num() == 0)
								osw.write("  ],\n");
						} else if (indent == 3) {
							osw.write("        \"" + key_str + "\": " + value_str + "\n");
							osw.write("      }\n");
							if (cur == size - 1) {
								osw.write("    ]\n");
								osw.write("  ]\n");
								osw.write("}\n");
							}
							if (myToken.get(cur + 1).getIndent_num() == 1)
								osw.write("    ],\n");
							if (myToken.get(cur + 1).getIndent_num() == 0) {
								osw.write("    ]\n");
								osw.write("  ],\n");
							}
						}
					} else if (type == 2) {
						if (indent == 1) {
							if (cur == size - 1) {
								osw.write("    " + value_str + "\n");
								osw.write("  ]\n");
								osw.write("}\n");
							}
							if (myToken.get(cur + 1).getIndent_num() == 1)
								osw.write("    " + value_str + ",\n");
							if (myToken.get(cur + 1).getIndent_num() == 0) {
								osw.write("    " + value_str + "\n");
								osw.write("  ],\n");
							}
						}
						if (indent == 2) {
							if (cur == size - 1) {
								osw.write("      " + value_str + "\n");
								osw.write("    ]\n");
								osw.write("  ]\n");
								osw.write("}\n");
							}
							if (myToken.get(cur + 1).getIndent_num() == 2)
								osw.write("      " + value_str + ",\n");
							if (myToken.get(cur + 1).getIndent_num() == 1) {
								osw.write("      " + value_str + "\n");
								osw.write("    ],\n");
							}
							if (myToken.get(cur + 1).getIndent_num() == 0) {
								osw.write("      " + value_str + "\n");
								osw.write("    ]\n");
								osw.write("  ],\n");
							}
						}
					} else if (type == 3) {
						osw.write("  \"" + key_str + ": [\n");
						if (cur == size - 1) {
							osw.write("  ]\n");
							osw.write("}\n");
						} else if (myToken.get(cur + 1).getIndent_num() == 0) {
							osw.write("  ],\n");
						}
					} else if (type == 4) {
						if (indent == 2) {
							osw.write("      {\n");
							// 不支持三重嵌套数组
						} else if (indent == 1) {
							if (myToken.get(cur + 1).getType() == 1) {
								osw.write("    {\n");
							} else if (myToken.get(cur + 1).getType() == 2) {
								osw.write("    [\n");
								if (cur == size - 1) {
									osw.write("    ]\n");
									osw.write("  ]\n");
									osw.write("}\n");
								}
								if (myToken.get(cur + 1).getIndent_num() == 1) {
									osw.write("    ],\n");
								}
								if (myToken.get(cur + 1).getIndent_num() == 0) {
									osw.write("    ]\n");
									osw.write("  ],\n");
								}
							}
						}
					}
					cur++;
				}
				System.out.println("success to write .json file.");
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} finally {
				try {
					osw.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		} catch (LexicalException e) {
			throw e;
		}
	}

	public static void main(String[] args) throws FileNotFoundException, UnsupportedEncodingException {
		// 解析控制台输入
		String filepath = "";
		String instruction = "";
		if (args.length == 1) {
			filepath = args[0];
		} else if (args.length == 2) {
			if (args[0].equals("-parse"))
				filepath = args[1];
			else if (args[0].equals("-json")) {
				filepath = args[1];
			} else {
				System.out.println("yamlite [option [value]] file");
				System.exit(0);
			}
		} else if (args.length == 3) {
			if (args[0].equals("-find")) {
				instruction = args[1];
				filepath = args[2];
			} else {
				System.out.println("yamlite [option [value]] file");
				System.exit(0);
			}
		} else {
			System.out.println("yamlite [option [value]] file");
			System.exit(0);
		}

		String[] separate = filepath.split("\\.", 2);
		if (!separate[1].equals("yml")) {
			System.out.println("ERROR: the file should be in format yml");
			System.exit(0);
		}

		File file = new File(filepath);
		if (!file.isFile() || !file.exists()) {
			System.out.println("No file exists!");
			System.exit(0);
		} else {
			Lexer lexer = new Lexer(filepath);
			try {
				if (args.length == 1 || args[0].equals("-parse")) {
					lexer.parse();
				} else if (args[0].equals("-find")) {
					lexer.find(instruction);
				} else if (args[0].equals("-json")) {
					lexer.json(separate[0]);
				}

			} catch (LexicalException e) {
				e.printStackTrace();
			}
		}
	}

}
