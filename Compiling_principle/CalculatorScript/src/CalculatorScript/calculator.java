package CalculatorScript;

import java.io.IOException;
import java.io.Reader;
import javax.script.*;

//【1】截取词法单元
//把输入string截成一个个token放入Token Vector。
// *****(1)number [integer/double]
// *****(2)标志符
// *****(3)operator
//
//【2】语法分析
//1.赋值
//****计算part：建立语法树
//****变量part：binding
//2.print()
//****内部计算
//****输出
//
//【3】建立语法树 & 生成三段地址代码
//1.算符优先分析、递归下降
//2.二叉树
//3.后序遍历语法树->得到结果
//
//【4】异常处理
//
//

public class calculator extends AbstractScriptEngine{  
	
	@Override
	public Object eval(String script, ScriptContext context) throws ScriptException {
		String id = new String();
		
		try {
			if (script.charAt(script.length() - 1) != ';')
				throw new TokenException("syntax error, insert \";\" to complete the sentence", 0, 0);
			String str = script.substring(0, script.length());	
			interpreter itp = new interpreter();
			id = itp.Analysis(interpreter.getLexicalUnit(str));
			return null;
		} catch (TokenException e) {
			throw new ScriptException(e);
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		if(id != "")
			context.setAttribute(id, interpreter.identifiers.get(id), ScriptContext.GLOBAL_SCOPE);
		return null;
	}

	@Override
	public void put(String s, Object o){  
		myNumber number = new myNumber(Double.valueOf(o.toString()));
		interpreter.identifiers.put(s, number);  
	} 
	
	@Override  
    public Object eval(Reader reader, ScriptContext context) throws ScriptException {  
        return null;  
    }  
  
    @Override  
    public Bindings createBindings() {  
        return null;  
    }  
  
    @Override  
    public ScriptEngineFactory getFactory() {  
        return null;  
    }

}

class TokenException extends Exception {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private String message;
	private Integer line;
	private Integer position;// 从零开始，算单个字符

	public TokenException(String msg, Integer lin, Integer pos) {
		message = msg;
		line = lin;
		position = pos;
	}

	public void print() {
		System.out.println("Error(line " + line + ", position " + position + "): " + message);
	}
}
