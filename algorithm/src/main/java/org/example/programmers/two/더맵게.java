package org.example.programmers.two;

    import java.util.*;
public class 더맵게 {

    class Solution {
        public int solution(int[] scoville, int K) {
            int answer = 0;
            // 섞은 음식의 스코빌 지수 = 가장 맵지 않은 음식의 스코빌 지수 + (두 번째로 맵지 않은 음식의 스코빌 지수 * 2)

            Queue<Integer> que = new PriorityQueue<>();

            for (int i : scoville){
                que.add(i);
            }

            while(que.size() >= 2){
                int min = que.poll();
                if (min >= K){
                    break;
                }

                int next = que.poll();
                int temp = min + (next * 2);

                que.add(temp);
                answer ++;
            }

            if (que.size() == 1 && que.peek() >= K) {
                return answer;
            } else if (que.size() == 1 && que.peek() < K ){
                return -1 ;
            }

            return answer;
        }
    }
}
