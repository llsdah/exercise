package org.example.algorithm.sort;

import java.util.Arrays;

public class MergeSort {


    private static void mergeSort(int[] arr, int[] temp, int left, int right) {
        if (left < right) {
            int mid = (left + right) / 2;
            mergeSort(arr, temp, left, mid);
            mergeSort(arr, temp, mid + 1, right );
            merge(arr, temp, left, right, mid);
        }
    }

    private static void merge(int[] arr, int[] temp, int left, int right, int mid) {
        int p = left; // 데이터
        int q = mid + 1; // 데이터
        int idx = p;

        while (p <= mid || q <= right) { // 둘중 하나라도 안쪽에 있는 것 유효한 인덱스에 있다.
            if (p <= mid && q <= right) { // 다 안쪽에 있는 유효
                if (arr[p] <= arr[q]) {
                    temp[idx++] = arr[p++];
                } else {
                    temp[idx++] = arr[q++];
                }
            } else if (p <= mid && q > right){ // q가 범위 벗어남, p는 유효 인덱스
                temp[idx++] = arr[p++]; // 좌측만 넣음
            } else {
                temp[idx++] = arr[q++]; // 우측만 넣음
            }
        }

        for (int i = left; i <= right; i++) {
            arr[i] = temp[i];
        }

    }

    public static void main(String[] args) {

        int[] arr = {3,5,2,7,1,4,6};
        int[] temp = new int[arr.length];
        System.out.println("기존 정렬 : "+ Arrays.toString(arr));
        mergeSort(arr,temp,0, arr.length-1);
        System.out.println("합병 정렬 : "+ Arrays.toString(arr));


    }

}
