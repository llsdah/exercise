package org.example.programmers.two;

    import java.util.*;
public class 완벽범죄 {

    class Solution {
        boolean[][][] visit;
        public int solution(int[][] info, int n, int m) {
            int answer = 0;
            // A - n, B - m
            // A 도둑의 흔적 누개 수 ,
            // 두도둑이 경찰에 안붙잡히면, A= 0
            // 붙잡히면, -1
            // 접근 방식 B가 큰것부터 훔친다 .
            // 각 물건은 훔치거나 움치지 않는것만 존재 한다.
            // dp 주의 이미 방문한건 하지 않는다
            visit = new boolean[121][121][info.length+1];
            dp (info, n, m, 0, 0, 0);

            answer = (min == Integer.MAX_VALUE ? -1 : min);
            //System.out.println("min : "+min);
            return answer;
        }
        int min = Integer.MAX_VALUE;
        public void dp(int[][] info, int n, int m, int an, int bm, int idx) {
            if (an >= n || bm >= m) return;
            if (visit[an][bm][idx]) return;
            visit[an][bm][idx] = true;

            if (idx == info.length ) {
                if(an<n && bm < m) {
                    min = Math.min(an,min);
                }
                return;
            }
            dp(info, n, m, an+info[idx][0] , bm, idx+1);
            dp(info, n, m, an, bm +info[idx][1], idx+1);
        }
    }
}
