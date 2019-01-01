package Tokens;

public class TYPE {
	public final static int INTEGER = 1;	//=>user defined
	public final static int REAL = 2;		//=>user defined
	public final static int BOOL = 3;		//=> true/false
	public final static int STRING = 4;		//=>user defined
	
	public final static int DATATYPE = 5;	//=>int, bool, real, string
	public final static int IDENTIFIER = 6;	//=>user defined
	public final static int ARRAY = 7;		//=>user defined(id)
	
	public final static int ATH_OP = 8;		//=> + - * /  =  
	public final static int CPN_OP = 9;		//=> ==  > < <>
	public final static int OP = 10;	
	
	public final static int DELIMITER = 11;	//=>( ) ; { } [ ] , “
	public final static int KEYW = 12;		//=>KEYWORD
	public final static int COMMENT = 13;	//=>user defined
	public final static int NL = 14;		//"换行符"\n
	public final static int SPACE = 15;		//"空白符"
	public final static int RT = 16;		//"回车符"\r
	public final static int TB = 17; 		//"制表符"\t
	
	public final static int ERROR = 18;
}
