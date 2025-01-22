package org.example.nonlinear.practice;

import java.util.PriorityQueue;

/**
 * n 정수의 target
 *
 * 초기배열로 target을 만들 수 있는지
 *
 * n = 3, {9,3,5}
 * 우선 초기배열 = {1,1,1}
 * 초기 배열의 모든 원소의 합을 구한 후 임의의 위치를 해당 값으로 교체
 * {1,3,1} => {1,3,5} => {9,3,5}; 가능 true
 *
 *
 *
 *
 */

public class Practice_8 {

    public static void main(String[] args) {

        int[] target = {9,3,5};
        System.out.println(solution(target));

        target = new int[]{8,3};
        System.out.println(solution(target));

        target = new int[]{1,1,1,2};
        System.out.println(solution(target));

    }

    // 역방향 진행하며, 진행합니다.
    // 최대 합 - 최대요소 값  = x
    // 최대요소 값 > x 면, 최대요소 값 - x  =  ? 의 값을 최대요소 값과 변경
    // 아니라면 진행 필요가 없다.
    private static boolean solution(int[] target) {

        if (target == null || target.length == 0) {
            return false;
        }

        PriorityQueue<Integer> pq = new PriorityQueue<>((x,y)-> y-x);

        int sum = 0;
        for (int num : target) {
            sum += num;
            pq.add(num);
        }

        // 1과 다르면 진행, 아니면 완료
        while (pq.peek() != 1) {
            int num = pq.poll();
            sum -= num;

            if (num <= sum || sum < 1) {
                return false;
            }

            num -= sum;
            sum += num;
            pq.add(num > 0 ? num : sum);

        }

        return true;
    }


}
