package org.example.programmers.campus;

import java.util.*;
public class 끝말잇기 {
    public boolean solution_my(String[] words) {
        boolean answer = true;
        Set<String> set = new HashSet<>();

        char ch = words[0].toCharArray()[0];
        for (String item : words) {
            if (!set.add(item)) return false;

            if (ch != item.toCharArray()[0]) return false;

            ch = item.toCharArray()[item.length() - 1];
        }

        return answer;
    }

    public boolean solution(String[] words) {

        Set<String> set = new HashSet<>();
        set.add(words[0]);
        char last = words[0].charAt(words[0].length() -1);
        for (int i = 1; i < words.length;i++) {
            String w = words[i];
            char fir = w.charAt(0);
            if (fir != last) return false;
            last = w.charAt(w.length()-1);
            set.add(w);

        }

        return false;
    }
}