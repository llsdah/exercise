package org.example.algorithm.sort.practice;

import java.util.*;

/**
 * interval 이라는 구간으로 이루어진 배열 오버랩 되는 구간을 합치는 프로그램
 * 단 합친구간은 오름차순 정렬
 *
 * 입력
 * {2,6},{1,3},{15,18},{8,10}
 * 출력
 * {1,6},{8,10},{15,18}
 *
 */
public class Practice_3 {

    public static void main(String[] args) {
        int[][] intervals =  {{2,6}, {1,3}, {15,18}, {8,10}};

        for (int[] item : solution(intervals)) {
            System.out.print(Arrays.toString(item) + " ");
        }

        System.out.println();
    }

    private static ArrayList<int[]> solution(int[][] intervals) {

        if (intervals == null || intervals.length < 2 ) {
            return new ArrayList<>();
        }

        sort(intervals);

        ArrayList<int[]> result = new ArrayList<>();
        int[] curInterval = intervals[0];

        result.add(curInterval);

        for (int i = 0; i < intervals.length; i++) {
            if (curInterval[1] >= intervals[i][0]) {
                curInterval[1] = intervals[i][1];
            } else {
                curInterval = intervals[i];
                result.add(curInterval);
            }
        }

        return result;
    }

    public static void sort(int[][] intervals) {
        quickSort(intervals, 0, intervals.length - 1);
    }

    public static void quickSort(int[][] arr, int left, int right) {
        if (left >= right) {
            return;
        }

        int pivot = partition(arr,left,right);

        quickSort(arr, left, pivot-1);
        quickSort(arr,pivot+1, right);

    }

    private static int partition(int[][] arr, int left, int right) {
        int pivot = arr[left][0];
        int i = left;
        int j = right;

        while (i < j) {
            while (arr[j][0] > pivot && i < j) {
                j--;
            }

            while (arr[i][0] <= pivot && i < j) {
                i++;
            }

            swap(arr, i, j);
        }

        swap(arr, left, j);

        return i;

    }

    private static void swap(int[][] arr, int i, int j) {
        int[] temp = arr[i];
        arr[i] = arr[j];
        arr[j] = temp;
    }

}
