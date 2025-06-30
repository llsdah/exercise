package org.example.programmers.three;

    import java.util.*;
public class 운영체제 {
    class Solution {
        public long[] solution(int[][] program) {
            long[] answer = new long[11];
            // 낮을 수록 우선 순위가 높은
            // 정해진 실행시간, 대기 -> 실행 -> 종료
            // 우선 순위, 시작시간, 종료 시간
            // 시간 순 정렬
            Arrays.sort(program, (a1, a2) -> {
                if (a1[1] != a2[1]) return a1[1] - a2[1];
                else return a1[0] - a2[0];
            } );

            int time = program[0][1];
            Queue<int[]> que = new PriorityQueue<>(
                    (a, b) -> a[0] == b[0] ? a[1] - b[1] : a[0] - b[0]
            );
            int index = 0;
            int n = program.length;

            while( index < n || !que.isEmpty()) {

                while(index < n && program[index][1] <= time) {
                    que.offer(program[index]);
                    index++;
                }

                if (!que.isEmpty()) {
                    int[] num = que.poll();

                    //System.out.println(Arrays.toString(num));
                    answer[num[0]] += (time - num[1]);
                    time += num[2];
                } else {
                    time = program[index][1];
                }
                //if (index == 3) break;

            }

            answer[0] = time;

            return answer;
        }
    }

}
