package org.example.linear.stack.practice;

import java.util.Stack;

/**
 * 후위표기법 연산
 * 전위표기법 (1+2)*3 ->
 * 12+3*
 *
 * 1+2*3
 * -> 123*+
 *
 */
public class Stack_Practice_3 {

    public static void main(String[] args) {
        System.out.println("args = " + caculate("2 2 *"));
        System.out.println("args = " +caculate("2 2 -"));
        System.out.println("args = " +caculate("2 2 +"));
        System.out.println("args = " +caculate("2 2 /"));
        System.out.println("args = " +caculate("1 1 + 2 * 3 2 5 /"));
    }

    private static double caculate(String s) {
        Stack<Double> stack = new Stack();

        for (String str : s.split(" ")) {
            if (str.equals("+")) {
                stack.push(stack.pop() + stack.pop());
            } else if (str.equals("-")) {
                stack.push(-stack.pop() + stack.pop());
            } else if (str.equals("*")) {
                stack.push(stack.pop() * stack.pop());
            } else if (str.equals("/")) {
                stack.push((1/stack.pop())* stack.pop());
            } else {
                stack.push(Double.parseDouble(str));
            }
        }
        return stack.pop();
    }

}
