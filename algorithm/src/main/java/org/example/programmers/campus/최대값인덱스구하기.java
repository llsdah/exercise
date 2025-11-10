package org.example.programmers.campus;

import java.util.LinkedList;
import java.util.List;

public class 최대값인덱스구하기 {
    public int[] solution(int[] arr) {

        List<Integer> list = new LinkedList<>();
        int max = arr[0];
        for (int i = 0; i < arr.length; i++) {
            if (max < arr[i]) {
                list = new LinkedList<>();
                max = arr[i];
                list.add(i);
            } else if (max == arr[i]) {
                list.add(i);
            }
        }

        int[] answer = list.stream().
                mapToInt(Integer::intValue).toArray();

        return answer;
    }
}
