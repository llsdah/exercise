package org.example.algorithm.dynamic;

public class Dynamic {
    /**
     * 동적 계획법
     * - 큰 문제를 부분 문제로 나눈 후 답을 찾아가는 과정에서,( 분할정복과 비슷 )
     *      계산된 결과를 기록하고 재활용해 문제의 답을 구하는 방식
     * - 중간 계산결과 기록 메모리 필요
     *
     * 분할정복과 차이
     * - 분할 정복은 부분 문제가 중복되지 않음
     * - DP는 부분 문제가 중복되어 재활용 사용
     *
     * 그리디 알고리즘
     * - 순간의 최선을 구하는 방식
     * - DP는 모든 방법을 확인 후 최적해 구하는 방식
     *
     * 구현 방법 1 = Tabulation
     * - 상향식 접근방법
     * - 작은 하위 문제 부터 올라감
     * - 모두 계산하면서 차례대로 진행
     *
     * 구현 방법 2 = Memoization
     * - 하향식 접근 방법
     * - 큰문제에서 하위문제를 확인
     * - 계산이 필요한 순간 계산하며 진행
     *
     */

    // 피보나치 수열 계산했던 부분도 다시 계산
    private static int recursive(int n) {
        if (n <= 1) {
            return n;
        } else {
            return recursive(n-1) + recursive(n-2);
        }
    }

    // 피보나치 수열 DP 풀이 타뷸레이션
    private static int tabulation(int n) {
        int[] dp = new int[n < 2 ? 2 : n + 1];

        dp[0] = 0;
        dp[1] = 1;

        for (int i = 2; i <= n; i++) {
            dp[i] = dp[i-1] + dp[i-2];
        }

        return dp[n];
    }

    // 피보나치 수열 DP 메모이제이션
    static int[] dp = new int[8];
    private static int memoization(int n) {
        if (n <= 2) {
            return 1;
        }

        if (dp[n] != 0) {
            return dp[n];
        }

        dp[n] = memoization(n-1) + memoization(n-2);

        return dp[n];
    }



    public static void main(String[] args) {
        // 피보나치 수열 구하기
        System.out.println("recursive : "+recursive(7));
        System.out.println("DP tabulation : "+tabulation(7));
        System.out.println("DP memoization : "+memoization(7));

    }

}
