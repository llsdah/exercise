package org.example.programmers.two;

    import java.util.*;
public class 체육대회 {
    class Solution {
        public int solution(int[][] ability) {
            int answer = 0;
            boolean[] check = new boolean[ability.length]; // 사람 사용 유무
            int sum =0;

            dfs (ability, 0 , check, sum);
            //System.out.println(cnt);
            return cnt;
        }
        int cnt = 0;
        public void dfs(int[][] a,int c, boolean[] p, int sum) {
            if (c == a[0].length) {
                cnt = Math.max(sum, cnt);
                return;
            }

            for (int i = 0; i < a.length; i ++ ) {
                if(p[i]) continue;
                p[i] = true;
                dfs (a, c+1 , p, sum +a[i][c]);
                p[i] = false;
            }

        }
    }
}
