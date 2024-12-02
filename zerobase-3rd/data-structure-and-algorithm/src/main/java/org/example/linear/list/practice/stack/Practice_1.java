package org.example.linear.list.practice.stack;

import java.util.Stack;

/**
 * 문자열 뒤집기
 */
public class Practice_1 {

    public static String reverseString(String str){
        Stack stack = new Stack();
        String result = "";

        for (int i = 0; i < str.length(); i++) {
            stack.add(str.toCharArray()[i]);
        }

        while (!stack.isEmpty()) {
            result = result + stack.pop();
        }

        return result;
    }
    public static void main(String[] args) {
        String str = reverseString("abcde");
        System.out.println("str = " + str);
        
        str = reverseString("1 2 3 4 5");
        System.out.println("str = " + str);
        
    }
}
