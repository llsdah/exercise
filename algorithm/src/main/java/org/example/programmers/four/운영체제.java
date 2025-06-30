package org.example.programmers.four;
    import java.util.*;

public class 운영체제 {

    class Solution1 {
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

    class Solution {
        public long[] solution(int[][] program) {
            long[] answer = new long[11];
            // 낮을 수록 우선 순위가 높은
            // 정해진 실행시간, 대기 -> 실행 -> 종료
            // 우선 순위, 시작시간, 종료 시간
            List<int[]> list = new ArrayList<>();
            for (int i = 0; i < program.length;i++){
                //System.out.println(Arrays.toString(program[i]));
                list.add(program[i]);

            }

            // 현재 흐른시간
            int time = 0;
            boolean flag = false;

            while(list.size() != 0) {
                int index = -1;
                int prio = Integer.MAX_VALUE;
                int wait = 0;
                // 다음 순서 찾기
                for (int i = 0; i <list.size();i++) {
                    int[] num = list.get(i);
                    // 해당 시간이 충족한다면.
                    if (time >= num[1] && num[0] < prio) {
                        index = i;
                        wait = time - num[1];
                        prio = num[0];
                        //System.out.println("prio : "+num[0]+", wait : "+wait);
                    }
                }
                if (index == -1) {
                    // 다음 시작 가능한 작업의 최소 시작 시간을 찾음
                    int nextStart = Integer.MAX_VALUE;
                    for (int[] task : list) {
                        nextStart = Math.min(nextStart, task[1]);
                    }
                    time = nextStart;
                    continue;
                }
                // 다음 순서 찾았다.
                time += list.get(index)[2];
                answer[list.get(index)[0]] += wait;
                //System.out.println(Arrays.toString(list.get(index)));
                //System.out.println();
                list.remove(index);
            }
            answer[0] = time;

            return answer;
        }
    }


}
