package org.example.programmers.one;

import java.util.*;

public class 끝말잇기 {

    class Solution {
        public boolean solution(String[] words) {
            boolean answer = true;

            char[] before = null;
            Set<String> set = new HashSet<>();
            for (String str : words) {
                set.add(str);
                if ( before != null ) {
                    char[] charArr = str.toCharArray();
                    if (before[before.length-1] != charArr[0] ){
                        return false;
                    }
                }
                before = str.toCharArray();
                //System.out.println("str : "+str +", "+new String(before));

            }

            System.out.println("str : "+set.size()+", "+ words.length);

            return set.size() == words.length;
        }
    }
}
