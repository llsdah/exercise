package org.example.programmers.two;

    import java.util.*;
public class 리코쳇로봇 {
    class Solution {
        public int solution(String[] board) {
            int answer = 0;

            int r = board.length;
            int c = board[0].toCharArray().length;

            // 갯수 까지
            char[][][] b = new char[r][c][1];
            boolean[][] visit = new boolean[r][c];

            int[] start = new int[2];
            int[] end = new int[2];

            Queue<int[]> que = new LinkedList<>();

            for (int i = 0; i< r;i++) {
                char[] arr = board[i].toCharArray();
                for (int k = 0; k<c;k++) {
                    b[i][k][0] = arr[k];
                    if (arr[k] == 'R') {
                        start[0] = i;
                        start[1] =k;
                    } else if (arr[k] == 'G') {
                        end[0] = i;
                        end[1] = k;
                    }
                }
            }

            que.offer(new int[]{start[0],start[1],0});
            visit[start[0]][start[1]] = true;
            List<int[]> dir = new LinkedList<>();
            dir.add(new int[]{0,1});
            dir.add(new int[]{0,-1});
            dir.add(new int[]{1,0});
            dir.add(new int[]{-1,0});

            while(!que.isEmpty()) {
                int[] cur = que.poll();
                int cnt = cur[2];
                if ( cur[0] == end[0] && cur[1] == end[1] ) return cnt;

                for (int[] item : dir) {
                    int[] next = new int[2];
                    next[0] = cur[0];
                    next[1] = cur[1];

                    int rc = cur[0];
                    int cc = cur[1];
                    while (true) {
                        rc +=item[0];
                        cc +=item[1];

                        if (rc < 0 || cc<0 || rc >= r || cc >=c || b[rc][cc][0] == 'D') break;
                        next[0] = rc;
                        next[1] = cc;
                    }
                    if(visit[next[0]][next[1]]) continue;
                    visit[next[0]][next[1]] = true;
                    que.offer(new int[]{next[0],next[1],cnt+1});
                }

                //System.out.println("arr : "+Arrays.toString(cur));

            }


            return -1;
        }
    }
}
