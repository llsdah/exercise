package org.example.nonlinear.practice;

import java.util.Arrays;
import java.util.PriorityQueue;

/**
 * 정수형 2차원
 * intervals 구간의 {시작값 종료값}
 * queries 구간내 포함되어 있는지 확인하기 위한 질문 목록
 * 포함하는 구간의 최소길이를 반환
 *
 * 최소 길이  = 끝 - 시작 + 1
 * 
 * 쿼리가 포함될 수 있는 구간 중 최소 구잔을 반환하는 프록그램
 * 만족 없으면 -1
 * 종료 - 시작 + 1
 *
 */
public class Practice_7 {


    public static void main(String[] args) {

        int[][] intervals = {{1,4},{2,4},{3,6},{4,4}};
        int [] queries = {2,3,4,5};

        int[] result = solution(intervals,queries);
        System.out.println("Arrays.toString(result) = " + Arrays.toString(result));

        intervals = new int[][]{{2,3},{2,5},{1,8},{20,25}};
        queries = new int[]{2,19,5,22};

        result = solution(intervals,queries);
        System.out.println("Arrays.toString(result) = " + Arrays.toString(result));

    }

    private static int[] solution(int[][] intervals, int[] queries) {
        int[] result = null;
        if (intervals == null || queries == null) {
            return null;
        }

        if (intervals[0].length == 0 || queries.length == 0) {
            return result;
        }

        // 간격 오름차순 정렬
        PriorityQueue<int[]> minHeap = new PriorityQueue<>((x,y)-> (x[1]- x[0]) - (y[1] - y[0]) );

        Arrays.sort(intervals, (x,y) -> (x[0] - y[0]));

        int[][] queriesBak = new int[queries.length][2];

        for (int i = 0; i < queries.length; i++) {
            queriesBak[i] = new int[]{queries[i],i};
        }

        Arrays.sort(queriesBak, (x,y) -> (x[0] - y[0]));

        result = new int[queries.length];
        
        int j = 0;
        
        for (int i = 0; i < queries.length; i++) {
            int queryVal = queriesBak[i][0];
            int queryIndex = queriesBak[i][1];

            while (j < intervals.length && intervals[j][0] <= queryVal) {
                minHeap.add(intervals[j]);
                j++;
            }

            while (!minHeap.isEmpty() && minHeap.peek()[1] < queryVal) {
                minHeap.remove();
            }

            result[queryIndex] = minHeap.isEmpty() ? -1 : minHeap.peek()[1] - minHeap.peek()[0] + 1;

        }

        return result;
    }

}
