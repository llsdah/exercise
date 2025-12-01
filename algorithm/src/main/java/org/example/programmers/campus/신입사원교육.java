package org.example.programmers.campus;
import java.util.*;
public class 신입사원교육 {
    public int solution_my(int[] ability, int number) {
        int answer = 0;

        Queue<Integer> pq = new PriorityQueue<>();
        for (int i : ability) {
            pq.offer(i);
        }

        while(number > 0) {
            number --;
            int a = pq.poll();
            int b = pq.poll();
            int sum = a+b;
            pq.offer(sum);
            pq.offer(sum);
        }

        while(!pq.isEmpty()) {
            answer += pq.poll();
        }

        return answer;
    }
}
