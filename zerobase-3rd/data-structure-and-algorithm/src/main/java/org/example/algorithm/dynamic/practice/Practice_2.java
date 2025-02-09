package org.example.algorithm.dynamic.practice;

public class Practice_2 {
    /**
     * 수열 arr 이 주어졌을 때,
     * 부분 수열 중 증가하는 부분이 가장 긴 길이를 출려하는 프로그램
     *
     * input : { 10, 20, 30, 10, 50, 10}
     * outpu : 4
     *
     */

    public static void main(String[] args) {
        int[] arr = new int[]{10,20,30,10,50,10};
        System.out.println(solution(arr));
    }

    private static int solution(int[] arr) {
        int length = arr.length;
        int[] dp = new int[arr.length + 1];

        int result = 0;

        for (int i = 1; i <= length; i++) {
            dp[i] = 1;
            for (int j = 1; j < i; j++) {
                // 증가하는 형태
                if (arr[j - 1] < arr[i - 1]) {
                    dp[i] = Math.max(dp[i],dp[j]+1);
                }
            }
            result = Math.max(result, dp[i]);
        }

        return result;
    }
}





