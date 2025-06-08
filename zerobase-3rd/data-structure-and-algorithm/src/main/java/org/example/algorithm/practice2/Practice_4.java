package org.example.algorithm.practice2;

/**
 * m x n 행렬
 * 1로 이루어진 정사각형중 가장큰 넓이 출력
 *
 * input {{0,1},{1,0}}
 * output 1
 *
 *
 * input {{1,0,1,1,1,},{1,0,1,1,1},{1,1,1,1,1},{1,0,1,1,0}}
 * output 9
 *
 */
public class Practice_4 {


    // DP 방식으로 푸는 것이 편하다. 
    private static void solution(int[][] matrix) {
    
        if (matrix == null || matrix.length == 0 ){
            return;
        }
        
        int maxVal = 0;
        // 인접구간 + 열
        int[][] dp = new int[2][matrix[0].length];


        for (int i = 0; i < matrix.length; i++) {
            for (int j = 0; j < matrix[0].length; j++) {
                dp[i % 2][j] = matrix[i][j];

                if (i != 0 && j != 0 && dp[i%2][j] != 0) {
                    int up = dp[i%2][j-1];
                    int left = dp[(i-1)%2][j];
                    int ul = dp[(i-1)%2][j-1];

                    int minVal = Math.min(Math.min(up,left),ul);
                    dp[i%2][j] = minVal + 1;
                }
                maxVal = Math.max(maxVal,dp[i%2][j]);
            }
        }

        System.out.println("maxVal = " + (maxVal*maxVal));

    }


    public static void main(String[] args) {
        int[][] matrix = {{0,1},{1,0}};
        solution(matrix);
        matrix = new int[][]{
                {1,0,1,1,1},
                {1,0,1,1,1},
                {1,1,1,1,1},
                {1,0,1,1,0}
        };
        solution(matrix);
    }
}
