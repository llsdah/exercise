package org.example.mathematics.practice;

import java.util.*;
/**
 * 1차 방정식 풀이
 * 해가 없다면 No solution
 * 해가 무한하면 Infinite solution
 */
public class Practice_8 {
    private static String solution1 (String equation) {
        String[] parts = equation.split("=");
        int[] leftSide = evaluate(parts[0]);
        int[] rightSide = evaluate(parts[1]);

        System.out.println("left = "+Arrays.toString(leftSide));
        System.out.println("right = "+Arrays.toString(rightSide));

        int[] leftSide2 = evaluate2(parts[0]);
        int[] rightSide2 = evaluate2(parts[1]);

        System.out.println("left2 = "+Arrays.toString(leftSide2));
        System.out.println("right2 = "+Arrays.toString(rightSide2));

        if (leftSide[0] == rightSide[0] && leftSide[1] == rightSide[1]){
            return "Infinite solution";
        } else if (leftSide[0] == rightSide[0]){
            return "no solution";
        } else {
            return "x="+ ((rightSide[1]-leftSide[1]) / (leftSide[0]- rightSide[0]));
        }

    }

    private static int[] evaluate(String part) {
        int[] result = new int[2]; // x 와 상수항 각각 계산
        boolean isMinus = false;
        int idx = 0;
        while ( idx != part.length()) {
            char c = part.charAt(idx++);

            if (c == '+') {
                isMinus = false;
                continue;
            }
            if (c == '-') {
                isMinus = true;
                continue;
            }
            if (c == 'x') {
                result[0] += isMinus ? -1 : 1;
            } else {
                if (idx < part.length() && part.charAt(idx) == 'x') {
                    result[0] += isMinus ? -(c - '0') : (c - '0');
                    idx++; // 추가본 x 인경우 index 늘려야함
                } else {
                    result[1] += isMinus ? -(c - '0') : (c - '0');
                }
            }
            isMinus = false;

        }

        return result;
    }


    private static int[] evaluate2(String part) {
        int[] result = new int[2];

        // 정규식을 사용한 풀이 있다면 좋다.
        for (String s : part.split("(?=[+-])")) {
            if (s.equals("+x") || s.equals("x")) {
                result[0]++;
            } else if (s.equals("-x")) {
                result[0]--;
            } else if (s.contains("x")) {
                // x의 상수항
                result[0] += Integer.parseInt(s.substring(0,s.length()-1));
            } else {
                result[1] += Integer.parseInt(s);
            }
        }

        return result;
    }


        public static void main(String[] args) {
        System.out.println(solution1("x+5-3+x=6+x-2"));
        System.out.println(solution1("x=x+1"));
        System.out.println(solution1("x=x"));
        System.out.println(solution1("2x=x"));
    }
}
