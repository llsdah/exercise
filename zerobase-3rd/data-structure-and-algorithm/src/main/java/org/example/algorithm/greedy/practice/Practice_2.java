package org.example.algorithm.greedy.practice;

/**
 * 양의 정수 배열 prices 가 주어졌다.
 * 각 원소의 의미는 날짜별 주식 가격을 의미한다.
 * 한번에 한주만 보유 prices 보고 사고 팔기 반복
 * 얻을 수 있는 최대 수익을 반환하는 프로그램 작성
 *
 *
 * input : 5,1,6,4,3,5
 * output : 7
 *
 * input : 1,2,3,4,5
 * output : 4
 */
public class Practice_2 {

    public static int solution(int[] prices) {
        if (prices == null || prices.length < 2) {
            return 0;
        }

        int profit = 0;
        for (int i = 1; i < prices.length; i++) {
            if (prices[i] > prices[i - 1]) {
                profit += prices[i] - prices[i - 1];
            }
        }

        return profit;
    }

    public static void main(String[] args) {

        int[] prices = {5,1,6,4,3,5};
        System.out.println(solution(prices));

        prices = new int[] {1,2,3,4,5};
        System.out.println(solution(prices));

        prices = new int[] {5,4,3,2,1};
        System.out.println(solution(prices));



    }

}
