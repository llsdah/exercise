package org.example.algorithm.sort.practice;

/**
 * nums
 * 오름차순 정렬하기 위해 배열 내 정렬이 필요한 구간의 길이를 출력하라
 *
 * 입력
 * 2,6,4,8,5,3,9,10
 * 출력
 * 5
 *
 * 입력
 * 1,3,1
 * 출력
 * 2
 *
 */
public class Practice_4 {

    public static void main(String[] args) {
        int[] nums = {2,6,4,8,5,3,9,10};
        System.out.println("length : " + solution(nums));

        nums = new int[]{1,3,1};
        System.out.println("length : " + solution(nums));

        nums = new int[]{1,9,3,4,5};
        System.out.println("length : " + solution(nums));
        nums = new int[]{1,2,3,4,5};
        System.out.println("length : " + solution(nums));

    }

    private static int solution(int[] nums) {
        if (nums == null || nums.length < 2) {
            return -1;
        }

        int min = Integer.MAX_VALUE;
        int firstIdx = 0;
        
        // 가장 좌측값을 찾을 것입니다.
        // {2,6,4,8,5,3,9,10}
        System.out.println();
        for (int i = nums.length - 1; i >= 0 ; i--) {
            
            min = Math.min(min, nums[i]); // 작은 값 업데이트
            if (nums[i] > min) {
                System.out.print("nums[i] = " + nums[i] +", ");
                System.out.print("min = " + min  + ", ");
                System.out.print("i = " + i + ", ");
                firstIdx = i;
            }
        }
        System.out.println();

        System.out.println("firstIdx = " + firstIdx);
        int max = Integer.MIN_VALUE;
        int lastIdx = -1;
        for (int i = 0; i < nums.length; i++) {
            max = Math.max(max, nums[i]);
            if (nums[i] < max) {
                lastIdx = i;
            }
        }
        System.out.println("lastIdx = " + lastIdx);

        return lastIdx - firstIdx + 1;

    }


}
