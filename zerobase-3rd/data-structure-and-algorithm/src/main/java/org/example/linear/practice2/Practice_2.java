package org.example.linear.practice2;

import java.util.ArrayList;
import java.util.Stack;

/**
 * 수열 주어 질떄 stack으로
 * - 같은 값이 두번 나오는 구조는 없다
 * - 스택은 오름차순
 *
 * 수열을 만들 수 있으면 push, pop 을 순차로 표현
 * 만들 수 없다면, NO 출력
 *
 */
public class Practice_2 {

    public static void main(String[] args) {
        int[] arr = { 1, 2, 5, 3, 4 };
        System.out.println(solution(arr));

        arr = new int[]{4,3,6,8,7,5,2,1};
        System.out.println(solution(arr));
    }

    private static String solution(int[] nums) {
        String answer = "";
        Stack<Integer> stack = new Stack<>();
        ArrayList<String> result = new ArrayList<>();

        int idx = 0;
        for (int i = 0; i < nums.length; i++) {
            int num = nums[i];

            if (num > idx) {
                //stack 쌓기
                for (int j = idx+1; j < num+1; j++) {
                    stack.push(j);
                    result.add("+");
                }
                idx = num;
            } else if (stack.peek() != num) {
                answer = "No";
                return answer;
            }

            stack.pop();
            result.add("-");

        }

        return result.toString();
    }

}
