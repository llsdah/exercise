package org.example.nonlinear.practice;

import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.PrimitiveIterator;
import java.util.PriorityQueue;

/**
 * n 명의 친구가 참석하는 파티
 * 먼저 도착한 친구 순으로 비어있는 의자중 작은 숫자의자에 앉는다.
 *
 * times 친구별 {도착시간, 떠나는 시간}, 배열 수 = 친구 수, 배열번호 = 친구번호
 * targetFriend: 확인 친구
 *
 *
 *
 * {{1,4},{2,3},{4,6}}
 * targetFriend 1
 * {{3,10},{1,5},{2,6}}
 * targetFriend 0
 *
 *
 */

public class Practice_6 {

    public static void main(String[] args) {
        int[][] time = {{1,4},{2,3},{4,6}};
        int targetFriend = 1;

        int result = solution(time, targetFriend);

        System.out.println("result = " + result);
        time = new int[][]{{3,10},{1,5},{2,6}};
        targetFriend = 0;
        result = solution(time, targetFriend);


        System.out.println("result = " + result);

    }

    private static int solution(int[][] time, int targetFriend) {
        if (time == null || time.length < 1 || time[0].length <1) {
            return -1;
        }

        // 정렬전 타켓 저장
        int targetArrival = time[targetFriend][0];

        // 도착시간 순서대로 정렬
        Arrays.sort(time, (x, y) -> x[0] -y[0]);

        // 반납한의자 최상단 도착
        PriorityQueue<Integer> pqChair = new PriorityQueue<>();
        for (int i = 0; i < time.length; i++) {
            pqChair.offer(i);
        }

        // 떠나는시간 , 의자
        PriorityQueue<int[]> pqFriend = new PriorityQueue<>( (x, y) -> x[0]-y[0]);

        for (int i = 0; i < time.length ; i++) {
            // 가장 먼저떠나는 시간이 현 시간보다 작으면 뺴로
            while (!pqFriend.isEmpty() && pqFriend.peek()[0]  <= time[i][0]) {
                pqChair.offer(pqFriend.poll()[1]);
            }

            if (time[i][0] == targetArrival) {
                break;
            }

            // 떠나는 시간 의차
            pqFriend.offer(new int[]{time[i][1], pqChair.poll()});

        }

        return pqChair.peek();
    }
}












