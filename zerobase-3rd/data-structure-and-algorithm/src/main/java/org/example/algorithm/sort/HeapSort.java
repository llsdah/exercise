package org.example.algorithm.sort;

import java.util.Arrays;

public class HeapSort {


    private static void heapSort(int[] arr) {

        for (int i = arr.length / 2 - 1; i >= 0; i--) {
            heapify(arr, i , arr.length);
        }

        for (int i = arr.length -1; i > 0 ; i--) {
            swap(arr, 0, i);
            heapify(arr, 0, i);
        }

    }

    // heap 자료 구조라 바꾸는 것
    private static void heapify(int[] arr, int parentIdx, int size) {

        int leftIdx = 2 * parentIdx + 1;
        int rightIdx = 2 * parentIdx + 2;
        int maxIdx = parentIdx; // max heap 자료 구조

        if (leftIdx < size && arr[maxIdx] < arr[leftIdx]) { // 범위 체크 max와 비교
            maxIdx = leftIdx;
        }

        if (rightIdx < size && arr[maxIdx] < arr[rightIdx]) {
            maxIdx = rightIdx;
        }

        if (parentIdx != maxIdx) {
            swap(arr, maxIdx, parentIdx);
            heapify(arr, maxIdx, size);

        }

    }

    private static void swap(int[] arr, int i, int j) {
        int temp = arr[i];
        arr[i] = arr[j];
        arr[j] = temp;
    }

    public static void main(String[] args) {

        int[] arr = {3,5,2,7,1,4};
        System.out.println("기존 정렬 : "+ Arrays.toString(arr));
        heapSort(arr);
        System.out.println("힙 정렬 : "+ Arrays.toString(arr));

    }


}
