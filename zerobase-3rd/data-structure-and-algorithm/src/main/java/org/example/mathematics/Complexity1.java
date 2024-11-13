package org.example.mathematics;

/**
 * 복잡도
 * 시간복잡도 - 필요연산 횟수
 *
 * 공간복잡도 - 메모리 사용량
 * int[] a = new int[1000] // 4kb
 * int[][] a = new int[1000][1000] // 4MB
 *
 */

public class Complexity1 {

    // O(2^N)
    static int fibonacci(int n) {
        if (n<3) {
            return 1;
        }
        return fibonacci(n-1) + fibonacci(n-2);
    }

    // O(N)
    static int factorial(int n) {
        if (n < 1) {
            return 1;
        }
        return n * factorial(n-1);
    }

    public static void main(String[] args) {

        // O(1)
        System.out.println("hello");

        // O(logN)
        for (int i = 1; i < 16; i*=2) {
            System.out.println("i + "+i);
        }

        // O(N)
        for (int i = 0; i < 16; i++) {
            System.out.println("i = " + i);
        }

        // O(NlogN)
        for (int i = 0; i < 2; i++) {
            for (int j = 1; j < 8; j*=2) {
                System.out.println(" = hello");
            }
        }

        // O(N*2)
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                System.out.println("hello");
            }
        }

    }

}




