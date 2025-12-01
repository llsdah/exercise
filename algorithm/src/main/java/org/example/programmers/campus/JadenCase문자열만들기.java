package org.example.programmers.campus;
import java.util.*;

public class JadenCase문자열만들기 {
    public String solution_my(String s) {
        String answer = "";

        Queue<Character> que = new LinkedList<>();
        s = s.toLowerCase();
        // 98 : 66 , 32차이
        // System.out.println(('a'+1)+" : "+('A'+1));
        char[] arr = s.toCharArray();

        for (char c : s.toCharArray()) {
            que.offer(c);
        }

        boolean flag = true;
        while(!que.isEmpty()) {
            char c = que.poll();
            if(c == ' ') {
                flag = true;
                answer += c;
                continue;
            }
            if(flag) {
                // 소문자 사이인지 체크
                if ( c >= 'a' && c <= 'z') {
                    answer += (char)(c-32);
                }else {
                    answer += c;
                }
                flag = false;
            } else {
                answer += c;
            }
        }

        return answer;
    }

    public String solution(String s) {
        StringBuilder sb = new StringBuilder();

        String s2 = s.toLowerCase();
        char last = ' ';
        for (char c : s2.toCharArray()) {
            if (last == ' ') c = Character.toUpperCase(c);
            sb.append(c);
            last = c;

        }
        return sb.toString();
    }
}
