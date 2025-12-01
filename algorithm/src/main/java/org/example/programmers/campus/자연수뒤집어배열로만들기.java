package org.example.programmers.campus;
import java.util.*;
public class 자연수뒤집어배열로만들기 {
    public int[] solution_my(long n) {
        String str = n + "";
        System.out.println(str);
        int[] answer = new int[str.length()];

        for (int i = str.length() - 1; i >= 0; i--) {
            answer[str.length() - 1 - i] = str.toCharArray()[i] - '0';
        }

        return answer;
    }

    public int[] solution(long n) {
        //뒤집기
        List<Integer> list = new LinkedList<>();
        while(n>0) {
            list.add((int)n % 10 );
            n/=10;
        }
        return list.stream().mapToInt(Integer::intValue).toArray();
    }
}