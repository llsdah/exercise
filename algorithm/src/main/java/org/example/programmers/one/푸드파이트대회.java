package org.example.programmers.one;

import org.example.programmers.ProgrammersSolutionSample;
import java.util.*;

public class 푸드파이트대회 implements ProgrammersSolutionSample {
    @Override
    public boolean solution() {
        String result = Solution.solution(new int[]{1,2,3,6});
        System.out.println(result);
        return false;
    }

    class Solution {
        public static String solution(int[] food) {
            String answer = "";
            // food의 데이터가 홀수인경우 -1
            for(int i = 1; i<food.length; i++){
                if(food[i]%2 == 1){
                    food[i]--;
                }
                int cnt = food[i]/2;
                for(int j = 0; j<cnt; j++){
                    answer = answer+i;
                }
            }
            String reverse = new StringBuilder(answer).reverse().toString();
            answer = answer + "0" + reverse;

            return answer;
        }
    }
}
