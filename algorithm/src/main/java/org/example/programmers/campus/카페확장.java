package org.example.programmers.campus;
import java.util.*;
public class 카페확장 {
    public int solution_my(int[] menu, int[] order, int k) {
        int answer = 0;

        Queue<Integer> que = new LinkedList<>();

        for (int i =0; i<order.length;i++) {
            int during = menu[order[i]];

            // 사이즈가 더 작으면 채워 넣어야해
            for ( int n = 0 ; n< during;n++) {
                que.offer(i+1);
            }
            if ( i == order.length - 1 ) break; // 마지막이면 그냥 종료
            int remain = k * (i+1) - que.size();
            if (remain > 0) {
                for ( int n = 0 ; n<remain;n++) {
                    que.offer(0);
                }
            }
        }

        int cnt = 0;

        int person = 0;
        int index = 0;

        int max = 0;
        boolean flag = false;
        while(!que.isEmpty()) {
            int number = que.poll();
            if (number == 0) {
                person = 0;
            } else {
                if (cnt % k == 0 ) {
                    index ++;
                    person = index - number + 1;
                }
            }
            //System.out.println("cnt:"+cnt+"index:"+index+"person:"+person);
            max = Math.max(max, person);
            cnt ++;
            if ( cnt / k >= order.length ) break;
        }

        //System.out.println(max);
        return max;
    }


    public int solution(int[] menu, int[] order, int k) {
        int answer = 0;

        Queue<Integer> que = new LinkedList<>();
        int busy = -1; // 현재 음료의 완성 시간
        int index = 0;

        // 총대기 시간은 손님 * (카페입장시간 -1)
        for (int time = 0; time < k * (order.length-1); time++) {
            // 손님 도착시 주문 메뉴 추가
            if ( k * index == time) que.offer(order[index++]);
            if (busy == time) {
                que.poll();
                busy = -1;
            }
            if (busy == -1 && !que.isEmpty()) {
                busy = time + menu[que.peek()];
            }
            answer = Math.max(answer, que.size());
        }

        return answer;
    }
}
