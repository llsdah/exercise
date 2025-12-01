package org.example.programmers.campus;
import java.util.*;
public class 보물지도 {

    public static void main(String[] args) {
        int n = 4;
        int m = 4;
        int[][] hole = {{2, 3}, {3, 3}};
        System.out.println(solution_my(n,m,hole));
    }

    public static int solution_my(int n, int m, int[][] hole) {
        int answer = 0;
        int[][] grape = new int[n][m];
        for (int[] i : hole) {
            grape[i[0]-1][i[1]-1] = -1; // -1
        }
        List<int[]> l1 = new ArrayList<>();
        l1.add(new int[]{1,0,0});
        l1.add(new int[]{0,1,0});
        l1.add(new int[]{2,0,1});
        l1.add(new int[]{0,2,1});
        List<int[]> l2 = new ArrayList<>();
        l2.add(new int[]{1,0,0});
        l2.add(new int[]{0,1,0});

        Queue<int[]> que = new LinkedList<>();
        que.offer(new int[]{0,0,0});

        while(!que.isEmpty()) {
            int[] now = que.poll();

            // 2번 점프 안한것
            if (now[2] == 0) {
                for (int[] item : l1) {
                    int x = item[0] + now[0];
                    int y = item[1] + now[1];
                    int two = item[2] + now[2];
                    System.out.println("x:"+x+",y:"+y+",now[0]:"+now[0]+",now[1]:"+now[1]+",item:"+two);
                    System.out.println(grape.length+":"+grape[1].length);
                    if( x< 0 || y < 0 || x > n -1 || y > m) continue;

                    if(grape[x][y] != 1 && grape[now[0]][now[1]] +1 > grape[x][y] ) continue;
                    grape[x][y] = grape[now[0]][now[1]] +1 ;
                    que.offer(new int[]{x,y,two});
                }
            } else {
                for (int[] item : l2) {
                    int x = item[0] + now[0];
                    int y = item[1] + now[1];
                    System.out.println("x:"+x+",y:"+y+",now[0]:"+now[0]+",now[1]:"+now[1]);
                    if( x< 0 || y < 0 || x > n || y> m) continue;

                    if(grape[x][y] != 1 && grape[now[0]][now[1]] +1 > grape[x][y] ) continue;
                    grape[x][y] = grape[now[0]][now[1]] +1 ;
                    que.offer(new int[]{x,y,1});
                }
            }
        }

        return answer;
    }

    public static int solution(int n, int m, int[][] hole) {
        int answer = 0;
        int INF = Integer.MAX_VALUE;
        // 사용여부 세로R 가로C
        int[][][] dist = new int[2][m+1][n+1];
        int[][] board = new int[m+1][n+1];
        for (int i = 0; i <= m; i++) {
            for (int j = 0; j < n; j++) {
                dist[0][i][j] = dist[1][i][j] = INF;
            }
        }

        for (int i = 0; i < hole.length; i++) {
            board[hole[i][1]][hole[i][0]] = 1; // 세로 가로
        }

        Queue<int[]> que  = new LinkedList<>();
        que.offer(new int[]{0,1,1});
        dist[0][1][1] = 0;

        int[] dx = {0,1,0,-1};
        int[] dy ={1,0,-1,0};
        
        while(!que.isEmpty()) {
            int[] front = que.poll();
            int flag = front[0];// 두번 뛴 여부
            int r = front[1];
            int c = front[2];

            for (int i = 0; i < 4; i++) {
                int nr = r + dx[i];
                int nc = c + dy[i];
                if (nr < 1 || nr > m || nc < 1 || nc >n) {
                    continue;
                }
                if (board[nr][nc] == 1) continue;
                if (dist[flag][nr][nc] != INF) continue;
                que.offer(new int[]{flag,nr,nc});
                dist[flag][nr][nc] = dist[flag][r][c] + 1;
            }

            if (flag == 0) {
                for (int i = 0; i < 4; i++) {
                    int nr = r + 2 * dx[i];
                    int nc = c + 2 * dy[i];
                    if (nr < 1 || nr > m || nc < 1 || nc > n) {
                        continue;
                    }
                    if (board[nr][nc] == 1) continue;
                    if (dist[1][nr][nc] != INF) continue;
                    que.offer(new int[]{1, nr, nc});
                    dist[1][nr][nc] = dist[1][r][c] + 1;
                }
            }
        }

        answer = Math.min(dist[0][m][n], dist[1][m][n]);
        if (answer == INF) return -1;
        
        return answer;
    }
}
