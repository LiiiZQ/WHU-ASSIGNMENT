import java.math.BigDecimal;
public class TokenNode {
	public TokenType type;
	public String value;//String
	public int invalue;//int
	public double dbvalue;//float
	public boolean blvalue;//bool
	public BigDecimal bgvalue;//sci
	public String tokenFather;
	public int arraynum;
	public int arraynum2;
	String key;
	
	public TokenNode(TokenType type,String value,String tokenFather,int arraynum)
	{
		this.type=type;
		this.value=value;
		this.tokenFather=tokenFather;
		this.arraynum=arraynum;
	}
	public TokenNode(TokenType type,String key,String value)
	{
		this.type=type;
		this.value=value;
		this.key=key;;
	}
	public TokenNode(TokenType type,String key,int value)
	{
		this.type=type;
		this.invalue=value;
		this.key=key;;
	}
	public TokenNode(TokenType type,String key,double value)
	{
		this.type=type;
		this.dbvalue=value;
		this.key=key;;
	}
	public TokenNode(TokenType type,String key,BigDecimal value)
	{
		this.type=type;
		this.bgvalue=value;
		this.key=key;;
	}
	public TokenNode(TokenType type,String key,boolean value)
	{
		this.type=type;
		this.blvalue=value;
		this.key=key;;
	}

	public TokenNode(TokenType type,String value,String tokenFather,int arraynum,int arraynum2)
	{
		this.type=type;
		this.value=value;
		this.tokenFather=tokenFather;
		this.arraynum=arraynum;
		this.arraynum2=arraynum2;
	}
	///////////////
	public TokenNode(TokenType type,String key,int value,String tokenFather,int arraynum)
	{
		this.type=type;
		this.key=key;
		this.invalue=value;
		this.tokenFather=tokenFather;
		this.arraynum=arraynum;		
	}
	public TokenNode(TokenType type,String key,double value,String tokenFather,int arraynum)
	{
		this.type=type;
		this.key=key;
		this.dbvalue=value;
		this.tokenFather=tokenFather;
		this.arraynum=arraynum;		
	}
	public TokenNode(TokenType type,String key,BigDecimal value,String tokenFather,int arraynum)
	{
		this.type=type;
		this.key=key;
		this.bgvalue=value;
		this.tokenFather=tokenFather;
		this.arraynum=arraynum;		
	}
	public TokenNode(TokenType type,String key,boolean value,String tokenFather,int arraynum)
	{
		this.type=type;
		this.key=key;
		this.blvalue=value;
		this.tokenFather=tokenFather;
		this.arraynum=arraynum;		
	}
	public TokenNode(TokenType type,String key,String value,String tokenFather,int arraynum)
	{
		this.type=type;
		this.key=key;
		this.value=value;
		this.tokenFather=tokenFather;
		this.arraynum=arraynum;		
	}
	/////////////////////
	public TokenNode(TokenType type,String key,int value,String tokenFather,int arraynum,int arraynum2)
	{
		this.type=type;
		this.key=key;
		this.invalue=value;
		this.tokenFather=tokenFather;
		this.arraynum=arraynum;		
		this.arraynum2=arraynum2;
	}
	public TokenNode(TokenType type,String key,double value,String tokenFather,int arraynum,int arraynum2)
	{
		this.type=type;
		this.key=key;
		this.dbvalue=value;
		this.tokenFather=tokenFather;
		this.arraynum=arraynum;		
		this.arraynum2=arraynum2;
	}
	public TokenNode(TokenType type,String key,BigDecimal value,String tokenFather,int arraynum,int arraynum2)
	{
		this.type=type;
		this.key=key;
		this.bgvalue=value;
		this.tokenFather=tokenFather;
		this.arraynum=arraynum;		
		this.arraynum2=arraynum2;
	}
	public TokenNode(TokenType type,String key,boolean value,String tokenFather,int arraynum,int arraynum2)
	{
		this.type=type;
		this.key=key;
		this.blvalue=value;
		this.tokenFather=tokenFather;
		this.arraynum=arraynum;		
		this.arraynum2=arraynum2;
	}
	public TokenNode(TokenType type,String key,String value,String tokenFather,int arraynum,int arraynum2)
	{
		this.type=type;
		this.key=key;
		this.value=value;
		this.tokenFather=tokenFather;
		this.arraynum=arraynum;		
		this.arraynum2=arraynum2;
	}
}



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

class Token {
    int indent_num;   
    int type;// 1.key_value  2.value  3.key  4.sign
    String key_str;
    String value_str;
    int value_type;//0.value值为空 1.int 2.float 3.String 4.boolean 5.科学计数法
    int line_num;
    
    Token(int indent, int type, int ln_num) {
        this(indent, 4, null, null, 0, ln_num);
    }
    Token(int indent, int type, String key_s, String val_s, int val_t, int ln_num) {
        this.indent_num = indent;
        this.type = type;
        this.key_str = key_s;
        this.value_str = val_s;
        this.value_type = val_t;
        this.line_num = ln_num;
    }
    public Token(){
    }
    
    public int getIndent_num() {
		return indent_num;
	}
    public int getType() {
		return type;
	}
    public String getKeyName() {
		return key_str;
	}
    public String getValue() {
		return value_str;
	}
    public int getValue_type() {
		return value_type;
	}
    public int getLine_num() {
		return line_num;
	}
};