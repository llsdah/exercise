package org.example.programmers.three;

    import java.util.*;
public class 정수삼각형 {

    class Solution {
        public int solution(int[][] triangle) {
            int answer = 0;
            int r = triangle.length;
            for (int i = r -2 ; i>=0;i--) {
                for (int k = 0; k < triangle[i].length;k++) {
                    triangle[i][k] = Math.max(triangle[i][k] + triangle[i+1][k]
                            ,triangle[i][k] + triangle[i+1][k+1]);
                }
            }
            return triangle[0][0];
        }
    }

    class Solution {
        int max = 0;
        public int solution(int[][] triangle) {
            int answer = 0;
            int r = triangle.length;

            dfs(triangle, 0, 0, triangle[0][0]);

            return max;
        }

        public void dfs(int[][] t, int r, int c, int sum) {

            if (r == t.length - 1) {
                max = Math.max(sum, max);
                return;
            }

            dfs(t,r+1,c,sum+t[r+1][c]);
            dfs(t,r+1,c+1,sum+t[r+1][c+1]);
        }
    }

}
