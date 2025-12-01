package org.example.programmers.campus;
import java.util.*;
public class 더맵게 {
    public int solution(int[] scoville, int K) {
        int answer = 0;
        // 섞은 음식의 스코빌 지수 = 가장 맵지 않은 음식의 스코빌 지수 + (두 번째로 맵지 않은 음식의 스코빌 지수 * 2)

        Arrays.sort(scoville);

        Queue<Integer> pq = new PriorityQueue();
        int n = scoville.length;
        for (int i = 0; i<n;i++) {
            pq.offer(scoville[i]);
        }

        while(pq.size() >= 2) {

            int n1 = pq.poll();
            int n2 = pq.poll();

            if (n1 < K) {
                int num = n1 + (n2*2);
                pq.offer(num);
                answer++;
            } else {
                break;
            }
        }

        if (pq.isEmpty()){
            return answer;
        }

        int num = pq.peek();

        if (num >= K) {
            return answer;
        }else {
            return -1;
        }

    }
}
