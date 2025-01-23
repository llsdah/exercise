package org.example.nonlinear.practice;

import java.util.Arrays;
import java.util.PriorityQueue;

/**
 * n 작업자수,
 * k 일할 수 있는 작업자 수
 * speed : n 명의 작업자가 각각의 작업 속도 정보
 * efficiency : n 명의 작업자가 각각의 작업 효율 정보
 *
 * k 명의 작업자를 선택할 떄 , 가장 작업 성능이 뛰어나게 구성하는 프로그램 작성
 * 작업 성능 = k명 작업자들의 작업속도 합에 그중 가장 효율의 낮은 값을 곱해서 구한다
 *
 *
 *
 */

public class Practice_9 {

    public static void main(String[] args) {
        int[] speed = {2,10,3,1,5,8}; //
        int[] efficiency = {5,4,3,9,7,2}; // 내림차순 정렬
        // 어짜피 낮은것을 곱하기 때문에 효율이 높은 것을 제외하면 반복


        System.out.println(solution(6,speed, efficiency,2)); // 60
        System.out.println(solution(6,speed, efficiency,3)); // 68
        System.out.println(solution(6,speed, efficiency,4)); // 72

    }

    private static int solution(int n, int[] speed, int[] efficiency, int k) {
        if (speed == null || efficiency == null || speed.length == 0 || efficiency.length == 0 ) {
            return -1;
        }

        if (speed.length != efficiency.length) {
            return -1;
        }

        int[][] engineers = new int[n][2];

        for (int i = 0; i < n; i++) {
            engineers[i] = new int[]{efficiency[i], speed[i]};
        }

        Arrays.sort(engineers, (x,y)-> y[0]-x[0]);

        // 정렬 완료
        PriorityQueue<Integer> pq = new PriorityQueue<>(k, (x,y) -> x - y);

        int sum = 0;
        int result = 0;

        for (int[] eg : engineers) {
            pq.offer(eg[1]);
            sum += eg[1];

            if (pq.size() > k) {
                sum -= pq.poll();
            }

            result = Math.max(result, (sum * eg[0]));

        }


        return result;

    }
}



