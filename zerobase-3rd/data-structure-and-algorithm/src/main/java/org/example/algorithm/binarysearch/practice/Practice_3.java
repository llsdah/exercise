package org.example.algorithm.binarysearch.practice;

/**
 * 이차원 행렬에서 이진탐색으로 데이터 찾기
 * row X col
 * 각 행의 데이터는 오름차순으로 정렬
 *
 * {{1,3,7,8,},{10,11,15,20},{21,30,35,60}}
 *
 * target 3 , output true
 *
 * target 13, output false
 */
public class Practice_3 {

    public static void main(String[] args) {
        int[][] matrix = {{1,3,7,8},{10,11,15,20},{21,30,35,60}};

        System.out.println(solution(matrix,3));
        System.out.println(solution(matrix,5));
        System.out.println(solution(matrix,60));
    }

    private static boolean solution(int[][] matrix, int target) {

        if (matrix == null || matrix.length == 0) {
            return false;
        }

        // 한줄 정렬
        int left = 0;
        int rows = matrix.length; // 행
        int cols = matrix[0].length; //열
        int right = rows * cols - 1;

        while (left <= right) {
            int mid = (left + right) / 2;

            if (matrix[mid / cols][mid % cols] == target){ // 일렬 인덱싱 중요합니다.
                return true;
            } else if (matrix[mid / cols][mid % cols] < target) {
                left = mid + 1;
            } else {
                right = mid - 1;
            }
        }





        return false;
    }
}
