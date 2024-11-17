package org.example.mathematics.practice;

import java.util.*;
/**
 * 파스칼의 삼각형
 * 삼각형의 행 수가 주어졌을떄 출력하기
 */
public class Practice_1 {


    public static ArrayList<ArrayList<Integer>> solution(int numRows) {
        ArrayList<ArrayList<Integer>> result = new ArrayList<>();

        for (int i = 0; i < numRows; i++) {
            ArrayList<Integer> list = new ArrayList<>();

            for (int j = 0; j < i+1; j++) {
                if (j == 0 || j == i) { // 가장 밖의 수 입니다.
                    list.add(1);
                } else {
                  int x = result.get(i-1).get(j-1);
                  int y = result.get(i-1).get(j);

                  list.add(x+y);
                }
            }
            result.add(list);
        }

        return result;
     }

    public static void main(String[] args) {
        System.out.println(solution(1));
        System.out.println(solution(2));
        System.out.println(solution(3));
        System.out.println(solution(4));
        System.out.println(solution(5));
    }

}
