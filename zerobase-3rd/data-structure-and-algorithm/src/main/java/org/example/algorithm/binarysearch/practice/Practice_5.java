package org.example.algorithm.binarysearch.practice;

/**
 * 정수형 배열 nums 와 정수 m 주어 졌다.
 * nums = 양의 정수값 , m 배열을 부분 배열로 분리할 수 있는 수
 * 각 부분배열의 합 중 가장 큰 값이 최소가 되도록 분리하자.
 *
 *
 * nums 7,2,5,10,8
 * m 2
 * output 18
 *
 * nums 1,2,3,4,5
 * m 2
 * output 9
 *
 */

public class Practice_5 {

    // left 배열의 최대수, right 배열의 총합
    public static void main(String[] args) {
        int[] nums = {7,2,5,10,8};
        int m = 2;
        System.out.println(solution(nums,m));
        nums = new int[]{1,2,3,4,5};
        m = 2;
        System.out.println();
        System.out.println(solution(nums,m));

        nums = new int[]{7,2,5,10,8};
        m = 3;
        System.out.println(solution(nums,m));


    }

    private static int solution(int[] nums, int m) {
        int left = 0;
        int right = 0;

        for (int num : nums) {
            left = Math.max(left, num);
            right += num;
        }

        if (m == 1) {
            return right;
        }

        while (left <= right) {
            int mid = (left + right) / 2;
            int cnt = 1;
            int total = 0;

            for (int num : nums) {
                total += num;
                if (total > mid) {
                    total = num;
                    cnt++;
                }
            }

            if (cnt <= m ) {
                right = mid - 1;
            } else {
                left = mid + 1;
            }

        }


        return left;
    }

}









