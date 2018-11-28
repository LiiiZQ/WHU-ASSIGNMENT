package CalculatorScript;

import java.util.*;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineFactory;

public class calculatorEngineFactory implements ScriptEngineFactory {  
	  
    public String getEngineName() {  
        return "calculator";  
    }  
  
    public String getEngineVersion() {  
        return "1.0";  
    }  
  
    public List getExtensions() {  
        return null;  
    }  
  
    public List getMimeTypes() {  
        return null;  
    }  
  
    public List getNames() {  
        return Arrays.asList( "calculator");  
    }  
  
    public String getLanguageName() {  
        return "Calculator Script";  
    }  
  
    public String getLanguageVersion() {  
        return "1.0";  
    }  
  
    public Object getParameter(String var1) {  
        if (Objects.equals(var1, "javax.script.engine")) {  
            return this.getEngineName();  
        } else if (Objects.equals(var1, "javax.script.engine_version")) {  
            return this.getEngineVersion();  
        } else if (Objects.equals(var1, "javax.script.name")) {  
            return this.getNames();  
        } else if (Objects.equals(var1, "javax.script.language")) {  
            return this.getLanguageName();  
        } else {  
            return Objects.equals(var1, "javax.script.language_version") ? this.getLanguageVersion() : null;  
        }  
    }  
  
    public String getMethodCallSyntax(String var1, String var2, String... var3) {  
        return "";  
    }  
  
    public String getOutputStatement(String var1) {  
        return null;  
    }  
  
    public String getProgram(String... var1) {  
        return "";  
    }  
  
    public ScriptEngine getScriptEngine()   
    {   
        // return your object  
        return new calculator();   
    }  
}  