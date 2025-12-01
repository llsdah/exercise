package org.example.programmers.campus;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class 가장큰수 {

    public String solution_my(int[] numbers) {
        String answer = "";
        int n = numbers.length;
        if (n==1) return String.valueOf(numbers[0]);
        String[] strs = new String[n];
        for (int i = 0 ; i <n;i++) {
            strs[i] = numbers[i]+"";
        }
        Arrays.sort( strs, (a,b)-> {
            String ab = a+b;
            String ba = b+a;
            return ba.compareTo(ab);
        });
        /**
         // 시간 초과도 안됨
         for(int i = 0 ; i < n-1;i++) {
         for (int k = i; k < n;k++ ) {

         String ik = strs[i] +strs[k];
         String ki = strs[k] +strs[i];

         if (ik.compareTo(ki) < 0) {
         String temp = strs[i];
         strs[i] = strs[k];
         strs[k] = temp;
         }
         }
         }

         **/
        if (strs[0].equals("0")) return "0";
        StringBuilder sb = new StringBuilder();
        for (String str : strs) {
            sb.append(str);
        }

        return sb.toString();
    }

    public String solution(int[] numbers) {
        String answer = IntStream.of(numbers)
                .mapToObj(String::valueOf)
                .sorted((s1,s2) -> (s2+s1).compareTo(s1+s2))
                .collect(Collectors.joining());

        return answer;
    }
}
