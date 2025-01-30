package org.example.algorithm.sort;

import java.util.Arrays;

public class ShellSort {


    private static void shellSort(int[] arr) {
        int gap = arr.length / 2;

        for (int g = gap; g > 0 ; g /= 2) {
            // 정렬
            for (int i = g; i < arr.length ; i++) {
                int temp = arr[i];

                int j = 0;
                for (j = i - g; j >=0; j -= g) {
                    if (arr[j] > temp) {
                        arr[j+g] = arr[j];
                    } else {
                        break;
                    }
                }
                arr[j + g] = temp;
            }
        }
    }

    public static void main(String[] args) {

        int[] arr = {10, 32,10, 11,13,12, 52, 27, 48, 17, 99, 56};
        int[] temp = new int[arr.length];
        System.out.println("기존 정렬 : " + Arrays.toString(arr));
        shellSort(arr);
        System.out.println("쉘 정렬 : " + Arrays.toString(arr));

    }

}
