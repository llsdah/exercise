package org.example.mathematics;

/**
 * 조합
 * 서로 다른 n 중에서 r개를 선택하는 경우의 수
 *
 * 중복조합
 * 서로 다른 n 중에서 r개를 선택하는 경우의 수 중복
 *
 *
 */
public class Combination1 {

    static int getCombination(int n, int r){
        int pResult = 1;

        for (int i = n; i >= n-r+1; i--) {
            pResult *= i;
        }

        int rResult = 1;
        for (int i = 1; i <= r; i++) {
            rResult *= i;
        }
        return pResult/rResult;
    }

    public static void main(String[] args) {

        // 조합
        int n = 4;
        int r = 2;
        int pResult = 1;

        for (int i = n; i >= n-r+1; i--) {
            pResult *= i;
        }

        int rResult = 1;
        for (int i = 1; i <= r; i++) {
            rResult *= i;
        }
        System.out.println("pResult/rResult = " + pResult/rResult);

        // 중복 조합 - 후보 명 , 유권자 3명

        n = 2;
        r = 3;

        System.out.println("result : " + getCombination(n+r-1,r));

    }


}
