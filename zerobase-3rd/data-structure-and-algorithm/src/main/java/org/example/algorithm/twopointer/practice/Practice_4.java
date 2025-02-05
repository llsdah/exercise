package org.example.algorithm.twopointer.practice;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * nums 배열엣 3개의 합이 0이 되는 모든 숫자들을 출력
 * 중복된 숫자 set은 제외하고 출력한다
 *
 *  input {-1,0,1,2,-1,-4};
 *  output {{-1,-1,2},{-1,0,1}};
 *
 *  input {1,-7,6,3,5,2,}
 *  output {{-7,1,6},{-7,2,5}}
 *
 */

public class Practice_4 {

    public static void main(String[] args) {
        // 정렬 하고 -> 기준 숫자 선택 -> 양끝에서 합하면서 0이 되는지 확인
        int[] nums = {-1,0,1,2,-1,-4};
        System.out.println(solution(nums));
        nums = new int[]{1,-7,6,3,5,2};
        System.out.println(solution(nums));

    }

    private static ArrayList<ArrayList<Integer>> solution(int[] nums) {
        Arrays.sort(nums);

        ArrayList<ArrayList<Integer>> result = new ArrayList<>();

        for (int i = 0; i < nums.length - 2; i++) { // p1, p2 를 위해서
            if (i == 0 || i > 0 && nums[i] != nums[i-1]) {
                int p1 = i + 1;
                int p2 = nums.length - 1;
                int sum = 0 - nums[i]; // 기준점

                while (p1 < p2) {

                    if (nums[p1] + nums[p2] == sum) {
                        result.add(new ArrayList<>(Arrays.asList(nums[i],nums[p1],nums[p2])));
                        // 중복값 제외 = 연속된 수 제외
                        while (p1 < p2 && nums[p1] == nums[p1 + 1]) {
                            p1 ++;
                        }
                        while (p1 < p2 && nums[p2] == nums[p2 - 1]) {
                            p2 --;
                        }

                        p1++;
                        p2--;

                    } else if (nums[p1] + nums[p2] < sum) {
                        p1++;
                    } else {
                        p2--;
                    }
                }
            }
        }

        return result;
    }
}
