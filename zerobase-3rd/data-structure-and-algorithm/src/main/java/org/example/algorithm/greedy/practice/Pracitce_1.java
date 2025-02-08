package org.example.algorithm.greedy.practice;

import java.util.Arrays;

/**
 * 정수 nums 배열
 * 각 원소의 값은 해당 위치에서 오른쪽으로 이동할 수 있는 최대 값
 * 첫번쨰 위치에서 시작, 가장 끝까지 이동이 가능한지 판별
 * 이동 가능 true 아니면 false
 *
 * input 2,3,0,1,4
 * output true
 *
 * input 3, 0, 0, 1, 1
 * output true
 *
 * input 3,2,1,0,4
 * output false
 *
 */
public class Pracitce_1 {
    public static void main(String[] args) {
        int[] nums = {2,3,0,1,4};
        System.out.println(Arrays.toString(nums));
        System.out.println(solution(nums));
        System.out.println();

        nums = new int[]{3, 0, 0, 1, 1};
        System.out.println(Arrays.toString(nums));
        System.out.println(solution(nums));
        System.out.println();

        nums = new int[]{3,2,1,0,4};
        System.out.println(Arrays.toString(nums));
        System.out.println(solution(nums));
    }

    private static boolean solution(int[] nums) {
        int pos = 0;

        for (int i = 0; i < nums.length; i++) {
            if (pos < i) {
                return false;
            } else if (pos >= nums.length - 1) {
                return true;
            }

            pos = Math.max(pos, i + nums[i]);
        }

        return  true;
    }
}








