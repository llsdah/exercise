package org.example.mathematics.practice;

import java.util.*;
/**
 * 카탈랑의 수
 * 1,1,2,5,14,42,132 ...
 *
 */
public class Practice_6 {

    private static int solution (int n) {
        int result = 0;

        if (n <= 1) {
            return 1;
        }
        for (int i = 0; i < n; i++) {
            result += (solution(i) * solution(n-i-1));
        }
        return result;
    }

    public static void main(String[] args) {
        System.out.println(solution(0));
        System.out.println(solution(2));
        System.out.println(solution(5));
        System.out.println(solution(7));
    }

}
