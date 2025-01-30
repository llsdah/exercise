package org.example.algorithm.sort;

import java.util.Arrays;

public class SelectionSort {

    private static void selectionSort(int[] arr) {
        // min
        for (int i = 0; i < arr.length-1; i++) {
            int min = i;
            for (int j = i; j < arr.length; j++) {
                if (arr[j] < arr[min]) {
                    min = j;
                }
            }
            int temp = arr[i];
            arr[i] = arr[min];
            arr[min] = temp;
        }

        // max
        for (int i = arr.length-1; i > 0; i--) {

            int max = i;

            for (int j = i-1; j >= 0; j--) {
                if (arr[j] > arr[max]) {
                    max = j;
                }
            }
            int temp = arr[i];
            arr[i] = arr[max];
            arr[max] = temp;

        }

    }

    public static void main(String[] args) {

        int[] arr = {3,5,2,7,1,4};

        System.out.println("기존 정렬 : "+ Arrays.toString(arr));
        selectionSort(arr);
        System.out.println("선택 정렬 : "+ Arrays.toString(arr));

    }
}
