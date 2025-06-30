package org.example.programmers.three;

import java.util.*;
public class 보물지도 {
    class Solution {
        public int solution(int n, int m, int[][] hole) {
            int answer = 0;

            int[][][] map = new int[n+1][m+1][2];
            boolean[][][] check = new boolean[n+1][m+1][2];

            for (int[] i : hole) {
                map[i[0]][i[1]][0] = -1;
                map[i[0]][i[1]][1] = -1;
            }

            List<int[]> dis = new LinkedList<>();
            dis.add(new int[]{0,1});
            dis.add(new int[]{0,-1});
            dis.add(new int[]{1,0});
            dis.add(new int[]{-1,0});

            Queue<int[]> que = new LinkedList<>();
            que.add(new int[]{1,1,0});
            check[1][1][0] = true;
            //check[1][1][1] = true;

            while(!que.isEmpty()) {
                int[] cur = que.poll();

                for (int[] i : dis) {
                    int n1 = cur[0] + i[0];
                    int m1 = cur[1] + i[1];

                    if(n1 <= 0||n1>n||m1<=0||m1>m) continue;
                    if(map[n1][m1][cur[2]] == -1) continue;
                    if(check[n1][m1][cur[2]]) continue;

                    map[n1][m1][cur[2]] =  map[cur[0]][cur[1]][cur[2]] + 1;
                    que.offer(new int[]{n1,m1,cur[2]});
                    check[n1][m1][cur[2]] = true;
                }

                if(cur[2] == 0) {

                    for (int[] i : dis) {
                        int n1 = cur[0] + (2*i[0]);
                        int m1 = cur[1] + (2*i[1]);

                        if(n1 <= 0||n1>n||m1<=0||m1>m) continue;
                        if(map[n1][m1][cur[2]] == -1) continue;
                        if(check[n1][m1][1] ) continue;

                        map[n1][m1][1] =  map[cur[0]][cur[1]][cur[2]] + 1;
                        que.offer(new int[]{n1,m1,1});
                        check[n1][m1][1] = true;
                    }
                }


            }
            System.out.println(" map : "+map[n][m][0]+", "+map[n][m][1]);
            answer = Math.min(map[n][m][0], map[n][m][1]);
            return answer == 0 ? -1 : answer;
        }
    }
}
