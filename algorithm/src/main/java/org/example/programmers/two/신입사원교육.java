package org.example.programmers.two;

    import java.util.*;
public class 신입사원교육 {

    class Solution {
        public int solution(int[] ability, int number) {
            int answer = 0;

            Queue<Integer> que = new PriorityQueue<>();
            for (int i : ability) {
                que.offer(i);
            }

            for (int i =0; i < number; i ++ ) {
                int p1 = que.poll();
                int p2 = que.poll();

                int sum = p1 + p2;
                que.offer(sum);
                que.offer(sum);
            }

            int sum = 0 ;
            while (!que.isEmpty()) {
                sum += que.poll();
            }

            return sum;
        }
    }
}
