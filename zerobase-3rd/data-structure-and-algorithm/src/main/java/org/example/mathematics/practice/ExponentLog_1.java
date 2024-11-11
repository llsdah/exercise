package org.example.mathematics.practice;

public class ExponentLog_1 {

    static double pow(int a, double b) {
        double result = 1;
        boolean isMinus = false;
        if (b == 0) {
            return 1;
        } else if (b < 0) {
            b *= -1;
            isMinus = true;
        }

        for (int i = 0; i < b; i++) {
            result *= b;
        }

        return isMinus ? 1 / result : result;

    }

    // 바빌로니아
    static double sqrt(int a) {
        double result = 1;

        for (int i = 0; i < 10; i++) {
            result = (result + (a / result)) / 2;
        }

        return result;
    }


    public static void main(String[] args) {

    }
}
