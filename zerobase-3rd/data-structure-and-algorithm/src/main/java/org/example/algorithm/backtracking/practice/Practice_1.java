package org.example.algorithm.backtracking.practice;

import java.lang.reflect.Array;
import java.util.Arrays;

/**
 * 정수형 n m 주어졌을 때,
 * 1 ~ n까지의 정수 중 중복없이 m개를 고른 수열을 출력하는 프로그램
 * 순열 출력
 *
 * 입출력 예시
 * n : 3
 * m : 2
 * output : {1,2}, {1,3}, {2,1}, {2,3}, {3,1}, {2,3}
 */
public class Practice_1 {
    public static boolean[] visited;
    public static int[] out;
    // 순열
    private static void solution(int n, int m) {
        visited = new boolean[n+1];
        out = new int[m]; // 선택 수

        permutaion(n,m,0);

    }

    public static void permutaion(int n, int m, int depth) {
        if (depth == m) {
            System.out.println(Arrays.toString(out));
            return;
        }

        for (int i = 1; i <= n; i++) {
            if (!visited[i]) {
                visited[i] = true;
                out[depth] = i;
                permutaion(n, m, depth + 1);
                visited[i] = false;
            }
        }
    }

    public static void main(String[] args) {
        solution(3,2);
        System.out.println();
        solution(4,3);
    }

}
