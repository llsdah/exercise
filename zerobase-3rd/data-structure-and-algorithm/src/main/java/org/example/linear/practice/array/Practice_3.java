package org.example.linear.practice.array;

import java.lang.reflect.Array;
import java.util.Arrays;

/**
 * 배열의 데이터순서를 거꾸로 변경하게요
 * 입력 : 1,3,5,7,9
 * 출력 : 9,7,5,3,1
 */
public class Practice_3 {
    public static void main(String[] args) {
        int[] arr = {1,3,5,7,9};

        for (int i = 0; i < arr.length/2; i++) {
            int temp = arr[i];
            arr[i] = arr[arr.length-i-1];
            arr[arr.length-i-1] = temp;
        }

        System.out.println(Arrays.toString(arr));
    }
}
