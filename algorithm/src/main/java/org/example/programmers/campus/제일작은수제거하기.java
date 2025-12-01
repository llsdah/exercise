package org.example.programmers.campus;

import java.util.*;
public class 제일작은수제거하기 {
    public int[] solution_my(int[] arr) {
        int[] answer = new int[arr.length-1];
        if (arr.length == 1) {
            return new int[]{-1};
        }

        List<Integer> list = new ArrayList<>();
        int min = -1;
        for (int i : arr) {
            if(min == -1 || min > i) min = i;
            list.add(i);
        }
        int idx = list.indexOf(min);
        //System.out.println("idx : "+idx);
        list.remove(idx);


        for(int i = 0;i<list.size();i++){
            answer[i] = list.get(i);
        }

        return answer;
    }

    public int[] solution(int[] arr) {
        if (arr.length== 1) return new int[]{-1};
        int min = Arrays.stream(arr).min().getAsInt();
        return Arrays.stream(arr)
                .filter(a -> a != min)
                .toArray();
    }

}
