import java.util.ArrayList;

public class Analyzer {


    private String expression;
    private ArrayList<String> token;
    private ArrayList<Integer> value;
    private ArrayList<String> checkedword;
    private ArrayList<Integer> checkedvalue;
    private boolean isLexicalError;
    private boolean isSyntaxError;

    public Analyzer(){}

    public Analyzer(String expression) {
        this.expression = expression;
        this.token =new ArrayList<String>();
        this.value =new ArrayList<Integer>();
        this.checkedword = new ArrayList<String>();
        this.checkedvalue = new ArrayList<Integer>();
        this.isLexicalError = false;
        this.isSyntaxError = false;
    }


    public void setToken(){
        String str="";
        boolean isFloat = false;
        boolean isNumber = false;
        for (int i = 0; i < expression.length(); i++) {
            if((expression.charAt(i)>47 && expression.charAt(i)<58)||expression.charAt(i)==46){
                isNumber = true;
                if(!isFloat){
                    if(expression.charAt(i)>47&&expression.charAt(i)<58){
                        str+=expression.charAt(i);
                    }
                    if(expression.charAt(i)==46){
                        str+='.';
                        isFloat = true;
                    }
                }else{
                    if(expression.charAt(i)==46){
                        str="第"+(token.size()+1)+"个token出错";
                        break;
                    }
                    else if(expression.charAt(i)>47&&expression.charAt(i)<58){
                        str+=expression.charAt(i);
                    }
                }
            }
            else if((expression.charAt(i)>39&&expression.charAt(i)<44)||expression.charAt(i)==45||expression.charAt(i)==47||expression.charAt(i)==37||expression.charAt(i)==94) {
                isFloat=false;
                if(isNumber){
                    if(str.charAt(0)=='.'||str.charAt(str.length()-1)=='.'){
                        str="第"+(token.size()+1)+"个token出错";
                        break;
                    }
                    this.token.add(str);
                    isNumber=false;
                }
                token.add(String.valueOf(expression.charAt(i)));
                str="";
            }
            else {
                if(isNumber){
                    if(str.charAt(0)=='.'||str.charAt(str.length()-1)=='.'){
                        str="第"+(token.size()+1)+"个token出错";
                        break;
                    }
                    this.token.add(str);
                }
                str="第"+(token.size()+1)+"个token出错";
                break;
            }
        }
        if(str!=""){
            if(isNumber){
                if(str.charAt(0)=='.'||str.charAt(str.length()-1)=='.'){
                    str="第"+(token.size()+1)+"个token出错";
                }
            }
            this.token.add(str);
        }
    }

    public void setValue() {
        for(int i = 0; i< token.size(); i++){
            value.add(-1);
            try{
                if(token.get(i).charAt(0)>47&&token.get(i).charAt(0)<58){
                    if(token.get(i).contains(".")){
                        value.set(i,2);//浮点数
                    }else{
                        value.set(i,1);//整数
                    }
                }
                if(token.get(i).charAt(0)==40){
                    value.set(i,3);//(
                }
                if(token.get(i).charAt(0)==41){
                    value.set(i,4);//)
                }
                if(token.get(i).charAt(0)==43){
                    value.set(i,5);//+
                }
                if(token.get(i).charAt(0)==45){
                    value.set(i,6);//-
                }
                if(token.get(i).charAt(0)==42){
                    value.set(i,7);//*
                }
                if(token.get(i).charAt(0)==47){
                    value.set(i,8);// /
                }
                if(token.get(i).charAt(0)==94){
                    value.set(i,9);// ^
                }
                if(token.get(i).charAt(0)==37){
                    value.set(i,10);// %
                }

            }catch (Exception e){
                e.printStackTrace();
                value.set(i,-1);
            }

        }
    }

    public void checkMinus(){
        int t;
        for(t=0;t<value.size();t++){
            //如果第一个值是负值
            if(t==0&&value.size()>=2&&value.get(0)==6&&(value.get(1)==1||value.get(1)==2)){
                if(value.get(1)==1){
                    checkedvalue.add(1);
                    checkedword.add("-"+token.get(1));
                }
                if(value.get(1)==2){
                    checkedvalue.add(2);
                    checkedword.add("-"+token.get(1));
                }
                t++;
            }
            else if(t==0&&value.size()>=2&&value.get(0)==5&&(value.get(1)==1||value.get(1)==2)){
                if(value.get(1)==1){
                    checkedvalue.add(1);
                    checkedword.add("+"+token.get(1));
                }
                if(value.get(1)==2){
                    checkedvalue.add(2);
                    checkedword.add("+"+token.get(1));
                }
                t++;
            }
                //整数：1 || 浮点数：2 || (：3 || )：4 || +：5 || -：6 || *：7 || /：8 || ^：9 || %：10
            else if((t+3)<value.size()&&t>0&&value.get(t)==3&&value.get(t+1)==6&&(value.get(t+2)==1||value.get(t+2)==2)&&value.get(t+3)==4){
                //(
                checkedvalue.add(3);
                checkedword.add("(");
                //负数
                if(value.get(t+2)==1){
                    checkedvalue.add(1);
                    checkedword.add("-"+token.get(t+2));
                }
                if(value.get(t+2)==2){
                    checkedvalue.add(2);
                    checkedword.add("-"+token.get(t+2));
                }
                //）
                checkedvalue.add(4);
                checkedword.add(")");
                t+=3;
            }
            else if((t+3)<value.size()&&t>0&&value.get(t)==3&&value.get(t+1)==5&&(value.get(t+2)==1||value.get(t+2)==2)&&value.get(t+3)==4){
                //(
                checkedvalue.add(3);
                checkedword.add("(");
                //负数
                if(value.get(t+2)==1){
                    checkedvalue.add(1);
                    checkedword.add("+"+token.get(t+2));
                }
                if(value.get(t+2)==2){
                    checkedvalue.add(2);
                    checkedword.add("+"+token.get(t+2));
                }
                //）
                checkedvalue.add(4);
                checkedword.add(")");
                t+=3;
            }
            else {
                checkedvalue.add(value.get(t));
                checkedword.add(token.get(t));
            }
        }
    }

    //整数：1 || 浮点数：2 || (：3 || )：4 || +：5 || -：6 || *：7 || /：8 || ^：9 || %：10

    public String printLexicalAnalysis(){
        setToken();
        setValue();
        checkMinus();
        String str = "-----词法分析开始-----\n";
        for(int i = 0;i<checkedword.size();i++){
            str+=checkedword.get(i)+":";
            switch (checkedvalue.get(i)){
                case 1:
                    str+="整数";
                    break;
                case 2:
                    str+="浮点数";
                    break;
                case 3:
                    str+="括号";
                    break;
                case 4:
                    str+="括号";
                    break;
                case 5:
                    str+="算术操作符";
                    break;
                case 6:
                    str+="算术操作符";
                    break;
                case 7:
                    str+="算术操作符";
                    break;
                case 8:
                    str+="算术操作符";
                    break;
                case 9:
                    str+="算术操作符";
                    break;
                case 10:
                    str+="算术操作符";
                    break;
                default:
                    str+="未识别"+"\n【提示：词法分析中断，请检查后重新输入】";
                    this.isLexicalError = true;
                    break;
            }
            str+="\n";
        }
        if(!isLexicalError){
            str+="-----词法分析结束-----\n\n";
        }
        return str;
    }


    //文法：E->E+T|E-T|T  T->T*F|T/F|F F->(E)|d
    //语法分析

    int idex=0;
    int symbol;
    int error=0;
    //获得下一个token的种类编码
    public void Next(){
        if(idex<checkedvalue.size()){
            symbol=checkedvalue.get(idex);
            idex++;
        }
        else{
            symbol=0;
        }

    }
    // E->T{+T|-T}
    public void E(){
        System.out.println("E->T");
        T();
        while(symbol==5||symbol==6){
            if(symbol==5){
                System.out.println("E->T+T");
            }
            if(symbol==6){
                System.out.println("E->T-T");
            }
            Next();
            T();
        }

    }
    //T->F{*F|/F}
    public void T(){
        System.out.println("T->F");
        F();
        while(symbol==7||symbol==8){
            if(symbol==7){
                System.out.println("T->F*F");
            }
            if(symbol==8){
                System.out.println("T->F/F");
            }
            Next();
            F();
        }
    }
    //F->(E)|d
    public void F(){
        if(symbol==1||symbol==2){
            System.out.println("F->d:"+checkedword.get(idex-1));
            Next();
        }
        else if(symbol==3){
            System.out.println("F->(E)");
            Next();
            E();
            if(symbol==4){
                Next();
            }
            else{
                error=-1;
                System.out.println("F():第"+(idex)+"词法单元错误");
            }
        }
        else{
            error=-1;
            System.out.println("F()2:第"+(idex)+"词法单元错误");
        }
    }
    public String printSyntaxAnalysis(){
        if(isLexicalError){
            return "【提示：词法分析错误，无法进行语法分析】";
        }
        String str = "-----语法检查开始-----\n";

        Next();
        E();
        if(symbol==0&&error==0){
            str+="-----语法检查结束-----\n\n";
        }
        else{
            if(symbol!=0){
                str=str+"第"+idex+"词法单元错误";
            }
            str+="【提示：语法检查错误，无法分析】";
        }
        return str;

    }


}
