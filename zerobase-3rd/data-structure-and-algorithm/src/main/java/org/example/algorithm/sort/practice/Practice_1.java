package org.example.algorithm.sort.practice;

import java.util.Arrays;

/**
 * nums 배열에 3자기 색으로 구분되는 데이터가 있다.
 * 0 흰, 1 파, 2 빨
 * 흰색부터 빨간색 순으로 인접하게 정렬시킨 후 출력하는 프로그램 작석
 *
 * 입력 : 2,0,2,1,1,0
 * 출력 : 0,0,1,1,2,2
 *
 */

public class Practice_1 {

    public static void main(String[] args) {
        int[] arr = {2,0,2,1,1,0};
        System.out.println(Arrays.toString(arr));
        solution(arr);
        System.out.println(Arrays.toString(arr));

        arr = new int[]{2,0,1};
        System.out.println(Arrays.toString(arr));
        solution(arr);
        System.out.println(Arrays.toString(arr));
    }

    private static void solution(int[] arr) {

        if (arr == null || arr.length == 0) {
            return;
        }
        int[] cntArr = new int[3];

        for (int i = 0; i < arr.length; i++) {
            cntArr[arr[i]] ++;
        }


        int idx = 0;
        for (int i = 0; i < cntArr.length; i++) {
            while (cntArr[i]>0) {
                arr[idx++] = i;
                cntArr[i] -= 1;
            }

        }

    }


}
