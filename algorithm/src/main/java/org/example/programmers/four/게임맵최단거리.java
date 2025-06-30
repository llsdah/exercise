package org.example.programmers.four;

    import java.util.*;
public class 게임맵최단거리 {
    class Solution {
        public int solution(int[][] maps) {
            int answer = 0;
            int r = maps.length;
            int c = maps[0].length;

            boolean[][] visit = new boolean[r][c];
            int[][] count = new int[r][c];
            count[0][0] = 1;
            count[r-1][c-1] = -1;

            Queue<int[]> que = new LinkedList<>();
            int[] start = {0,0};

            List<int[]> move = new LinkedList<>();
            move.add(new int[]{0,1});
            move.add(new int[]{0,-1});
            move.add(new int[]{1,0});
            move.add(new int[]{-1,0});

            que.offer(start);
            visit[0][0] = true;

            while(!que.isEmpty()) {
                int[] s = que.poll();
                for (int[] m : move) {

                    if( s[0]+m[0] < 0 || s[0]+m[0] >= r
                            || s[1]+m[1] < 0 || s[1]+m[1] >= c ) continue;
                    if (visit[s[0]+m[0]][s[1]+m[1]]) continue;
                    if (maps[s[0]+m[0]][s[1]+m[1]] == 0) continue;

                    count[s[0]+m[0]][s[1]+m[1]] = count[s[0]][s[1]] +1;

                    int[] ad = new int[2];
                    ad[0] = s[0]+m[0];
                    ad[1] = s[1]+m[1];
                    que.offer(ad);
                    visit[ad[0]][ad[1]] = true;
                };

            }
            //System.out.println(count[r-1][c-1]);

            return count[r-1][c-1];
        }

    }
}
