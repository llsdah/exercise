package org.example.mathematics;

import java.sql.SQLOutput;

/**
 *
 */
public class ExponentialLog {

    public static void main(String[] args) {

        // 제곱
        System.out.println(Math.pow(2,3));
        System.out.println(Math.pow(2,-3));
        System.out.println(Math.pow(-2,3));

        System.out.printf("%.0f\n", Math.pow(2,10));

        // 제곱근
        System.out.println(Math.sqrt(16));
        System.out.println(Math.pow(16,1.0/2));
        System.out.println(Math.pow(2,1.0/2));

        // 절대 값
        System.out.println(Math.abs(5));
        System.out.println(Math.abs(-5));

        // 로그
        System.out.println(Math.log(1));// 자연 상수
        System.out.println(Math.E);
        System.out.println(Math.log10(1000));
        System.out.println(Math.log(4) / Math.log(2));


    }
}
