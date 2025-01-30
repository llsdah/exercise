package org.example.algorithm.sort;

import java.util.ArrayList;
import java.util.*;

public class RedixSort {


    private static void redixSort(int[] arr) {
        ArrayList<Queue<Integer>> list = new ArrayList<>();

        for (int i = 0; i < 10; i++) {
            // 0 - 9까지의 기수 테이블이 필요핟
            list.add(new LinkedList<>());
        }
        // 
        int idx = 0;
        int div = 1;
        int maxLen = getMaxLen(arr);

        for (int i = 0; i < maxLen; i++) {

            for (int j = 0; j < arr.length; j++) {
                // 해다 자리수에 넣기
                list.get((arr[j] / div) % 10 ).offer(arr[j]);
            }

            for (int j = 0; j < 10; j++) {
                Queue<Integer> que = list.get(j);
                // 자리수별 정리 완료
                while (!que.isEmpty()) {
                    arr[idx++] = que.poll();
                }
            }

            idx = 0; // 인덱스 초기화
            div *= 10; //
        }

    }
    // 최대 자리 수 구하는 것
    private static int getMaxLen(int[] arr) {
        int maxLen = 0;
        for (int i = 0; i < arr.length; i++) {
            int len = (int)Math.log10(arr[i]) + 1; // 해당 "자리수" 구하기
            if (maxLen < len) {
                maxLen = len;
            }
        }
        return maxLen;
    }

    public static void main(String[] args) {

        int[] arr = {10, 32, 52, 27, 48, 17, 99, 56};
        int[] temp = new int[arr.length];
        System.out.println("기존 정렬 : "+ Arrays.toString(arr));
        redixSort(arr);
        System.out.println("기수 정렬 : "+ Arrays.toString(arr));

    }

}
