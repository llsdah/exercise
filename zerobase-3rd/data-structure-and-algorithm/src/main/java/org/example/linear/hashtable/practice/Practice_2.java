package org.example.linear.hashtable.practice;

import java.util.Arrays;
import java.util.Hashtable;

/**
 * 정수형 배열 nums 와 taret 이 주어졌을때
 * num에서 임의 두수를 더해 target을 구할 수 있는 지 확인하는 프로그램 개발
 * 두수의 합으로 target을 구할 수 있으면 index 반환
 * 아니며 null
 *
 */
public class Practice_2 {
    public static void main(String[] args) {
        int[] nums = {7, 11, 5, 3};
        int target = 10;
        System.out.println(Arrays.toString(solution(nums,target)));
        nums = new int[]{8,3,-2};
        target = 6;
        System.out.println(Arrays.toString(solution(nums,target)));
        nums = new int[]{1,2,3};
        target = 12;
        System.out.println(Arrays.toString(solution(nums,target)));


    }

    private static int[] solution(int[] nums, int target) {
        
        int[] result = new int[2];

        Hashtable<Integer,Integer> ht = new Hashtable<>();

        for (int i = 0; i < nums.length; i++) {
            if (ht.containsKey(nums[i])){
                result[0] = ht.get(nums[i]);
                result[1] = i;
                return result;
            }

            ht.put(target - nums[i],i);
        }

        return null;
    }
}
