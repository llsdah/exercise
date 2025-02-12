package org.example.algorithm.divideandconquer.practice;

/**
 * 정수형 배열, 연속된 부분 배열의 합 중 가장 큰값
 * => 투포인터? 구간씩 구하면 쉽다
 *
 * 입출력 예시
 * input : -5, 0 , -3, 4 , -1, 3, 1,-5, 8
 * output : 10
 *
 * input : 5,4,8,7,8
 * output : 24
 *
 */
public class Practice_1 {

    public static void main(String[] args) {
        int[] nums = {-5, 0 , -3, 4 , -1, 3, 1,-5, 8};
        System.out.println(solution(nums));

        nums = new int[]{5,4,0,7,8};
        System.out.println(solution(nums));
    }

    private static int solution(int[] nums) {
        if (nums == null || nums.length == 0) {
            return -1;
        }

        return divideSubArray(nums, 0, nums.length - 1);
    }

    private static int divideSubArray(int[] nums, int left, int right) {
        if (left == right) {
            return nums[left];
        }

        int mid = left + (right - left) / 2; // 오버플로우 방지를 위해서
        int maxLeft = divideSubArray(nums, left, mid);
        int maxRight = divideSubArray(nums,mid+1, right);

        int maxArr = getMaxSubArray(nums, left, mid, right);

        return Math.max(maxLeft, Math.max(maxRight, maxArr));
    }

    private static int getMaxSubArray(int[] nums, int left, int mid, int right) {
        int sumLeft = 0;
        int maxLeft = Integer.MIN_VALUE;

        for (int i = mid; i >= left; i--) {
            sumLeft += nums[i];
            maxLeft = Math.max(maxLeft,sumLeft);

        }
        int sumRight = 0;
        int maxRight = Integer.MIN_VALUE;

        for (int i = mid + 1; i <= right ; i++) {
            sumRight += nums[i];
            maxRight = Math.max(maxRight, sumRight);
        }
        return maxLeft + maxRight;
    }

}






