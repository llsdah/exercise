package org.example.mathematics.practice;

import java.util.*;

/**
 * arr 배열이 주어 져쓸때
 * 해당 데이터로 만들 수 있는 permutation 데이터 출력
 * 단, 현재 데이터 보다 이전의 큰수를 출력
 * 한번의 swap으로 출력 가능한 큰수 출력
 */
public class Practice_2 {


    public static void main(String[] args) {

        int[] arr = {3,2,1};
        System.out.println(solution(arr));
        arr = new int[]{1,9,4,7,6};
        System.out.println(solution(arr));
        arr = new int[]{1,1,2,3};
        System.out.println(solution(arr));
    }

    private static String solution(int[] arr) {

        if (arr == null || arr.length <2) {
            return Arrays.toString(arr);
        }

        int idx = -1;
        for (int i = arr.length-1; i >= 1; i--) {
            if (arr[i] < arr[i-1]) {
                idx = i-1;
                break;
            }
        }

        // 변화가 없다면 그대로 출력
        if (idx == -1) {
            return Arrays.toString(arr);
        }

        // 어느 대상과 교체 해야되나요 ? 가장 오른쪽 입니다.
        for (int i = arr.length -1; i > idx; i--) {
            if (arr[i] < arr[idx] && arr[i] != arr[i-1]) {
                int temp = arr[i];
                arr[i]  = arr[idx];
                arr[idx] = temp;
                break;
            }
        }

        return Arrays.toString(arr);
    }
}


