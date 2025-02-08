package org.example.algorithm.greedy.practice;

import java.util.Arrays;

/**
 * 배열의 숫자중 두자리를 최대 한번만 교체해서 얻을 수 있는 최대값으로 출력하세요
 *
 * input : 2736
 * output : 7236
 *
 * input : 7116
 * output : 7611
 *
 */
public class Practice_5 {

    public static int solution(int num) {
        char[] cArr = String.valueOf(num).toCharArray();
        int[] maxArr = new int[cArr.length];

        int max = 0;
        // 뒤에서 부터 찾아서 최대 값 기록
        for (int i = cArr.length - 1; i >= 0; i--) {
            max = Math.max(max, cArr[i] - '0');
            maxArr[i] = max;
        }
        System.out.println("maxArr : "+ Arrays.toString(maxArr));
        for (int i = 0; i < cArr.length - 1; i++) {
                System.out.println("i = "+ i + ", max arr : "+ maxArr[i+1]+", cArr : "+cArr[i]);
            if (cArr[i] - '0' < maxArr[i + 1]) {
                for (int j = cArr.length - 1; j >= i + 1 ; j--) {
                    if (cArr[j] - '0' == maxArr[i+1]) {
                        char temp = cArr[j];
                        cArr[j] = cArr[i];
                        cArr[i] = temp;
                        return Integer.parseInt(String.valueOf(cArr));
                    }
                }
            }
        }

        return 0;
    }

    public static void main(String[] args) {
        int num = 2736;
        System.out.println("num : "+num+", "+solution(num));
        num = 7116;
        System.out.println("num : "+num+", "+solution(num));

        num = 7116116;
        System.out.println("num : "+num+", "+solution(num));
    }


}
