package org.example.algorithm.greedy.practice;

/**
 * 양의 정수 n 다음 연산
 * - n 짝수이면 2로 나누기 연산
 * - n 홀수이면 1더하거나 1 빼기 연산
 * 주어진 n 이  1이되는 최소한의 연산
 *
 * input : 8
 * output : 3
 *
 * intput : 7
 * output : 4
 *
 * input : 9
 * output : 4
 *
 */
public class Practice_3 {

    public static void main(String[] args) {

        int num = 7;

        System.out.println("num : "+num +" , "+solution(num));
        num = 9;
        System.out.println("num : "+num +" , "+solution(num));
        num = 8;
        System.out.println("num : "+num +" , "+solution(num));


    }
    // 4배수?
    public static int solution(int num) {
        int result = 0;

        if (num == 0 || num == 2) {
            return 1;
        }

        while (num != 1) {

            if (num == 3) {
                result += 2;
                break;
            }

            if (num % 2 == 0) {
                num /= 2;
            } else if ((num+1) % 4 == 0) {
                num += 1;
            } else if ((num-1) % 4 == 0){
                num -= 1;
            }

            result ++;

        }

        return result;
    }


}
