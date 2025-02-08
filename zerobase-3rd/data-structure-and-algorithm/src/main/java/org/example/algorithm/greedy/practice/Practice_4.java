package org.example.algorithm.greedy.practice;

import org.w3c.dom.css.CSSUnknownRule;

import java.lang.reflect.Array;
import java.util.Arrays;

/**
 * 원형 n 개의 주유소
 * 각 주유소의 보유량 배열이 주어짐
 * 해당 주유소에서 다음 주유소로 가는 이동 비용이 배열로 주어짐
 *
 * 어떤 위치에서 차량이 가스를 채워 출발하면 모든 주유소를 방문하고 원래의 위치로 돌아올 수 있는지
 *
 *
 * input
 * gas : 1,2,3,4,5
 * cost : 3,4,5,1,2
 * output : 3
 *
 * gas : 2,3,4
 * cost : 3,4,3
 * output : -1
 *
 *
 *
 */
public class Practice_4 {
    public static void main(String[] args) {
        int[] gas = {1,2,3,4,5};
        int[] cost = {2,3,4,5,1};
        System.out.println("gas : "+Arrays.toString(gas));
        System.out.println("gas : "+Arrays.toString(cost));
        System.out.println(solution(gas,cost));

        gas = new int[]{2,3,4};
        cost = new int[]{3,4,5};
        System.out.println("gas : "+Arrays.toString(gas));
        System.out.println("gas : "+Arrays.toString(cost));
        System.out.println(solution(gas,cost));

    }

    // 출발이 음수에서 양수로 전환되는 경우가 출발점
    private static int solution(int[] gas, int[] cost) {

        int result = -1;

        if (gas == null || cost == null) {
            return result;
        }


        int curGas = 0;
        int totalGas = 0;
        int startPos = 0;

        for (int i = 0; i < gas.length; i++) {
            curGas += gas[i] - cost[i];
            totalGas += gas[i] - cost[i];

            if (curGas < 0) {
                startPos = i + 1;
                curGas = 0;
            }

        }


        return totalGas >= 0 ? startPos : -1;
    }

}












