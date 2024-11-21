package org.example.mathematics.practice;
import java.util.*;

/**
 * 좋은 수
 * 1이상의 정수 n이 주어졌을떄 좋은 수가 몇개냐 !
 * 짝수 인덱스에는 짝수, 홀수 인덱스에는 소수 ( 2,3,5,7)
 * 2582 는 좋은 수
 * 3245 나쁜 수
 *
 * 0번 인덱스는 5개 , 홀수번 인덱스는 4개
 * 5 * 4 * 5 * 4
 */
public class Practice_9 {

    final static int mod = (int) 1e9 + 7;

    public static int solution(long n) {
        int answer = 0;
        answer = (int) (recursion(5, ( n + 1 ) / 2) * recursion(4, n / 2) % mod );
        return answer;
    }
    // y는 몇번 실행 해줄지 , x 실행할값
    public static long recursion(long x, long y) {
        if ( y == 0 ) {
            return 1;
        }
        long p = recursion(x, y / 2);
        return p * p * ( y % 2 > 0 ? x : 1) % mod;
    }


    public static void main(String[] args) {
        System.out.println((long) 1/2);
        System.out.println(solution(1));
        System.out.println(solution(2));
        System.out.println(solution(3));
        System.out.println(solution(4));
        System.out.println(solution(50));
    }

}
