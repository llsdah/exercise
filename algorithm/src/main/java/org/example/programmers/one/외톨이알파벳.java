package org.example.programmers.one;

public class 외톨이알파벳 {

    import java.util.*;
    class Solution {
        public String solution(String input_string) {
            String answer = "";
            int[] e = new int[26];
            boolean[] lone = new boolean[26];
            char[] c= input_string.toCharArray();
            char prev = ' ';
            for (int i = 0; i < c.length; i++) {
                // 재등장
                if(e[c[i]-'a'] >= 1 && c[i] != prev) {
                    lone[c[i]-'a']  = true;
                }
                e[c[i]-'a']++;
                prev = c[i];

            }

            for (int i = 0; i <26;i++) {
                if(lone[i]) {
                    answer+= (char)('a'+i);
                }
            }

            if (answer.length() == 0) answer = "N";

            return answer;
        }
    }
}
