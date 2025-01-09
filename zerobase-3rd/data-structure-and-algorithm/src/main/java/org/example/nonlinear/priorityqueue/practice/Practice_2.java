package org.example.nonlinear.priorityqueue.practice;

import java.util.PriorityQueue;

/**
 * 도의 무게 데이터로 이루어진 정수형 stones 배열이 주어졌을때 다음의 내용에 따라 프로그램 작성
 * 해당 배열로부터 가장 무게가 많이 나가는 돌 두개를 꺼내세요
 * 두개의 돌을 충돌시키는데, 무게가 같으면 둘다 소멸
 * 무게가 다르면 남은 무게 만큼의 돌을 다시 추가합니다.
 * 이 과정을 반복하며 가장 마지막 돌의 무게를 구하시오
 *
 * 입력 2,7,4,1,8,1
 * 출력 1
 *
 * 입력 5,3,5,3,4
 * 출력 2
 *
 */
public class Practice_2 {

    public static void main(String[] args) {
        int[] stones = {2,7,4,1,8,1};
        solution(stones);

        stones = new int[]{5,3,5,3,4};
        solution(stones);

    }

    private static void solution(int[] stones) {
        // 큰수 대로 정렬
        PriorityQueue<Integer> pq = new PriorityQueue<>((x, y) -> y - x);

        for (int stone : stones) {
            pq.offer(stone);
        }

        while (pq.size() > 1) {
            int stone1 = pq.poll();
            int stone2 = pq.poll();
            int diff = Math.abs(stone1 - stone2);

            if (diff != 0)  {
                pq.offer(diff);
            }

        }

        System.out.println(pq.poll());
    }

}
