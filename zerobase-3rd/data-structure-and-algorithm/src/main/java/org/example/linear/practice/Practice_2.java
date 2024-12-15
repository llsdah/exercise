package org.example.linear.practice;

/**
 * n x n 행렬 데이터
 * 0이있는 경우  원소위치의 행열 데이터 전체를 0으로 변경하는 코드
 *
 */
public class Practice_2 {

    public static void main(String[] args) {
        int[][] matrix = {
                {1,1,1},
                {1,0,1},
                {1,1,1}
        };
        solution(matrix);
        System.out.println();
        matrix = new int[][]{
                {1,1,0},
                {1,1,1},
                {0,1,1},
        };
        solution(matrix);
    }

    private static void solution(int[][] matrix) {
        boolean frZero = false;
        boolean fcZero = false;

        // 바꿀 외각만 변경
        for (int i = 0; i < matrix.length; i++) {
            for (int j = 0; j < matrix[i].length; j++) {
                if (matrix[i][j] == 0) {
                    if (i == 0) {
                        frZero = true;
                    }
                    if (j == 0) {
                        fcZero = true;
                    }
                    matrix[i][0] = 0;
                    matrix[0][j] = 0;
                }
            }
        }

        // 외곽은 확인 했으니 내부 데이터 변경
        for (int i = 1; i < matrix.length; i++) {
            for (int j = 1; j < matrix[i].length; j++) {
                if(matrix[i][0] == 0 || matrix[0][j] == 0){
                    matrix[i][j] = 0;
                }
            }
        }

        if (fcZero) {
            for (int i = 0; i < matrix[0].length; i++) {
                matrix[0][i] = 0;
            }
        }
        if (frZero) {
            for (int i = 0; i < matrix.length; i++) {
                matrix[i][0] = 0;
            }
        }

        for (int i = 0; i < matrix.length; i++) {
            for (int j = 0; j < matrix[i].length; j++) {
                System.out.print(matrix[i][j] +" ");
            }
            System.out.println();
        }

    }
}
