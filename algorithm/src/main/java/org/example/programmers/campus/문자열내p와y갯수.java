package org.example.programmers.campus;
import java.util.*;
public class 문자열내p와y갯수 {
    boolean solution(String s) {
        boolean answer = true;
        s = s.toLowerCase();
        // [실행] 버튼을 누르면 출력 값을 볼 수 있습니다.
        System.out.println("Hello Java");
        Queue<Character> qp = new LinkedList<>();
        Queue<Character> qy = new LinkedList<>();

        char[] carr = s.toCharArray();

        for (char c : carr) {
            if(c == 'p') qp.offer(c);
            if(c == 'y') qy.offer(c);
        }

        return qp.size() == qy.size();
    }
}
