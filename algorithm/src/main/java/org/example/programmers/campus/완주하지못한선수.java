package org.example.programmers.campus;

import java.util.*;

public class 완주하지못한선수 {
    public String solution_my(String[] participant, String[] completion) {
        String answer = "";
        // 동명이인 set 불가
        Map<String, Integer> map = new HashMap<>();
        for (String item : participant) {
            map.put(item, map.getOrDefault(item, 0) + 1);
        }
        for (String item : completion) {
            int n = map.get(item) - 1;
            if (n == 0) {
                map.remove(item);
            } else {
                map.put(item, n);
            }
        }

        return map.keySet().iterator().next();
    }

    public String solution(String[] participant, String[] completion) {
        //List<String> players = new LinkedList<>();
        List<String> players = new ArrayList<>();
        for (String p : participant) players.add(p);
        for (String c : completion) players.remove(c);

        Map<String, Integer> map = new HashMap<>();
        for (String p : participant) {
            map.put(p, map.getOrDefault(p,0)+1);
        }
        for (String p : participant) {
            int n = map.get(p) -1;
            if (n == 0) map.remove(p);
            else map.put(p,n);

        }

        return map.keySet().iterator().next();
    }
}