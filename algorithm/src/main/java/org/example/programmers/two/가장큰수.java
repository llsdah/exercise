package org.example.programmers.two;
    import java.util.*;

public class 가장큰수 {
    class Solution {
        public String solution(int[] numbers) {
            String answer = "";

            String[] strs = new String[numbers.length];
            for (int i = 0; i<numbers.length;i++){
                strs[i] = numbers[i]+"";
            }
            Arrays.sort(strs, new Comparator<String>(){
                public int compare(String s1, String s2){
                    return (s2+s1).compareTo(s1+s2);
                }
            });

        /*
        for (int i = 0; i <numbers.length;i++){
            for (int k = i+1; k < numbers.length ; k ++){
                String s1 = strs[i];
                String s2 = strs[k];

                if ((s1+s2).compareTo(s2+s1) < 0) {

                    strs[i] = s2;
                    strs[k] = s1;
                }

            }
        }
        */
            for (int i = 0; i <numbers.length;i++){
                answer += strs[i];
            }

            if(answer.charAt(0)=='0') return "0";

            return answer;
        }
    }
    class Solution1 {
        public String solution(int[] numbers) {
            String answer = "";

            String[] strs = new String[numbers.length];
            for (int i = 0; i<numbers.length;i++){
                strs[i] = numbers[i]+"";
            }

            for (int i = 0; i <numbers.length;i++){
                for (int k = i+1; k < numbers.length ; k ++){
                    String s1 = strs[i];
                    String s2 = strs[k];

                    if ((s1+s2).compareTo(s2+s1) < 0) {

                        strs[i] = s2;
                        strs[k] = s1;
                    }

                }
            }



            for (int i = 0; i <numbers.length;i++){
                answer += strs[i];
            }

            return answer;
        }
    }
}
