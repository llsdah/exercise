package org.example.algorithm.pracitce1;

/**
 * 포도먹기
 * 여우는 포도가 떨어지는 순간에 먹는다
 * k번만큼이동해 두그루의 나무를 이동한다. 기준점은 1
 *
 * 최대로 먹을 수 있는 수
 *
 * order = { 2,1,1,2,2,1,1}
 * k = 2;
 */
public class Practice_4 {

    final static int numOfTree = 2;

    private static void solution(int[] order, int k) {


        // 3 2 1 , k번 이동, 0 1 2 = 3번 + 0인덱스
        int[][][] dp = new int[k + 2][numOfTree + 1][order.length];

        for (int i = 1; i < order.length; i++) {
            for (int j = 1; j < k + 2; j++) {
                // 1번 나무에 떨어지면
                if (order[i] == 1) {
                    // 현재 갯수 추가 
                    dp[j][1][i] = Math.max(dp[j][1][i-1], dp[j-1][2][i-1]) + 1;
                    dp[j][2][i] = Math.max(dp[j][2][i-1], dp[j-1][1][i-1]);
                } else {
                    // 2번 나무에서 떨어지는것
                    // 처음에 시작이 1이니까
                    if (i == 1 && j == 1) {
                        continue;
                    }
                    
                    dp[j][1][i] = Math.max(dp[j][1][i-1],dp[j-1][2][i-1]);
                    dp[j][2][i] = Math.max(dp[j][2][i-1],dp[j-1][1][i-1]) + 1;
                }
            }
        }
        
        int result = 0;
        for (int i = 1; i < k+2; i++) {
            result = Math.max(result, Math.max(dp[i][1][order.length-1], dp[i][2][order.length-1]));
        }

        System.out.println("result = " + result);
        
    }

    public static void main(String[] args) {
        int[] order = {2,1,1,2,2,1,1};
        int k = 2;

        solution(order,k);
    }
}
