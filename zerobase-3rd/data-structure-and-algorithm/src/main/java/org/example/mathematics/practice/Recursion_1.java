package org.example.mathematics.practice;

public class Recursion_1 {

    static int factorial(int n) {
        if (n == 1){
            return 1;
        }
        return n * factorial(n-1);
    }

    public static void main(String[] args) {

    }
}
