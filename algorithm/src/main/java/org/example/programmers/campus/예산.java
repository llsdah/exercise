package org.example.programmers.campus;
import java.util.*;
import java.util.stream.IntStream;

public class 예산 {

    public int solution_my(int[] budgets, int M) {
        long sum = 0;
        int max = 0;
        for (int b : budgets) {
            sum += b;
            max = Math.max(max, b);
        }

        if (sum <= M) return max;

        int left = 0;
        int right = max;
        int answer = 0;
        while(left <= right) {
            int mid = (left + right) / 2;
            sum = 0;
            for (int b : budgets) {
                sum+= Math.min(mid,b);
            }

            if(sum <= M) {
                left = mid + 1;
                answer = mid; //현재 가능
            } else right = mid - 1;

        }

        return answer;
    }
    public int solution_old(int[] budgets, int M) {
        int answer = 0;
/** 1. 모든 요청이 배정될 수 있는 경우에는 요청한 금액을 그대로 배정합니다.
 2. 모든 요청이 배정될 수 없는 경우에는 특정한 정수 상한액을 계산하여 그 이상인 예산요청에는 모두 상한액을 배정합니다. 상한액 이하의 예산요청에 대해서는 요청한 금액을 그대로 배정합니다. */
        int n = budgets.length;
        //Queue<Integer> pq = new PriorityQueue<>(Collections.reverseOrder());
        int sum = 0;
        int max = 0;
        for (int item : budgets) {
            sum += item;
            max = Math.max(item, max);
        }
        if (sum <= M) return max;
        while (true) {
            long num = 0;
            for (int i = 0; i < n; i++) {
                if (max <= budgets[i]) {
                    num += max;
                } else {
                    num += budgets[i];
                }
            }

            if (num <= M) {
                return max;
            } else {
                max--;
            }

        }
    }

    public int solution_old2(int[] budgets, int M) {
        int answer = 0;
        int sum = 0;
        Queue<Integer> pq = new PriorityQueue<>(Collections.reverseOrder());
        for(int i :budgets) {
            pq.offer(i);
            sum+= i;
        }
        if(sum <= M) return pq.poll();

        while(sum > M) {
            int num = pq.peek() - 1;

            while(true) {
                int temp = pq.poll();
                pq.offer(num);
                if ( temp <= num) {
                    break;
                }
                else {
                    sum = sum - temp;
                    sum += num;
                }

            }

        }

        return pq.poll();
    }

    // 이미 정렬되어 있는 것에 대한 탐색은 이분 탐색
    public int solution(int[] budgets, int M) {
        int min = 1;
        int max = IntStream.of(budgets).max().orElse(0);
        int answer = 0;

        while(min <= max) {
            final int mid = (min + max) /2;

            int sum = IntStream.of(budgets)
                    .map(s -> Math.min(mid,s))
                    .sum();

            if ( sum <= M) {
                min = mid + 1;
                answer = mid;
            } else {
                max = mid - 1;
            }

        }

        return answer;
    }
}