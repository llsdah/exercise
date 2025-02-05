package org.example.algorithm.twopointer.practice;

import java.util.Arrays;
import java.util.HashSet;

/**
 * nums1, nums2 배열
 * 두배열의 공통 원소를 배열로 반환하는 프로그램
 * 결과 배열의 원소에는 중복값이 없도록 하며 순서는 상관없다
 *
 * nums1 : 1,2,2,1
 * nums2 : 2,2
 * output : 2


 * nums1 : 4,9,5
 * nums2 : 9,4,9,8,4
 * output : 4,9 (or 9,4)
 *
 *
 *
 */
public class Practice_2 {

    public static void main(String[] args) {
        int[] nums1 = {1,2,2,1};
        int[] nums2 = {2,2};
        System.out.println(Arrays.toString(solution(nums1,nums2)));

        nums1 = new int[]{4,9,5};
        nums2 = new int[]{9,4,9,8,4};
        System.out.println(Arrays.toString(solution(nums1,nums2)));

    }

    private static int[] solution(int[] nums1, int[] nums2) {

        HashSet<Integer> set = new HashSet<>();

        Arrays.sort(nums1);
        Arrays.sort(nums2);

        int p1 = 0;
        int p2 = 0;

        while (p1 < nums1.length && p2 < nums2.length) {
            if (nums1[p1] < nums2[p2]) {
                p1 ++;
            } else if (nums1[p1] > nums2[p2]) {
                p2 ++;
            } else {
                set.add(nums1[p1]);
                p1++;
                p2++;
            }
        }

        int[] result = new int[set.size()];
        int idx = 0;
        for (Integer n : set) {
            result[idx++] = n;
        }

        return result;
    }
}
