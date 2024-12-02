package org.example.linear.list.practice.stack;

import java.util.Stack;

/**
 * 괄호의 짝 검사
 */
public class Practice_2 {

    public static void main(String[] args) {
        checkParenthesis("(");
        checkParenthesis(")");
        checkParenthesis("()");
        checkParenthesis("()()()");
        checkParenthesis("(())()");
        checkParenthesis("(((()))");
    }

    private static void checkParenthesis(String str) {
        Stack stack = new Stack();
        boolean checkFlag = true;
        
        for (String s : str.split("")){
            if (s.equals("(")) {
                stack.push(s);
            } else {
                if (stack.isEmpty()) {
                    checkFlag = false;
                    break;
                } else {
                    stack.pop();
                }
            }
        }
        
        if (checkFlag && stack.isEmpty()) {
            System.out.println("checkFlag = " + checkFlag);
        } else {
            System.out.println("checkFlag = " + false);
        }
        
    }
}
