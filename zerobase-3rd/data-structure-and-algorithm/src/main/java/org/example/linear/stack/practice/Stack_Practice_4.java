package org.example.linear.stack.practice;

import java.util.Stack;

/**
 * 문자열 비교
 * 단, #은 지우기로 바로 이전의 문자를 삭제하는 기능
 *
 * tree , th#ree 면 true
 * wo#w, ww#o false
 */
public class Stack_Practice_4 {

    public static void main(String[] args) {
        String s1 = "tree";
        String s2 = "th#ree";
        System.out.println("stringCompare(s1,s2) = " + stringCompare(s1, s2));
    }

    private static boolean stringCompare(String s1, String s2) {
        s1 = doBackspace(s1);
        s2 = doBackspace(s2);
        return s1.equals(s2);
    }

    private static String doBackspace(String s1) {
        Stack stack = new Stack();

        for (char c : s1.toCharArray()) {
            if (c == '#' && !stack.empty()) {
                stack.pop();
            } else {
                stack.push(c);
            }
        }
        return String.valueOf(stack);
    }


}
