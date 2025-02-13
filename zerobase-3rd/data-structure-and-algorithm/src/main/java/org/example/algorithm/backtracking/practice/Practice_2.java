package org.example.algorithm.backtracking.practice;

import java.util.*;
/**
 * 숫자 7193 소수
 * 719 소수, 71 소수 , 7도 소수
 * n이 주어졌 떄 n 자리 수 중 위와 같은 소수를 찾는 프로그래밍
 *
 * input : 3
 * output : 233, 239, 293, 313, 317, 373, 379, 593, 599, 719, 733, 739, 797
 *
 *
 */
public class Practice_2 {

    public static ArrayList<Integer> result;
    // 소수 2,3,5,7 로 시작
    private static ArrayList<Integer> solution(int num) {
        result = new ArrayList<>();

        int[] primeNum = {2,3,5,7};

        for (int i = 0; i < primeNum.length; i++) {
            // backtracking
            backTracking(primeNum[i],1,num);
        }

        return result;
    }

    //
    public static void backTracking(int prime, int len, int n) {
        //
        if (len >= n) {
            result.add(prime);
            return;
        }

        // 뒤에 붙을 가능한 숫자 0 ~ 9
        for (int i = 0; i < 10; i++) {
            // 2로 나누어 떨어지면 소수아님 , 뒤에 5가 와도 아닙
            if (i % 2 != 0 || i != 5) {
                // 자리숫 증가 및 숫자 붙이기
                int primeCandidate = prime * 10 + i;

                if (isPrimeNum(primeCandidate)) {
                    backTracking(primeCandidate, len+1,n);
                }
            }
        }

    }

    public static boolean isPrimeNum(int num) {
        for (int i = 2; i <= num / 2; i++) {
            if (num % i == 0) {
                return false;
            }
        }
        return true;
    }

    public static void main(String[] args) {
        int num = 3;
        System.out.println(solution(num));
        num = 4;
        System.out.println(solution(num));

    }

}
