package org.example.programmers.two;

import java.util.*;
public class 서버증설횟수 {
    public int solution(int[] players, int m, int k) {
        int answer = 0;
        int cnt = 0;
        Queue<Integer> server = new LinkedList<>();

        server.add(0);

        for (int i = 0; i < players.length; i++) {
            int play = players[i];
            int serverCnt = server.poll();
            if (play >= (m + m * serverCnt)) {

                int add = play / m - serverCnt;
                cnt += add;
                addServer(server, k - 1, add);
                //System.out.println(i+" add : "+add);
            }

            // 사이즈가 0 인경우
            if (server.size() == 0) {
                server.add(0);
            }

        }

        //System.out.println("cnt : "+ cnt);
        return cnt;
    }


    private void addServer(Queue<Integer> server, int k, int add) {
        if (server.size() == 0) {
            for (int i = 0; i < k; i++) {
                server.add(add);
            }
        } else {
            int[] arr = new int[server.size()];
            for (int i = 0; i < arr.length; i++) {
                arr[i] = server.poll();
            }

            for (int i = 0; i < k; i++) {
                if (i < arr.length) {
                    server.add(add + arr[i]);
                } else {
                    server.add(add);
                }
            }

        }

    }
}