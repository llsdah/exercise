package org.example.programmers.campus;
import com.sun.source.tree.WhileLoopTree;

import java.util.*;

public class 순위 {
    public int solution_my(int n, int[][] results) {
        int answer = 0;

        int[][] win = new int[n][n];
        // 1 : c는 r한테 이겼다
        // -1 : c는 r한테 졌다.
        for (int[] items : results) {
            win[items[0]-1][items[1]-1] = 1;
            win[items[1]-1][items[0]-1] = -1;
        }

        for (int c = 0 ; c < n;c++) {
            for (int r = 0; r < n;r++) {
                if(win[c][r]==0) continue;
                // c가 r을 이겼다.
                if(win[c][r]==1) {
                    for (int k = 0; k <n ; k++) {
                        // r이 k를 이겼다.
                        if(win[r][k] == 1) {
                            win[c][k] = 1;
                            win[k][c] = -1;
                        }
                    }
                    // c 가 r 한테 졌다.
                } else if (win[c][r]==-1) {
                    for (int k = 0; k <n ; k++) {
                        // r이 k를 졌다면.
                        if(win[r][k] == -1) {
                            win[c][k] = -1;
                            win[k][c] = 1;

                        }
                    }
                }
            }
        }

        for (int c = 0; c< n;c++) {
            int cnt = 0;
            for (int r = 0; r < n; r++) {
                if (win[c][r]==0) cnt++;
            }
            if (cnt==1) answer++;
        }

        return answer;
    }

    public int solution(int n, int[][] results) {
        List<Node> list = new ArrayList<>();
        for (int i = 0; i < n; i++) {
            list.add(new Node(i+1));
        }
        for (int[] result : results) {
            Node win = list.get(result[0] - 1);
            Node lose = list.get(result[1] - 1);
            win.links.add(lose);
        }
        for (Node win : list) {
            for(Node item : list) { item.visit = false;}

            Queue<Node> que = new LinkedList<>();

            win.visit = true;
            que.offer(win);
            while(!que.isEmpty()) {
                Node now = que.poll();
                for (Node lose : now.links) {
                    if (!lose.visit) continue;
                    lose.visit = true;
                    que.offer(lose);
                    win.win += 1;
                    lose.lose +=1;
                }
            }
        }

        int answer = 0;
        for (Node node : list) {
            if (node.win + node.lose == n -1) answer++;
        }
        
        return answer;
    }

    class Node {
        int n;
        int win = 0; int lose = 0;
        boolean visit = false;
        List<Node> links = new LinkedList<>();
        Node (int n ) {this.n = n;}
    }

}
