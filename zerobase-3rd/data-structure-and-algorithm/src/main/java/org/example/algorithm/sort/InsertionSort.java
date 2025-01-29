package org.example.algorithm.sort;

import java.util.Arrays;

public class InsertionSort {
    /**
     *
     */

    private static void insertionSort(int[] arr) {
        // 앞쪽 하고 비교하는 부분이다.
        for (int i = 1; i < arr.length; i++ ) {
            for (int j = i; j > 0 ; j--) {
                if (arr[j] < arr[j -1]) {
                    int temp = arr[j];
                    arr[j] = arr[j - 1];
                    arr[j-1] = temp;
                } else {
                    break;
                }
            }
        }

    }

    public static void main(String[] args) {

        int[] arr = {3,5,2,7,1,4};

        System.out.println("기존 정렬 : "+ Arrays.toString(arr));
        insertionSort(arr);
        System.out.println("삽입 정렬 : "+ Arrays.toString(arr));

    }

}
