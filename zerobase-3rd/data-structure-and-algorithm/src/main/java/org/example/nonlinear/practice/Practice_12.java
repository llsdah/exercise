package org.example.nonlinear.practice;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Queue;

/**
 * N x N 방의 전등 스위치 정보가 2차원 배열
 * 각방은 (1,1) - (N,N) 번호가 있음
 *
 *
 * switches[i] = {1,1,1,2} => (1,1) ~ (1,2) 방까지 불을 킬 수 있다.
 *
 * 방의 불을 킬 수 있는 최대 갯수
 *
 * 상하좌우 인접방만 이동 불켜져 있는 방으로 이동 가능
 * (1,1) 불 켜져 있다.
 *
 */
public class Practice_12 {

    public static void main(String[] args) {
        int[][] switches = {
                {1,1,1,2},
                {2,1,2,2},
                {1,1,1,3},
                {2,3,3,1},
                {1,3,1,2},
                {1,3,2,1}
        };

        System.out.println("solution() = " + solution(3,switches));

    }

    final static int[][] dirs = {{1,0},{-1,0},{0,1},{0,-1}};

    public static class Room {
        int x;
        int y;
        public Room(int x, int y) {
            this.x = x;
            this.y = y;
        }
    }

    private static int solution(int n, int[][] switches) {
        if (switches == null || switches.length == 0 || switches[0].length == 0 ) {
            return -1;
        }

        ArrayList<Room>[][] graph = new ArrayList[n][n];
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                graph[i][j] = new ArrayList<>();
            }
        }


        for (int i = 0; i < switches.length; i++) {

            // () -> ()
            graph[switches[i][0] - 1][switches[i][1] - 1].add(new Room(switches[i][2]-1, switches[i][3] - 1));
        }

        boolean[][] switched = new boolean[n][n];
        switched[0][0] = true;

        int cnt = bfs(n, graph, switched);

        return cnt + 1;
    }

    public static int bfs(int n, ArrayList<Room>[][] graph, boolean[][] switched) {
        Queue<Room> que = new LinkedList<>();
        que.offer(new Room(0,0));

        boolean[][] visited = new boolean[n][n];
        visited[0][0] = true;

        int cnt = 0;
        while (!que.isEmpty()) {
            Room cur = que.poll();

            for (Room r : graph[cur.x][cur.y]) {
                if (!switched[r.x][r.y]) {
                    switched[r.x][r.y] = true;
                    cnt++;
                }
            }


            for (int[] dir : dirs) {
                int xNext = cur.x + dir[0];
                int yNext = cur.y + dir[1];

                if (xNext >= 0 && xNext < n && yNext >= 0 && yNext < n) {
                    if (switched[xNext][yNext] && !visited[xNext][yNext]) {
                        que.offer(new Room(xNext,yNext));
                        visited[xNext][yNext] = true;
                    }
                }

            }

        }

        return cnt == 0 ? cnt : cnt + bfs(n, graph, switched);
    }


}
