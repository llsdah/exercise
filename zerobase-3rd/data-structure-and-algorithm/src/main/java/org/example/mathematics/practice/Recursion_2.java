package org.example.mathematics.practice;

public class Recursion_2 {

    // 유클리드 호제법
    static int gcd1(int a, int b) {

        if (a%b == 0){
            return b;
        }
        return gcd1(b, a%b);
    }

    static int gcd2(int a, int b) {
        while (b != 0) {
            int temp = b;
            b = a % b;
            a = temp;
        }
        return a;
    }

    public static void main(String[] args) {

        System.out.println(gcd1(3,5));
        System.out.println(gcd2(2,6));
        System.out.println(gcd1(8,12));

    }
}
