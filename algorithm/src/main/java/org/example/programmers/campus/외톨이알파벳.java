package org.example.programmers.campus;
import java.util.*;
public class 외톨이알파벳 {
    public String solution_my(String input_string) {
        String answer = "";
        char[] arr = input_string.toCharArray();
        Map<Character, Integer> map = new HashMap<>();
        char main = arr[0];
        // 중복제외 넣고
        for (int i = 1; i< arr.length; i++) {

            if(arr[i] == main) {
                if(i == arr.length-1) map.put(main,map.getOrDefault(main,0)+1);
                continue;
            }
            map.put(main,map.getOrDefault(main,0)+1);
            main = arr[i];
            if(i == arr.length-1) map.put(main,map.getOrDefault(main,0)+1);
        }

        for (char c : map.keySet()) {
            //System.out.println("c : "+c+", "+map.get(c));
            if(map.get(c) >= 2) answer+=c;
        }

        return answer.length() == 0 ? "N" : answer;
    }

    public String solution(String input_string) {
        String answer = "";
        // 알파벳 등장 횟수 카욱트
        int[] cnt = new int[26];
        boolean[] isLone = new boolean[26];
        char prev = ' ';

        for (int i = 0; i< input_string.length(); i++) {
            char cur = input_string.charAt(i);
            if (cnt[cur - 'a'] >= 1 && cur != prev) {
                isLone[cur - 'a'] = true;
            }
            cnt[cur - 'a'] ++;
            prev = cur;
        }

        for (int i = 0; i < 26; i ++) {
            if (isLone[i] == true) answer +=('a'+i);
        }
        if(answer.length() == 0) return "N";

        return answer;
    }
}
