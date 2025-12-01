package org.example.programmers.campus;
import java.util.*;
import java.util.stream.Collectors;

public class 위장 {
    public int solution_my(String[][] clothes) {
        int answer = 1;
        Map<String, Integer> map = new HashMap<>();

        for (String[] items : clothes) {
            map.put(items[1], map.getOrDefault(items[1], 0) + 1);
        }

        for (String key : map.keySet()) {
            answer *= (map.get(key) + 1);
        }

        // 전체 다 안하는 경우
        return answer - 1;
    }

    public int solution(String[][] clothes) {
        int answer = Arrays.stream(clothes)
                .map(c -> c[1])
                .distinct()
                .map(type -> ((int)Arrays.stream(clothes)
                        .filter(c -> c[1].equals(type)).count()))
                .map(c -> c + 1)
                .reduce(1, (c,n) -> c * n);

        return answer;
    }
}
