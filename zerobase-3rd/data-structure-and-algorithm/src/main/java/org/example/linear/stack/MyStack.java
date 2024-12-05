package org.example.linear.stack;

import java.util.Stack;

/**
 * 스택
 * 후입 선출 구로 Last in First Out
 * 입력순서를 역순으로 처리되어야 하는 경우 사용
 * - 함수 콜 스택, 수식계산, 인터럽트 처리 등 
 * push - pop
 * 
 */

public class MyStack {
    public static void main(String[] args) {
        Stack stack = new Stack();
        stack.push(1);
        stack.push(2);
        stack.push(3);
        stack.push(4);
        stack.push(5);
        System.out.println("stack = " + stack);

        stack.pop();
        stack.pop();
        stack.pop();
        System.out.println("stack = " + stack);
        stack.peek();
        stack.contains(2);
        stack.size();
        stack.empty();
        
        stack.clear();
        System.out.println("stack = " + stack);
        
    }
}
