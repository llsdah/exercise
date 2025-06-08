package org.example.algorithm.practice2;

import java.util.Arrays;

/**
 * 출발지 0에서 목표위치에 사이에 큰 바위가 있다.
 * 바위 위치 {11,2,14,21,17} 에 있다
 * 목표 위치가 25면, 각 위치 ㅏ이의 간격은 {2,9,3,3,4,4} 이다
 *
 * 이때 임의로 2와 21 위치의 바위를 제거하면 아래와 같이 바뀌나
 * {11,3,3,8} 바뀐 간격 의 최소값을 3
 *
 * n개의 바위를 제거하는 경우 간격의 최소값 중에서 최대값을 구하는 방법
 *
 * rocks {11,2,14,21,17}
 * goal 25
 * n 2
 *
 * output 4
 *
 */
public class Practice_1 {


    private static void solution(int[] rocks, int goal, int n) {

        Arrays.sort(rocks);

        int left = 2;
        int right = goal;

        // 최소중 최대 찾기
        int result = Integer.MIN_VALUE;

        while ( left <= right) {

            int mid = (left + right) / 2;
            int cnt = 0; // 돌삭제 갯수
            int prev = 0; // 이전돌의 위치

            for (int i = 0 ; i < rocks.length; i++) {
                if (rocks[i] - prev < mid) { // 돌간격 확인
                    // 돌 하나 지움
                    cnt ++;
                } else {
                    //돌위치 이동
                    prev = rocks[i];
                }
                // 돌 다지움?
                if (cnt > n) {
                    break;
                }
            }
            // 최종위치도 확인
            if (goal - prev < mid && cnt <= n) {
                cnt ++;
            }

            if (cnt > n) {
                right = mid - 1;
            } else {
                left = mid + 1;

                if (cnt == n) {
                    result = Math.max(result,mid);
                }
            }

        }

        System.out.println("result = " + result);

    }

    // 이진 탐색으로
    public static void main(String[] args) {
        int[] rocks = {11,2,14,21,17};
        int goal = 25;
        int n = 2;
        solution(rocks,goal,n);
    }


}
