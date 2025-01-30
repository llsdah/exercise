package org.example.algorithm.sort;

import java.util.*;

public class CountingSort {


    private static void countingSort(int[] arr) {
        int max = Arrays.stream(arr).max().getAsInt();
        int[] cntArr = new int[max+1];
        // 카운트 테이블
        for (int i = 0; i < arr.length; i++) {
            cntArr[arr[i]]++;
        }

        int idx = 0;
        for (int i = 0; i < cntArr.length; i++) {
            while (cntArr[i] > 0) {
                arr[idx++] = i;
                cntArr[i] -= 1;
            }
        }

        // 값이 너무 클떄를 대비한 2번째 방법
        HashMap<Integer,Integer> map = new HashMap<Integer, Integer>();
        for (int item : arr) {
            map.put(item, map.getOrDefault(item, 0) + 1);
        }

        int idx2 = 0;
        ArrayList<Integer> list = new ArrayList<>();
        Collections.sort(list);

        for (int i = 0; i < list.size(); i++) {
            int cnt = map.get(list.get(i));
            while (cnt > 0) {
                arr[idx2++] = list.get(i);
                cnt--;
            }
        }

    }

    public static void main(String[] args) {

        int[] arr = {10, 32, 10, 52, 27, 48, 17, 99, 56};
        int[] temp = new int[arr.length];
        System.out.println("기존 정렬 : "+ Arrays.toString(arr));
        countingSort(arr);
        System.out.println("계수 정렬 : "+ Arrays.toString(arr));

    }
}
