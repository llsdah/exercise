package org.example.algorithm.binarysearch;

import java.util.Arrays;

/**
 * 자바 기본 이분탐색
 */
public class BinarySearch_2 {

    public static void main(String[] args) {

        int[] arr = {1, 3, 5, 10, 20,30, 40, 50, 60};
        System.out.println();
        System.out.println("데이터가 있는 경우 ");
        System.out.println("java binary search : "+Arrays.binarySearch(arr, 1));
        System.out.println("java binary search : "+Arrays.binarySearch(arr, 30));
        System.out.println("java binary search : "+Arrays.binarySearch(arr, 60));

        System.out.println();
        System.out.println("데이터가 없는 경우 ");
        System.out.println("java binary search : "+Arrays.binarySearch(arr, 100));
        System.out.println("java binary search : "+Arrays.binarySearch(arr, 11));
        System.out.println("java binary search : "+Arrays.binarySearch(arr, 21));
        // 없는 값은 기존에 값이 있었다면 들어갈 index 위치의 음수 형태의 값에 "-1" 을 더해서 반환한다.
        
    }
}
