package org.example.linear.practice.array;

import java.util.Arrays;

/**
 * 2차원 배열의 시계방향 90 도로 회전 결과
 *
 * 입력
 * 1 2 3 4 5
 * 6 7 8 9 10
 * 11 12 13 14 15
 *
 * 출력
 *
 * 11 6 1
 * 12 7 2
 * 13 8 3
 * 14 9 4
 * 15 10 5
 *
 *
 */
public class Practice_7 {
    public static void printArr(int[][] arr) {
        for (int[] itemArr : arr) {
            System.out.println(Arrays.toString(itemArr));
        }
    }
    public static void main(String[] args) {
        int[][] arr = {
                {1,2,3,4,5},
                {6,7,8,9,10},
                {11,12,13,14,15}
        };
        int[][] arr90 = new int[arr[0].length][arr.length];

        for (int i = 0; i < arr.length; i++) {
            for (int j = 0; j < arr[i].length; j++) {
                int r = arr.length -1 -i; // 첫번쨰행이 마지막으로 가기 위한것
                // 행 열
                arr90[j][r] = arr[i][j];
            }
        }

        printArr(arr);
        System.out.println("=================");
        printArr(arr90);
    }
}









