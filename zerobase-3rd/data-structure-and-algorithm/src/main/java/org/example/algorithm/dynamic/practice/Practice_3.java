package org.example.algorithm.dynamic.practice;

import java.util.Arrays;

/**
 * 배날에 물품을 담으려고한다
 * 배낭에는 k 무게 만큼의 물품을 담을 수 있다
 * n 개의 물품이 주어지고 이물품의무게와 가치 정보가 items 2 차원 배열이 주어졌을 때
 * 최대 가치가 되도록 물품을 담았을 떄의 가치를 출력하는 프로그램
 *
 * input : {{6,13},{4,8},{3,6},{5,12}}
 * n : 4 //물품 4개
 * k : 7 // 담을 수 있는 무게
 * output : 14
 *
 *
 */
public class Practice_3 {
    public static void main(String[] args) {
        // 정렬한번 하고 가야됨
        int[][] item = {{6,13},{4,8},{3,6},{5,12}};
        int n = 4;
        int k = 7;

        System.out.println(solution(item, n, k));
    }

    // 설계 tebulation 으로 각 무게별 최대가치 산정
    private static int solution(int[][] item, int n, int k) {
        // 물품갯 수 , 무게 수
        int[][] dp = new int[n + 1][k + 1];

        for (int i = 0; i < n; i++) { // 물품 넣기
            for (int j = 1; j <= k; j++) {
                if (item[i][0] > j) { // 현재 넣으려고하는 물건이 j보다 크다면 직전것 가지고 옴
                    dp[i+1][j] = dp[i][j];
                } else {
                    // 위에 써놓은 기존 물건, 현재 지금 넣는 물건 가치 + 무게 뺸것의 가치
                    dp[i+1][j] = Math.max(dp[i][j], dp[i][j - item[i][0]] + item[i][1]);
                }
            }
        }

        for (int i = 0; i < dp.length; i++) {
            System.out.println(Arrays.toString(item[i])+" || "+Arrays.toString(dp[i]));
        }

        return dp[n][k];
    }

}
