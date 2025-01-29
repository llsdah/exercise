package org.example.algorithm.sort;

import java.util.Arrays;

public class QuickSort {

    private static void quickSort(int[] arr, int left, int right) {
        if (left >= right) {
            return;
        }
        int pivot = partition(arr, left, right);

        quickSort(arr, left, pivot - 1);
        quickSort(arr, pivot + 1, right);

    }

    // 가장 좌측이 pivot
    private static int partition(int[] arr, int left, int right) {
        int pivot = arr[left];
        int i = left;
        int j = right;

        while (i < j) { // 만날때 까지 돌린다.
            while (arr[j] > pivot && i < j) { // pivot 보다 작은 아이
                j--;
            }
            while (arr[i] <= pivot && i < j) { // pivot
                i++;
            }
            swap(arr,i,j);
        }
        // pivot 과 교환 된
        swap(arr, left, i);

        return i;
    }

    private static void swap(int[] arr, int i, int j) {
        int temp = arr[i];
        arr[i] = arr[j];
        arr[j] = temp;
    }

    public static void main(String[] args) {

        int[] arr = {3,5,2,7,1,4};
        System.out.println("기존 정렬 : "+ Arrays.toString(arr));
        quickSort(arr, 0, arr.length-1);

        System.out.println("힙 정렬 : "+ Arrays.toString(arr));

    }
}
