package org.example.algorithm.pracitce1;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * 치과병원 n명 환가 대기 중
 * K명의 의가 있고 의사마다 진료하는 소요 시간이 다름
 * k명 의사의 걸리는 시간 times
 *
 * 대기하는 환자들은 순서대로 진료가 먼저끝나는 의사에게 가서 진료받는다.
 *
 * 모든환자가 진료받는데 걸리는 최소한의 시간을 구하는 프로그램
 *
 * n = 6
 * time = {7,10}
 *
 *
 */
public class Practice_1 {

    public static void main(String[] args) {
        int n = 6;
        int[] times = {7,10};

        solution(n ,times);
    }

    private static void solution(int n, int[] times) {


        Arrays.sort(times);

        int left = 0;
        int right = times[times.length-1] *n;// 최대 진료 시간

        while(left <= right) {
            int mid = (left + right) / 2; // 시간

            int cnt = 0;
            for (int i = 0; i < times.length; i++) {
                cnt += mid / times[i]; // 의사가 몇명 진료가능한지.
            }

            if (cnt < n) { // 의사 수보다 작으면,
                left = mid + 1;
            } else {
                right = mid - 1;
            }
        }


        System.out.println("result : " + left);
    }

}
