package org.example.algorithm.binarysearch.practice;

/**
 * 이진탐색 추가 구현
 * target 값이 없느 경우 기존 경우의 수 위치에 "-1" 곱하고 "-1" 배기
 *
 * arr = 1,2,5,10,20,30,40,50,60
 * 입력 30 출력 5
 * 입력 3 출력 -3
 *
 */
public class Practice_1 {

    public static void main(String[] args) {

        int[] arr = {1, 2, 5, 10, 20,30, 40, 50, 60};
        System.out.println();
        System.out.println("index : "+solutino(arr,30));
        System.out.println("index : "+solutino(arr, 3));

    }

    private static Integer solutino(int[] arr, int target) {
        if (arr == null || arr.length == 0) {
            return null;
        }

        int left = 0;
        int right = arr.length - 1;

        while (left <= right) {
            int mid = (left + right) / 2; // 오버플로우 가능 long 타입
            // int mid = left + (right-left) / 2;

            int a = Integer.MAX_VALUE;
            int b = Integer.MAX_VALUE - 10;
            
            int midAB = ( a + b ) / 2;
            int tempMidAB = a + (b - a) / 2;
            System.out.println("midAB = " + midAB);
            System.out.println("tempMidAB = " + tempMidAB);
            

            if (target == arr[mid]) {
                return mid;
            } else if (target < arr[mid]) {
                right = mid - 1;
            } else {
                left = mid + 1;
            }
        }

        return ((-1)*left) -1;

    }

}
