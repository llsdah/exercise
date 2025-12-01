package org.example.programmers.campus;

import java.util.*;
public class 게임맵최단거리 {
    public int solution_my(int[][] maps) {
        int answer = 0;

        int c = maps.length;
        int r = maps[0].length;

        boolean[][] visit = new boolean[c][r];
        int[][] dirs = {{1,0},{-1,0},{0,1},{0,-1}};
        Queue<int[]> que = new LinkedList<>();

        que.offer(new int[]{0,0});

        while(!que.isEmpty()) {
            int[] pos = que.poll();
            if ( visit[pos[0]][pos[1]]) continue;
            visit[pos[0]][pos[1]] = true;

            for (int[] dir : dirs) {
                int x = pos[0] + dir[0];
                int y = pos[1] + dir[1];

                if (x >= c || x < 0 || y >= r || y < 0) continue;
                if (maps[x][y]  == 0 ) continue;
                if (maps[x][y] == 1) {
                    maps[x][y] += maps[pos[0]][pos[1]];
                } else {

                    maps[x][y] = Math.min(maps[x][y],maps[pos[0]][pos[1]]+1);
                }


                que.offer(new int[]{x,y});
            }
        }


        return maps[c-1][r-1] != 1 ? maps[c-1][r-1] : -1;
    }

    public int solution_diff(int[][] maps) {
        int answer = 0;

        int n = maps[0].length; // x
        int m = maps.length; // y
        boolean[][] visit = new boolean[m][n];
        int[][] dir ={{0,1},{0,-1},{1,0},{-1,0}};
        Queue<int[]> que = new LinkedList<>();
        que.offer(new int[]{0,0});

        while(!que.isEmpty()) {
            int[] now = que.poll();
            // 방문했거나 0이면 못감
            int x = now[1];
            int y = now[0];
            if (maps[y][x] == 0 || visit[y][x]) continue;
            visit[y][x] = true;
            for(int[] d : dir) {
                int xn = x + d[1];
                int yn = y + d[0];
                if( xn <0 || yn < 0 || xn >= n || yn >= m) continue;
                if (maps[yn][xn] == 0 || visit[yn][xn]) continue;
                que.offer(new int[]{yn,xn});
                maps[yn][xn] = maps[y][x] +1;

            }

        }

        if(maps[m-1][n-1]==1) return -1;

        return maps[m-1][n-1];
    }

    public int solution_1(int[][] maps) {
        int answer = 0 ;
        int X = maps.length;
        int Y = maps[0].length;
        Location target = new Location(X-1,Y-1);
        boolean[][] visit = new boolean[X][Y];

        Queue<Position> que = new LinkedList<>();
        while(!que.isEmpty()) {
            Position now = que.poll();

            if(now.location.x < 0 || now.location.y <0 ||
                    now.location.x >= X || now.location.y >=Y) continue;
            if(maps[now.location.x][now.location.y]==0) continue;
            if(visit[now.location.x][now.location.y]) continue;

            visit[now.location.x][now.location.y] = true;

            if (now.location.equals(target)) {return now.step;}

            que.offer(new Position(now.location.left(), now.step+1));
            que.offer(new Position(now.location.right(), now.step+1));
            que.offer(new Position(now.location.up(), now.step+1));
            que.offer(new Position(now.location.down(), now.step+1));

        }

        return -1;
    }
    class Position {
        int step;
        Location location;
        Position(Location l, int s) {location = l; step = s;}
    }
    class Location {
        int x,y;
        Location(int x, int y) {this.x =x; this.y = y;}
        @Override
        public boolean equals(Object l) {
            return this.x == ((Location)l).x
                    && this.y == ((Location)l).y;
        }
        Location left() {return new Location(x-1,y);}
        Location right() {return new Location(x+1,y);}
        Location up() {return new Location(x,y-1);}
        Location down() {return new Location(x,y+1);}
    }

    public int solution_2(int[][] maps) {
        int w = maps.length;
        int h = maps[0].length;

        Queue<Node> que = new LinkedList<>();
        int[][] count = new int[w][h];
        boolean[][] visit = new boolean[w][h];
        que.offer(new Node(0,0));
        count[0][0] = 1;
        visit[0][0] = true;

        while(!que.isEmpty()) {
            Node cur = que.poll();

            int cnt = count[cur.x][cur.y];

            // 이동
            int[][] move = {
                    {1,0},
                    {-1,0},
                    {0,1},
                    {0,-1}
            };
            for (int[] m : move) {
                Node next = new Node(m[0]+cur.x,m[1]+cur.y);
                if (!next.isValid(next.x, next.y)) continue;
                if (visit[next.x][next.y]) continue;
                if (maps[next.x][next.y] == 0) continue;

                count[next.x][next.y] = cnt + 1;
                visit[next.x][next.y] = true;
                que.offer(next);
            }
        }
        int answer = count[w-1][h-1];
        if (answer == 0) return -1;

        return answer;
    }
    class Node {
        int x,y;
        Node(int x, int y) {
            this.x = x;
            this.y = y;
        }
        boolean isValid(int w, int h) {
            if (x < 0 || x >= w
                    || y < 0 || y >= h) return false;
            return true;
        }
    }

}
