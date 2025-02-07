package org.example.algorithm.binarysearch;

/**
 * 이진 탐색
 * - "정렬된 상태"의 데이터에서 특정값을 빠르게 탐색하는 방법
 *      찾고자 하는 값과 데이터 중앙 비교
 *      -> 찾고자 하는 값이 더 작으면 왼쪽
 *      -> 찾고자 하는 값이 크면 오른쪽
 *
 *
 * -
 */
public class BinarySearch {

    public static void main(String[] args) {
        int[] arr = {1, 3, 5, 10, 20,30, 40, 50, 60};

        System.out.println("Loop method index : "+ binarySearch(arr, 30));
        System.out.println();

        System.out.println("Recursive Call method index : "+ binarySearch2(arr, 30, 0, arr.length - 1));

    }
    // 반복문으로 구현
    private static int binarySearch(int[] arr, int target) {
        int left = 0;
        int right = arr.length-1;

        while (left <= right) {
            int mid = (left + right) / 2;
            if (target == arr[mid]) {
                return mid;
            } else if (target < arr[mid]) {
                // 작은 경우
                right = mid - 1;
            } else {
                // 더 큰 경우
                left = mid + 1;
            }
        }

        return -1;
    }

    // 재귀 호출 구조
    private static int binarySearch2(int[] arr, int target, int left, int right) {

        if (left > right) {
            return -1;
        }

        int mid = (left + right) / 2;

        if (target == arr[mid]) {
            return mid;
        } else if (target < arr[mid]) {
            return binarySearch2(arr, target, left, mid - 1 );
        } else {
            return binarySearch2(arr, target, mid + 1, right);
        }

    }


}
