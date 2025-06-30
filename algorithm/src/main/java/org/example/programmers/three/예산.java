package org.example.programmers.three;

    import java.util.*;
public class 예산 {

    class Solution {
        public int solution(int[] budgets, int M) {
            int answer = 0;
            int left = 0;
            int right = Arrays.stream(budgets).max().getAsInt();

            while(left <= right) {

                int min = (left + right) / 2;
                int sum = 0;

                for(int item : budgets) {
                    sum += Math.min(min, item);
                }

                if ( sum <= M) {
                    answer = min;
                    left = min + 1;
                } else {
                    right = min - 1;
                }

            }

            return answer;
        }
    }

    class Solution1 {

        public int solution1(int[] budgets, int M) {
            int answer = 0;
        /*
        1. 모든 요청이 배정될 수 있는 경우에는 요청한 금액을 그대로 배정합니다.
2. 모든 요청이 배정될 수 없는 경우에는 특정한 정수 상한액을 계산하여 그 이상인 예산요청에는 모두 상한액을 배정합니다.
   상한액 이하의 예산요청에 대해서는 요청한 금액을 그대로 배정합니다.
   */
            Queue<Integer> que = new PriorityQueue<>(Comparator.reverseOrder());
            int len = budgets.length;
            int sum = 0;
            int max = 0;
            for(int i = 0 ; i < len;i++) {
                sum+=budgets[i];
                max = Math.max(max, budgets[i]);
                que.offer(budgets[i]);
            }

            if(sum <= M) {
                return max;
            }
            int count = sum - M;
            System.out.println(count);
            while(count != 0) {
                int num = que.poll();

                que.offer(num-1);
                count--;

            }

            //Arrays.sum(budgets)
            return que.poll() -1;
        }
    }
}
