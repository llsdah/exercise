package org.example.algorithm.dynamic.practice;

/**
 * 정수가 아래 연산을 통해 1로 만들려고한다
 * 2로 나누어떨어지면 2로 나누고
 * 3으로 나우어 떨어지면 3으로 나누고
 * 1 뺴기
 *
 *
 * 위으 연산을 통해 1로 만들때 필요한 최소한의 연산 횟수 출력
 *
 * input : 2
 * output : 1
 *
 * input 9
 * output 2
 *
 */
public class Practice_1 {

    public static void main(String[] args) {

        System.out.println(solution(2)); //1
        System.out.println(solution(4)); //2
        System.out.println(solution(9)); //2
        System.out.println(solution(10)); //3

    }

    // 1ㅂ터 차근히 수행

    private static int solution(int n) {
        int[] dp = new int[n+1];

        for (int i = 2; i <= n; i++) {
            dp[i] = dp[i-1] + 1; // 기본연산
            if (i % 2 == 0) {
                dp[i] = Math.min(dp[i], dp[i/2] + 1);
            }
            if (i % 3 == 0) {
                dp[i] = Math.min(dp[i], dp[i/3] + 1);
            }

        }

        return dp[n];
    }

}












