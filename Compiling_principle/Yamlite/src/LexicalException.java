
public class LexicalException extends Exception{
    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	
	
	public LexicalException(String message){
        super(message);
    }

    public LexicalException(String message, int lineNo, int position){
        super("line " + lineNo + ", position "+position+": " + message);
    }
    public LexicalException(String message, int lineNo){
        super("line" + lineNo + ": " + message);
    }
    
}
