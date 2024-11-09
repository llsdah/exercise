package org.example.mathematics.practice;

import java.util.*;

/**
 * 약수 구하기, 두수의 최대공약수 와 최소공배수 구하기
 * -> 향후 재귀함수 영역에서 더 나아갈 수 있습니다.
 *
 */
public class NumberOfCases_Practice {

    // 약수
    public ArrayList getDivisor(int num) {
        ArrayList result = new ArrayList();

        // 절반만
        for (int i = 1; i<=(int)num/2; i++) {
            if (num % i == 0) {
                result.add(i);
            }
        }
        result.add(num);
        return result;
    }

    // 최대공약수
    // GCD the greatest Common Denominator
    public int getGCD(int numA, int numB) {
        int gcd = -1;

        ArrayList divisorA = this.getDivisor(numA);
        ArrayList divisorB = this.getDivisor(numB);

        for (int itemA : (ArrayList<Integer>) divisorA){
            for (int itemB : (ArrayList<Integer>) divisorB) {
                if (itemA == itemB) {
                    if (gcd < itemA) {
                        gcd = itemA;
                    }
                }
            }
        }

        return gcd;
    }

    // 최소공배수
    // LCM the lowest common multiple
    public int getLCM(int numA, int numB) {
        int lcm = -1;

        int gcd = this.getGCD(numA,numB);

        // 없는 경우
        if (gcd != -1) {
            lcm = numA * numB / gcd;
        }

        return lcm;
    }

    public static void main(String[] args) {

        int numA = 10;
        int numB = 6;

        NumberOfCases_Practice p = new NumberOfCases_Practice();
        ArrayList l1 = p.getDivisor(numA);
        ArrayList l2 = p.getDivisor(numB);
        System.out.println("l1 = " + l1);
        System.out.println("l2 = " + l2);

        int gcd = p.getGCD(numA,numB);
        System.out.println("gcd = " + gcd);

        int lcm = p.getLCM(numA,numB);
        System.out.println("lcm = " + lcm);


    }

}
