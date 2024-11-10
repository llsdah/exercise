package org.example.mathematics;

import java.util.stream.IntStream;

/**
 * 순열
 * 순서를 정해서 나열
 * 서로다른 n개 중에 r개를 선택하는 경우의 수 - 순서
 * n!/(n-r)!
 *
 * 중복순열
 * 서로다른 n개 중 r개를 선택하는 경우의 수 - 순서와 중복
 *
 * 원순열
 * 원모양 테이블에 n개의 원소를 나열하는 수
 * (n-1)!
 *
 */
import java.util.*;
public class Permutation1 {
    public static void main(String[] args) {


        System.out.println("펙토리얼");
        int n = 5;
        int result = 1;

        for (int i = 1; i<=n; i++) {
            result *= i;
        }
        System.out.println("result = " + result);
        System.out.println(IntStream.range(1,6).reduce(1, (x,y) -> (x*y)));


        // 순열 - 5명을 3줄로 세우는 경우의수
        n = 4;
        int r = 3;
        result = 1;

        for (int i = n; i >= n-r+1; i--) {
            result *= i;
        }
        System.out.println("result = " + result);


        // 중복 순열 - 서로다른 4개의 수에서 2개를 뽑는 경우
        n = 4;
        r = 2;
        result = 1;

        for (int i = 0; i < r; i++) {
            result *= n;
        }

        System.out.println("result = " + result);

        // 원 순열 - 3명 앉히기
        n = 3;
        result = 1;

        for (int i = 1; i < n; i++) {
            result *= i;
        }
        System.out.println("result = " + result);


    }
}










