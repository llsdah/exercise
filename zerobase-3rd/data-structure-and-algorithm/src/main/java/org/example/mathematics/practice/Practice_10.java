package org.example.mathematics.practice;

/**
 * 하노이의 탑
 * 원판 갯수 n개 주어집니다.
 *
 * *********************하나도 이해가 안감...
 */
public class Practice_10 {

    static StringBuffer sb;
    private static void solution(int n) {
        sb = new StringBuffer();
        hanoi(n, 1,2,3);
        System.out.println(sb.toString());
    }

    public static void hanoi(int n, int start, int mid, int to) {
        // 한개만 남았을때
        if (n==1) {
            sb.append(start + " " + to + "\n");
            return;
        }

        hanoi(n-1, start, to, mid);

        sb.append(start + " "+ to + "\n");

        hanoi(n-1, mid, start, to);

    }

    public static void main(String[] args) {
        solution(2);
        System.out.println();
        solution(4);
        System.out.println();
        solution(6);
    }
}
