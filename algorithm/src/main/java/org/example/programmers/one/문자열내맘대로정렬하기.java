package org.example.programmers.one;

    import java.util.*;
public class 문자열내맘대로정렬하기 {

    class Solution {
        public String[] solution(String[] strings, int n) {
            String[] answer = {};

            Arrays.sort(strings, Comparator.comparing((String s) -> s.charAt(n))
                    .thenComparing( s -> s));


            return strings;
        }
    }
}
