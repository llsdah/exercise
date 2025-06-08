package org.example.algorithm.practice2;

import java.util.ArrayList;

/**
 * 몸무게 맞추기 대회
 * 상대 몸무게 에상 -> 그 무게 보다 n만큼 무게가 더 나간다는힌트
 * 1< n < 1000
 * 실제 몸무게 제곱 - 예상무게 제곱을 뺸값
 *
 * n 15
 * output {4,8}
 *
 */
public class Practice_2 {

    //
    private static void solution(int n) {
        int p1 = 1; // answer
        int p2 = 1; // expectation


        ArrayList result = new ArrayList();
        while (true) {
            int diff = p1* p1 - p2*p2;
            if (p1 -p2 == 1 && diff >n) {
                break;
            }


            if (diff < n) {
                p1 ++;
            } else {
                p2 ++;
            }


            if (diff == n) {
                result.add(p1);
            }
        }

        System.out.println("result = " + result);

    }


    public static void main(String[] args) {
        int n = 15;
        solution(n);
    }


}
