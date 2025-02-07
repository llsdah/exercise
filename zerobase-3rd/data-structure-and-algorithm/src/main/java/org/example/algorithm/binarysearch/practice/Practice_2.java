package org.example.algorithm.binarysearch.practice;

/**
 * 원형 정렬 상태 배열에서의 이진 탐색
 * nums 배열에 원형 상태로 데이터가 정렬되어 있을때,
 * 이진 탐색으로 데이터를 찾는 프로그램을 작성하시오
 *
 * 배열을 재 정렬 하지 않고 풀기
 *
 * arr L 4,5,6,7,8,0,1,2
 *
 * target 0, output 5
 * target 3, output 3
 *
 */



public class Practice_2 {
    public static void main(String[] args) {
        int[] arr = {4,5,6,7,8,0,1,2};
        System.out.println(solution(arr, 0));
        System.out.println(solution(arr, 3));
    }

    private static int solution(int[] arr, int target) {
        if (arr == null || arr.length == 0) {
            return -1;
        }

        int left = 0;
        int right = arr.length -1;

        while (left <= right) {
            int mid = (left + right) / 2;

            if (target == arr[mid]) {
                return mid;
            }
            // 4,5,6,7,0,1,2 원형 정렬 상태
            if (arr[left] <= arr[mid]) {
                if (target >= arr[left] && target < arr[mid]) {
                    right = mid -1;
                } else {
                    left = mid + 1;
                }
            } else {
                // 11, 5,6,7,8,9,10
                if (target > arr[mid] && target <= arr[right]) {
                    left = mid + 1;
                } else {
                    right = mid - 1;
                }
            }

        }
        return -1;
    }
}
