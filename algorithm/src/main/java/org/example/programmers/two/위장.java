package org.example.programmers.two;

    import java.util.*;
public class 위장 {
    class Solution {
        public int solution(String[][] clothes) {
            int answer = 1;
            Map<String,Integer> map = new HashMap<>();

            for (String[] strs : clothes) {
                map.put(strs[1], map.getOrDefault(strs[1],0) + 1);
            }

            for (String key : map.keySet()) {
                answer *= (map.get(key)+1);
            }


            return answer - 1;
        }
    }
}
