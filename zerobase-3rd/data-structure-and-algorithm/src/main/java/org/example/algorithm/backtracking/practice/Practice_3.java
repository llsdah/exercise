package org.example.algorithm.backtracking.practice;

/**
 * sols 배열에 5지 선다 문제 정답이 있다.
 * 3번 연속 같은 정답이 있는 경우가 없다.
 * 문제를 찍어서 푸는 경우 5점이상 받는 경우의 수
 * 10문제 각 문제당 1점
 *
 *
 * input : {1,2,3,4,5,1,2,3,4,5}
 * output : 261622
 *
 */
public class Practice_3 {

    final static int numOfProblems = 10; // 문제 수
    static int cnt;

    private static void solution(int[] sols) {
        if (sols == null || sols.length != numOfProblems) {
            return;
        }

        cnt = 0;
        int[] submit = new int[numOfProblems];

        // backtracking // 5점 이상 이하 확인
        backTracking(sols,submit,0,0);
        System.out.println("cnt : "+cnt);



    }

    public  static void backTracking(int[] sols, int[] submit, int correctCnt, int index) {
        if (numOfProblems - index + correctCnt < 5) {
            return; // 더이상안됨
        }

        if (index == numOfProblems) {
            if (correctCnt >= 5) {
                cnt+=1;
            }
        } else {
            int towInRow = 0; // 두문제 이상 같은 번호
            if (index >= 2) {
                if (submit[index -1] == submit[index - 2]) {
                    towInRow = submit[index - 1];
                }
            }

            for (int i = 1; i <= 5; i++) {
                if (i == towInRow) {
                    continue;
                }

                submit[index] = i;

                if (sols[index] == i) {
                    backTracking(sols,submit, correctCnt+1,index+1);
                } else {
                    backTracking(sols,submit,correctCnt,index+1);
                }
                submit[index] = 0;
            }
        }
    }



    public static void main(String[] args) {
        int[] sols = {1,2,3,4,5,1,2,3,4,5};
        solution(sols);
        sols = new int[] {1,1,2,2,3,3,4,4,5,5};
        solution(sols);
    }

}
