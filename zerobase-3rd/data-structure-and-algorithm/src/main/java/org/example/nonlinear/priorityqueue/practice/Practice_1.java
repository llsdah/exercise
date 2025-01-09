package org.example.nonlinear.priorityqueue.practice;

import java.util.Arrays;
import java.util.PriorityQueue;

/**
 * nums 배열에 주어진 정수들 중에서 k번째 큰 수를 반환하는 프로그램을 작성하시오
 *
 * 입력 예시
 * 입력 : 3,1,2,7,6,4
 * K : 2
 * 출력 : 6
 *
 * 입력 : 1, 3, 7, 4, 2, 8, 9
 * k : 7
 * 출력 : 1
 *
 */
public class Practice_1 {

    public static void main(String[] args) {

        int[] nums = {3, 1, 2, 7, 6, 4};

        System.out.println(solution1(nums,2));
        System.out.println(solution2(nums,2));
        System.out.println();

    }

    private static Integer solution1(int[] nums, int k) {
        PriorityQueue<Integer> pq = new PriorityQueue<>();

        for (int num : nums) {
            pq.offer(num);

            if (pq.size() > k) {
                pq.poll();
            }

        }

        return pq.peek();
    }

    private static Integer solution2(int[] nums, int k) {
        Arrays.sort(nums);
        return nums[nums.length - k];
    }

}
