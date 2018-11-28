import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class SourceCode {
    private BufferedReader code;
    private int lineNo;
    private String line;

    public SourceCode(String path) {
        try {
            code = new BufferedReader(new FileReader(path));
            lineNo = 0;
        }catch (IOException e){
            e.printStackTrace();
        }
    }
    public String nextLine(){
        String temp = "";
        try{
            temp = code.readLine();
        }catch (IOException e){
            e.printStackTrace();
        }
        if(temp!=null) {
            temp = temp + "\n";
            lineNo++;
        }
        line = temp;
        return line;
    }

    public int getLineNo() {
        return lineNo;
    }

    public String getLine() {
        return line;
    }
}