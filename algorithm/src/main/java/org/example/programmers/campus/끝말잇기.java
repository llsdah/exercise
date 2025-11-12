package org.example.programmers.campus;

import java.util.*;
public class 끝말잇기 {
    public boolean solution(String[] words) {
        boolean answer = true;
        Set<String> set = new HashSet<>();

        char ch = words[0].toCharArray()[0];
        for (String item : words) {
            if(!set.add(item)) return false;

            if(ch != item.toCharArray()[0]) return false;

            ch = item.toCharArray()[item.length()-1];
        }

        return answer;
    }
}
