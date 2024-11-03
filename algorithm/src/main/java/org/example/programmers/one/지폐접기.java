package org.example.programmers.one;

import org.example.programmers.ProgrammersSolutionSample;

import java.util.Arrays;

public class 지폐접기 implements ProgrammersSolutionSample {
    @Override
    public boolean solution() {

        Object result = Solution.solution(null,null);
        return false;
    }

    class Solution {
        public static int solution(int[] wallet, int[] bill) {
            int answer = 0;
            Arrays.sort(wallet);
            Arrays.sort(bill);
            while(true){
                if( wallet[0] >= bill[0] && wallet[1] >= bill[1]) break;

                answer++;
                bill[1]/=2;
                System.out.println("half bill "+bill[0]+":"+bill[1]);
                Arrays.sort(bill);

            }


            return answer;
        }
    }
}
