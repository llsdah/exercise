package org.example.mathematics.practice;
import java.util.*;

/**
 * 행복한 수 찾기
 * 각 자리수를 제곱한 것을 더하는 과정을 반복했을 떄 1로 끝나는 수
 *
 */
public class Practice_4 {

    private static boolean solution(int n){
        HashSet<Integer> set = new HashSet<>();

        while (set.add(n)) {
            int sqaureSum = 0;
            while (n > 0) {
                int remain = n % 10;
                sqaureSum += remain * remain; // 제곱
                n /= 10; // 자리수별 계산
            }

            if (sqaureSum == 1) {
                return true;
            } else {
                n = sqaureSum;
            }

        }

        return false;
    }

    public static void main(String[] args) {
        System.out.println(solution(31));
        System.out.println(solution(2));
        System.out.println(solution(61));
    }

}
