
import java.util.*;

public class Calculator {


    public String conver2Postfix(String expression) throws Exception {
        Stack st = new Stack();
        String postFix = "";
        for (int i = 0; expression != null && i < expression.length(); i++) {
            char c = expression.charAt(i);
            if (' ' != c) {
                if (isOpenParenthesis(c)) {
                    // 左括号就入栈
                    st.push(c);
                } else if (isCloseParenthesis(c)) {// 右括号

                    Character ac = (Character) st.pop();
                    while (!isOpenParenthesis(ac)) {
                        postFix += ac.toString();
                        ac = (Character) st.pop();
                    }
                } else if (isOperator(c)) {// 操作符

                    if (!st.isEmpty()) {
                        Character ac = (Character) st.pop();
                        while (ac != null
                                && priority(ac.charValue()) > priority(c)) {
                            postFix += ac;
                            ac = (Character) st.pop();

                        }

                        if (ac != null) {
                            st.push(ac);
                        }
                    }
                    // 运算符入栈
                    st.push(c);
                } else {// 后缀表达式
                    postFix += c;
                }
            }
        }

        while (!st.isEmpty()) {
            postFix += st.pop().toString();
        }
        return postFix;
    }


    public double numberCalculate(String postFix) throws Exception {
        Stack st = new Stack();// 操作数栈
        for (int i = 0; i < postFix.length(); i++) {
            char c = postFix.charAt(i);
            if (isOperator(c)) {
                double d2 = Double.valueOf(st.pop().toString());
                double d1 = Double.valueOf(st.pop().toString());
                double d3 = 0;
                switch (c) {
                    case '+':
                        d3=d1+d2;
                        break;
                    case '-':
                        d3=d1-d2;
                        break;
                    case '*':
                        d3=d1*d2;
                        break;
                    case '/':
                        d3=d1/d2;
                        break;
                    case '%':
                        d3=d1%d2;
                        break;
                    case '^':
                        d3=Math.pow(d1, d2);
                        break;

                    default:
                        break;
                }

                st.push(d3);//将操作后的结果入栈
            }else{//当是操作数时，就直接进操作数栈
                st.push(c);
            }

        }

        return (double) st.pop();
    }

    private int priority(char c) {
        switch (c) {
            case '^':
                return 3;
            case '*':
            case '/':
            case '%':
                return 2;
            case '+':
            case '-':
                return 1;
        }
        return 0;
    }


    private boolean isOperator(char c) {
        if ('+' == c || '-' == c || '*' == c || '/' == c || '^' == c
                || '%' == c) {
            return true;
        }
        return false;
    }


    private boolean isCloseParenthesis(char c) {
        return ')' == c;
    }


    private boolean isOpenParenthesis(char c) {
        return '(' == c;
    }


}

