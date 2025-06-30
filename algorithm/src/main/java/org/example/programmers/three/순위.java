package org.example.programmers.three;
    import java.util.*;

public class 순위 {

    class Solution {
        public int solution(int n, int[][] results) {
            int answer = 0;

            int[][] gp = new int[n+1][n+1];

            for (int i = 0; i < results.length;i++){
                int[] num = results[i];
                gp[num[0]][num[1]] = 1;
                gp[num[1]][num[0]] = -1;
            }

            for (int i = 1; i <=n ; i++) {
                for (int j = 1; j <=n ; j++) {
                    for (int k = 1; k <=n ; k++) {
                        if( gp[j][i] == 1 && gp[i][k] == 1 ){
                            gp[j][k] = 1;
                            gp[k][j] = -1;
                        }
                    }
                }
            }

            // answer

            for (int i = 1; i <=n ; i++) {
                int cnt = 0;
                for (int j = 1; j <=n ; j++) {
                    if ( i == j) continue;
                    if (gp[i][j] != 0) cnt ++;

                }
                if ( cnt == n-1) answer++;
            }




            return answer;
        }
    }
}
