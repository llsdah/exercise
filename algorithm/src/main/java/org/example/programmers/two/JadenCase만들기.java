package org.example.programmers.two;

    import java.util.*;
public class JadenCase만들기 {

    class Solution {
        public String solution(String s) {
            String answer = "";

            // 공백 연속 가능,

            boolean empty = true;

            char[] c = s.toCharArray();
            //System.out.println((int)'A' - (int)'a');// -32
            //System.out.println((int)'A');

            for (int i = 0; i < c.length; i++ ) {
                if( c[i] == ' ' ) {
                    empty = true;
                } else if(empty && (c[i] >= 'a' && c[i] <= 'z') ){
                    c[i] = (char)((int)c[i] - 32);
                    empty = false;
                } else if( !empty && (c[i] >= 'A' && c[i] <= 'Z')){
                    c[i] = (char)((int)c[i] + 32);
                } else {
                    empty = false;
                }

            }

            return new String(c);
        }
    }
}
