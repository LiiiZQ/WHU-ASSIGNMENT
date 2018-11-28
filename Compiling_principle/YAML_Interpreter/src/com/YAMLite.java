package com;

import java.util.*;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.io.File;
import java.lang.Character;

/*
 * 完整地体现一个解析器的结构，读取源文件->词法分析->语法分析->简单的语义分析->实现语义；
 * 缩进文法属于乔姆斯基一级文法（上下文有关文法）
 *
 * 待解决notes:
 * #(1)只有值与值名称  (2)缩进和数组层级有关有意义，不能直接忽略/缩进->类型
 */

public class YAMLite {
	public static final String[] SET_VALUE_FOR_KEY = new String[] { "a", "b", "c", "d", "e", "f", "g", "h", "i", "j",
			"k", "l", "m", "n", "o", "p", "q", "r", "s", "t", "u", "v", "w", "x", "y", "z", "A", "B", "C", "D", "E",
			"F", "G", "H", "I", "J", "K", "L", "M", "N", "O", "P", "Q", "R", "S", "T", "U", "V", "W", "X", "Y", "Z",
			"0", "1", "2", "3", "4", "5", "6", "7", "8", "9", "_" };
	public static final HashSet<String> keyletters = new HashSet<String>(Arrays.asList(SET_VALUE_FOR_KEY));
	/*
	 * public static final String[] SET_VALUE_FOR_DIGIT = new String[] { "1",
	 * "2", "3", "4", "5", "6", "7", "8", "9", "0", ".", "-", "e", "E" }; public
	 * static final HashSet<String> digits = new
	 * HashSet<String>(Arrays.asList(SET_VALUE_FOR_DIGIT));
	 */
	private SourceCode code;
	private ArrayList<Token> TokenList;
	private String currentLine;
	private String TokenFather = "";
	int arraynum = 0;
	int arraynum2 = -2;

	public YAMLite(String path) {
        code = new SourceCode(path);
        TokenList = new ArrayList<Token>();
    }

	public String Value(String value) {
		if (value.startsWith("\"")) {
			value = value.substring(1);
			int index = value.indexOf("\"");
			value = value.substring(0, index);
			return value;
		} else {
			return value;
		}
	}

	// helper function
	// "int"指示value类型，其中不合法为-1
	// (1)int (2)float (3)String (4)boolean (5)科学计数
	private int CheckValue(String value) {
		int type = -1;
		boolean isSci = false;
		boolean isFloat = false;
		int size = value.length();

		if (value.charAt(0) == '\"' && value.charAt(size - 1) == '\"')
			type = 3;
		else if (value.charAt(0) == 't' || value.charAt(0) == 'f') {
			if (value.equals("true") || value.equals("false"))
				type = 4;
		}
		// 除数字以外符号：".","e","E","-"
		else if (value.charAt(0) == '-' || (Character.isDigit(value.charAt(0)) && value.charAt(0) != '0')) {
			for (int i = 1; i < size; i++) {
				if (value.charAt(i) == '.') {
					if (isFloat || isSci)
						return -1;
					isFloat = true;
					continue;
				}
				if (value.charAt(i) == 'e' || value.charAt(i) == 'E') {
					if (isSci)
						return -1;
					isSci = true;
				}
				if (value.charAt(i) == '-' && (value.charAt(i - 1) != 'e' || value.charAt(i - 1) != 'E'))
					return -1;
				if (!Character.isDigit(value.charAt(i)))
					return -1;
			}
			if (isSci)
				type = 5;
			else if (isFloat)
				type = 2;
			else
				type = 1;
		}
		return type;
	}

	// **preprocess:对sentence预处理
	// 1.忽略注释:“#“以后无效化
	// 2.忽略结尾的空格
	// 3.检查前面空格段的数量->是否为偶数
	// 4.检查字符间的空格->只有一个space且该space为1
	// *******如何判断一整行读完？
	// 读入的sentence是文件的一行(换行符号'\n'之前全部)
	public Line Preprocess(int line_num, final String sentence) throws YamlException {
		int count = 0;
		int length = sentence.length();
		if (length == 0)
			return new Line(line_num, sentence, 0);
		// 忽略注释
		while (count < length) {
			if (sentence.charAt(count) == '#')
				break;
			count++;
		}
		// 忽略该行后的space
		while (count > 0 && sentence.charAt(count - 1) == ' ')
			count--;

		// 检查该行前的indent是否合法(为2的倍数)
		int curr = 0;
		for (; curr <= count; curr++) {
			if (sentence.charAt(curr) != ' ') {
				if (curr % 2 != 0)
					throw new YamlException("Invalid indent.", line_num, curr + 1);
				break;
			}
		}
		// 检查字符间space是否合法("- xxx"或": xxx")
		// ******* 暂待
		// 当value为字符串，字符串中间的space? 检查“"”的话如果字符串中间出现“\"”?
		boolean space_occur = false;
		boolean in_string_val = false;
		int chr_begain = curr;
		for (; curr <= count; curr++) {
			if (sentence.charAt(curr) == ' ' && !in_string_val) {
				if (space_occur)
					throw new YamlException("Unexpected ' '", line_num, curr + 1);
				else if (curr == chr_begain + 1) {
					if (sentence.charAt(chr_begain) != '-')
						throw new YamlException("Expect '-'", line_num, chr_begain + 1);
				} else if (sentence.charAt(curr - 1) != ':')
					throw new YamlException("Expect ':'", line_num, chr_begain + 1);

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

	// method1: 获得词法单元 + 语法分析
	// 扫描每一行，一行就是一个元素，把该行按类别放进Token Vector【indent, type, val1, val2】
	// 类别判断依据:
	// ***** 1.indent(= 2*space)部分:indent数量->普通元素/有数组等级元素(要求呼应的等级指示)
	// ***** 2.归类部分: 判断其类型，并为其赋相应值
	// ***** 3.异常检查
	// 字符四个type:
	// *****(1)"key: value"【key_value】: 键值对
	// *****(2)"- value”【value】:数组值
	// *****(3)"key:"【key】: 数组的键<functional>:数组等级加一层
	// *****(4)"-"【sign】:换行指示数组中接下来的值是数组/键值对<functional>:数组等级加一层／下一行kv
	// 可能出现的异常
	// ***** 1.key名称的字符
	// ***** 2.value值是否合法
	// ******
	public void GetLexicalUnit(Line line, Vector<Token> myToken) throws YamlException {
		int size = line.getSize();
		if (size == 0)
			return;

		int indent = 0;
		int cur_pos = 0;
		boolean isEven = true;
		int line_num = line.getLine_num();
		String sentence = line.getSentence();

		// indent数
		while (cur_pos <= size) {
			if (sentence.charAt(cur_pos) == ' ') {
				if (!isEven) {
					isEven = true;
					indent++;
				}
				isEven = false;
			} else
				break;
			cur_pos++;
		}
		int chr_start = cur_pos;
		// 判断类型
		if (sentence.charAt(chr_start) == '-') {
			// type4【sign】(-)
			if (cur_pos == size) {
				myToken.add(new Token(indent, 4, line_num));
				return;
			}
			// type2【value】(- value)
			else {
				if (sentence.charAt(++cur_pos) != ' ') {
					throw new YamlException("expect ' '", line_num, cur_pos + 1);
				}
				String str = sentence.substring(++cur_pos, size);
				int val_type = CheckValue(str);
				if (val_type == -1) {
					throw new YamlException("invaild value", line_num, cur_pos + 1);
				} else
					myToken.add(new Token(indent, 2, null, str, val_type, line_num));
				return;
			}
		}
		while (cur_pos <= size) {
			if (sentence.charAt(cur_pos) == ':') {
				String key_str = sentence.substring(chr_start, cur_pos - 1);
				// 检查key值是否合法
				for (int i = 0; i < key_str.length(); i++) {
					if (!keyletters.contains(String.valueOf(key_str.charAt(i))))
						throw new YamlException("use invalid character for key", line_num, chr_start + i + 1);
				}
				// type3【key】(key:)
				if (cur_pos == size) {
					myToken.add(new Token(indent, 3, key_str, null, 0, line_num));
					return;
				}
				// type1【key_value】(key: value)
				else {
					if (sentence.charAt(++cur_pos) != ' ')
						throw new YamlException("expected ' '", line_num, cur_pos + 1);
					String value_str = sentence.substring(++cur_pos, size);
					int value_type = CheckValue(value_str);
					if (value_type == -1)
						throw new YamlException("invaild value", line_num, cur_pos + 1);
					else
						myToken.add(new Token(indent, 1, key_str, value_str, value_type, line_num));
				}
			}
			cur_pos++;
		}
	}

	// method2: 语义分析
	// 把它从纯字面储存模式变成我要的数据->(1)键值(2)数组
	// 从两方面
	// 1.indent数获得数组的等级
	/////////// indent:当前元素的数组等级(数组内kv例外), indent_max:当前打开的数组中最外层的数组等级
	// *****(1)"key: value"【key_value】
	// indent = 0, indent_max = 0;
	// indent >= 2, preToken为【sign】, indent = preIndent + 1, indent_max =
	// preIndent;
	// *****(2)"- value”【value】
	// 1 ≤ indent ≤ indent_max, indent_max = indent;
	// *****(3)"key:"【key】: indent = 0, indent_max = 1;
	// *****(4)"-"【sign】:1 ≤ indent ≤ indentMax, indent_max = indent + 1;
	// *****将"-"indent = nextIndent情况认为是：(作为元素的)该数组 为空
	// 2.按照"键-值"重新储存HashMap:特别是切分出数组(怎么解决数组层级问题)
	// *****(1)"key: value"【key_value】<string, val>
	// *****(2)"- value”【value】普通数组元素:->推入哪一层数组？
	// *****(3)"key:"【key】新建最外层数组
	// *****(4)"-"【sign】
	// ***** nextToken:是key-value数组元素还是新建一层数组?（如果是空数组也新建一层数组）
	// kv类型-> 直接推入数组
	// v类型-> 新建一个vector->push入相应vector;
	// **** 可能出现的异常：
	// indent数不符合数组等级关系(也只剩这个异常了)(和语义联系的异常)
	public void Analysis(Vector<Token> myTokens, Vector<Token> result, String exception) throws YamlException {
		int line;
		int type;
		int indent;
		Token cur_token = null;
		HashMap<String, String> find_map = new HashMap<String, String>();
		Stack<Token> myStack = null;

		cur_token = myTokens.get(0);
		type = cur_token.getType();
		line = cur_token.getLine_num();
		indent = cur_token.getIndent_num();
		if (indent != 0) {
			throw new YamlException("Unexpected ' '", line, 0);
		} else {
			if (type == 2 || type == 4) {
				throw new YamlException("Unexpected '-'", line, 0);
			}
		}

		int cur = 1;
		int indent_max = 0;
		int size = myTokens.size();
		while (cur < size) {
			type = cur_token.getType();
			cur_token = myTokens.get(cur);
			line = cur_token.getLine_num();
			indent = cur_token.getIndent_num();
			int temp;
			if (type == 1) {
				if (indent == 0)
					indent_max = 0;
				else if (indent < 2)
					throw new YamlException("Invalid indent.", line, 3);
				else {
					if (myTokens.get(cur - 1).getType() != 4) {
						throw new YamlException("the key-value element in array does't match with '-'", line - 1,
								indent * 2 - 1);
					} else if ((indent - 1) != myTokens.get(cur - 1).getIndent_num()) {
						throw new YamlException("the key-value element in array does't match with '-'", line,
								indent * 2 - 1);
					}
					temp = indent_max - indent + 1;

					for (int i = 0; i < temp; i++) {
						myStack.pop();
					}

					indent_max = indent - 1;

				}
			} else if (type == 2) {
				if (!(1 <= indent && indent <= indent_max)) {
					throw new YamlException("the array value is invalid", line, indent * 2 + 1);
				}
				indent_max = indent;
			} else if (type == 3) {
				if (indent != 0) {
					throw new YamlException("the array value is invalid", line, indent * 2 + 1);
				}
				indent_max = 1;
			} else if (type == 4) {
				if (!(1 <= indent && indent <= indent_max)) {
					throw new YamlException("the '-' has invalid indent", line, indent * 2 + 1);
				}

				indent_max = indent + 1;
			}
			cur++;
		}
	}

	// **** 从语言到功能 option -parse -json -find
	// -parse:indent异常报完即足以解决
	// -find:存储解决后即可解决
	// -json:复写文件 (1)根据indent写:词到词
	// 优势:不用考虑语义，可以直接在indent上加工，在层级交替的地方记得考虑"[]"、"{}"即可
	// (2)根据储存写:即根据语义写 ->如何判断当前元素是数组?根据数组递进加入intend

	// option1:parse
	// 调用Analysis, 若为false则输出exception内容
	// * 读完整个文件，确认语法是否正确，如果全部正确，输出valid
	// * 如果出现错误，要求能指出错误行数 & 位置
	public void Parse() {
		try {
			Vector<Token> theToken = null;

		} catch (YamlException e) {
			// TODO: handle exception
		}

	}

	// option2:json
	// * 读完整个文件，生成同名json文件，依次生成json文件下对应代码并写入到文件中
	// 读一遍result,写json(注意缩进)
	// *****1."}"利用栈：对应},每个层级结束时弹一次栈
	// *****2.是否有下一个元素？有的话末尾+"," (注意{}中键值不用加)
	// *****3.键值类型 是否在数组中？
	// (1)不在"\"string\": " + value
	// (2)在 "{" 换行 "\"string\": " + value 换行"}"
	// *****4.数组类型 "\"string\": [" 换行，开始输出数组元素
	// ***** 利用栈:该层级数组结束时弹一次]
	void Json(String fileName) {

	}

	// option3:find
	// * 读取整个文件，查找对应键下value
	// 1.读+拆address, 变量名/. -> num找几次
	// 2.找 for循环 遍历找到val1(string,string[i]),再到遍历val1
	// 3.输出 “value”如果value是键或数组->判断:以json格式输出
	//
	void Find(String address) {

	}
};
