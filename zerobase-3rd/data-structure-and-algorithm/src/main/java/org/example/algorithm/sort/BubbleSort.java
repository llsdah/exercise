package org.example.algorithm.sort;

import java.util.Arrays;

public class BubbleSort {


    private static void bubbleSort(int[] arr) {
        for (int i = 1; i < arr.length; i++) {
            for (int j = 0; j < arr.length - i; j++) {
                if (arr[j] > arr[j+1]) {
                    int temp = arr[j];
                    arr[j] = arr[j+1];
                    arr[j+1] = temp;
                }
            }
        }

        // 끝을 나하씩 줄이는 방식
        for (int i = arr.length - 1; i > 0; i--) {
            for (int j = 0; j < i; j++) {
                if (arr[j] > arr[j+1]) {
                    int temp = arr[j];
                    arr[j] = arr[j + 1];
                    arr[j + 1] = temp;
                }
            }
        }
        
    }

    public static void main(String[] args) {

        int[] arr = {3,5,2,7,1,4};
        System.out.println("기존 정렬 : "+ Arrays.toString(arr));
        bubbleSort(arr);
        System.out.println("버블 정렬 : "+ Arrays.toString(arr));

    }

}
