package org.example.programmers.two;

public class 주식가격 {
    class Solution {
        public int[] solution(int[] prices) {
            int[] answer = new int[prices.length];

            for (int i = 0; i< prices.length;i++) {
                for (int k = i+1; k<prices.length;k++) {
                    if (prices[i] <= prices[k]){
                        answer[i] ++;
                    } else {
                        answer[i]++;
                        break;
                    }
                }
            }
            return answer;
        }
    }
}
