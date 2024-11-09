package org.example.mathematics;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;

/**
 * 경우의수
 *
 * 합의법칙
 * 사전 A 또는 B 가 일어날 경우의 수
 *
 * 곱의 법칙
 * 사건 A와 B가 동시에 일어날 경우의 수
 *
 *
 *
 */
public class NumberOfCases1 {

    public static void main(String[] args) {

        // 합의 법칙
        System.out.println("합의 법치");


        int[] dice1 = {1,2,3,4,5,6};
        int[] dice2 = {1,2,3,4,5,6};


        int nA = 0;
        int nB = 0;
        int AandB = 0;

        // 기본 풀이
        // 두 주사위를 던졌을 때 합이 3 또는 4의 배수인 경우
        for (int item1: dice1) {
            for (int item2 : dice2) {

                if ((item1 + item2) % 3 == 0) {
                    nA += 1;
                }

                if ((item1 + item2) % 4 == 0) {
                    nB += 1;
                }
                // 최소 공배수
                if ((item1 + item2) % 12 == 0) {
                    AandB += 1;
                }

            }
        }
        System.out.println("결과 = " + (nA+nB-AandB));

        HashSet<ArrayList> allCase = new HashSet<>();

        for (int item1 : dice1) {
            for (int item2 : dice2) {
                if ((item1 + item2)%3 == 0 ||(item1 + item2)%4 == 0 ){
                    ArrayList list = new ArrayList(Arrays.asList(item1,item2));
                    allCase.add(list); // 모든 중복된 정보 제외
                }
            }
        }

        System.out.println("allCase.size() = " + allCase.size());


        // 곱의 법칙
        // 주사위 던질 경우 a는 3의 배수, b는 4의 배수인 경우의 수
        nA = 0;
        nB = 0;

        for (int item1 : dice1) {
            if (item1 % 3 == 0){
                nA++;
            }
        }

        for (int item1 : dice2) {
            if (item1 % 4 == 0){
                nB++;
            }
        }

        System.out.println("(nA*nB) = " + (nA*nB));



    }

}
