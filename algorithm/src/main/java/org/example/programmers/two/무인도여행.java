package org.example.programmers.two;

    import java.util.*;
public class 무인도여행 {


    class Solution {
        public int[] solution(String[] maps) {
            int[] answer = {};
            int r = maps.length;
            int c = maps[0].length();

            int[][] map = new int[r][c];
            boolean[][] visit = new boolean[r][c];

            for (int i = 0; i< r ; i ++) {
                char[] arr = maps[i].toCharArray();

                for (int k = 0; k < c;k++) {
                    if (arr[k] == 'X') {
                        map[i][k] = -1;
                        visit[i][k] = true;
                        continue;
                    }
                    map[i][k] = arr[k]-'0';
                    //System.out.println(map[i][k]);
                }
            }

            List<int[]> list = new LinkedList<>();
            list.add(new int[]{0,1});
            list.add(new int[]{0,-1});
            list.add(new int[]{-1,0});
            list.add(new int[]{1,0});

            List<Integer> an = new ArrayList<>();
            for (int i=0; i < r; i++) {
                for (int k=0; k<c;k++) {
                    if(visit[i][k]) continue;
                    Queue<int[]> que = new LinkedList<>();

                    que.offer(new int[]{i,k});
                    visit[i][k] = true;
                    int sum = 0 ;
                    while(!que.isEmpty()) {
                        int[] cur = que.poll();
                        int cr = cur[0];
                        int cc = cur[1];
                        sum += map[cr][cc];

                        for (int[] item : list) {
                            int nr = item[0] + cr;
                            int nc = item[1] + cc;
                            if (nr<0 || nc<0
                                    || nr >= map.length || nc >= map[0].length) continue;
                            if (visit[nr][nc] || map[nr][nc]==-1) continue;

                            visit[nr][nc] = true;

                            que.offer(new int[]{nr,nc});
                        }
                    }
                    an.add(sum);
                }
            }

            if (an.size() == 0) return new int[]{-1};

            answer = new int[an.size()];
            for (int i = 0 ; i <an.size();i++) {
                answer[i] = an.get(i);
            }
            Arrays.sort(answer);
            return answer;
        }


    }
}
