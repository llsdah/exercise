package org.example.programmers.campus;
import java.util.*;

public class 문자열내마음대로정렬하기 {
    public String[] solution_my(String[] strings, int n) {
        String[] answer = {};
        // 해당 인덱스번째로 정렬하는구나
        int num = strings.length;
        for (int i = 0; i < num - 1; i++) {
            for (int k = i + 1; k < num; k++) {
                char be = strings[i].toCharArray()[n];
                char ne = strings[k].toCharArray()[n];
                //System.out.println("default : "+ strings[i]+","+(be+", ") +":"+strings[k]+", "+ (ne+","));
                if (be > ne) {
                    String temp = strings[i];
                    strings[i] = strings[k];
                    strings[k] = temp;
                    //System.out.println("true : "+ (be+", ") +":"+ (ne+","));
                } else if (be == ne) {
                    String[] strs = new String[]{strings[i], strings[k]};
                    Arrays.sort(strs);
                    strings[i] = strs[0];
                    strings[k] = strs[1];
                }
            }
        }

        //System.out.println(Arrays.toString(strings));
        return strings;
    }
    public String[] solution(String[] strings, int n) {
        Arrays.sort(strings, (s1,s2) -> {
            if(s1.charAt(n)==s2.charAt(n)) return s1.compareTo(s2);
            return s1.charAt(n) - s2.charAt(n);
        });
        return strings;
    }

    }
