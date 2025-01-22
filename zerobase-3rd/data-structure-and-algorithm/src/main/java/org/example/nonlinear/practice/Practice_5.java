package org.example.nonlinear.practice;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.Queue;

/**
 * x 축 상에 앞 뒤로만 이동할 수 있는 로봇이 0 위치에 놓여 있다.
 *
 * forward 방향 a 만큼 무조건 이동
 * backward 방향 b 만큰 무조건 이동
 * backward 연속 2번은 못 움직임
 * forbidden 갈 수 없다.
 * 음수도 못간다
 *
 *  목적지 x 로 몇번 이동하면 갈 수 있는지 확인
 *
 * forbidden = 14, 4, 18, 1, 15 , a = 3, b = 15, x = 9, result 3
 * forbidden = 1, 6, 2, 14, 5, 17, 4 , a = 16, b = 9, x = 7  result 2
 * forbidden = 8, 3, 16, 6, 12, 20 , a = 15, b = 13, x = 11 result -1
 *
 * BFS로 시작
 *
 */
public class Practice_5 {

    public static void main(String[] args) {

        int[] forbidden = {14,4,18,1,15};

        System.out.println(solution(forbidden, 3, 15, 9));

        forbidden = new int[]{1, 6, 2, 14, 5, 17, 4};
        System.out.println(solution(forbidden, 16, 9, 7));

        forbidden = new int[]{8, 3, 16, 6, 12, 20};
        System.out.println(solution(forbidden, 15, 13, 11));



    }

    private static int solution(int[] forbidden, int a, int b, int x) {
        int cnt = 0;
        // 뒤로가는것을 2번 연속 못하길래!!!
        int limit = x + a + b;

        Queue<int[]> que = new LinkedList<>();
        que.offer(new int[]{0,0});

        HashSet<int[]> visited = new HashSet<>();

        for (int pos : forbidden) {
            // { 방향, 위치 }
            visited.add(new int[]{0,pos});
            visited.add(new int[]{1,pos});
            limit = Math.max(limit, pos + a + b);
        }

        while (!que.isEmpty()) {
            for (int i = 0; i< que.size(); i++) {

                int[] cur = que.poll();

                int dir = cur[0];
                int pos = cur[1];

                if (pos == x) {
                    return cnt;
                }

                int[] forward = new int[]{0, pos + a};

                if (pos + a <= limit && visited.add(forward)) {
                    que.offer(forward);
                }

                int[] backward = new int[]{1, pos - b};

                // 뒤로 2번 못감, 음수 아님, 방문 안함
                if (dir == 0 && pos - b >= 0 && visited.add(backward)){
                    que.offer(backward);
                }

            }
            // 이동 횟수 카운트
            cnt ++;

        }

        return -1;
    }



}
