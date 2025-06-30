package org.example.programmers.two;

    import java.util.*;
public class 올바른괄호 {

    class Solution {
        boolean solution(String s) {
            boolean answer = true;

            // [실행] 버튼을 누르면 출력 값을 볼 수 있습니다.
            System.out.println("Hello Java");
            char[] sArr = s.toCharArray();
            Stack<Character> stack = new Stack<>();
            stack.push(sArr[0]);
            for(int i = 1; i < sArr.length; i++) {
                if (sArr[i] == ')') {
                    if (stack.size() == 0 || stack.peek() != '(') {
                        return false;
                    }else {
                        stack.pop();
                    }
                }else {
                    stack.push(sArr[i]);
                }
            }

            return stack.size() == 0;
        }
    }
}
