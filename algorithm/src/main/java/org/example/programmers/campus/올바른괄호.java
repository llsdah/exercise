package org.example.programmers.campus;
import java.util.*;
public class 올바른괄호 {
    boolean solution_my(String s) {
        boolean answer = true;

        // [실행] 버튼을 누르면 출력 값을 볼 수 있습니다.
        char[] arr = s.toCharArray();
        Queue<Character> que = new LinkedList<>();
        for (char ch : arr) {
            if (ch == '(') {
                que.offer(ch);
            } else {
                if (que.peek() != null && que.peek() == '(') que.poll();
                else return false;
            }
        }

        return que.size() == 0;
    }

    boolean solution(String s) {
        Stack<Character> stack = new Stack<>();
        for (char c : s.toCharArray()) {
            if (c == '(') stack.push(c);
            else {
                if(stack.isEmpty()) return false;
                stack.pop();
            }
        }

        return false;
    }
}