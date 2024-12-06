package org.example.linear.queue.practice;

import java.util.LinkedList;
import java.util.Queue;
import java.util.stream.IntStream;

/**
 * 카드섞기
 * 1부터 N까지 카드
 * 1이 위 N이 가장 아래의 상태로 카드가 순서대로 쌓여 있다 .
 * 아래의 동작을 카드 한장만 남을때까지 빈복 가장 만지막 카드는
 * 1. 가장 위이의 카드는 버린다.
 * 2. 그 다음 위의 카드는 쌓여있는 카드 가장아래에 넣는다.
 *
 * n=4,
 * 결과 4
 *
 * n=7
 * 결과 6
 *
 */
public class Practice_1 {

    public static int findLastCard(int N) {
        Queue que = new LinkedList();

        IntStream.range(1, N+1).forEach(x -> que.add(x));
        System.out.println();

        while (que.size() > 1) {
            que.remove();
            int data = (int) que.remove();
            que.add(data);
            System.out.println(que);
        }


        return (int) que.remove();
    }

    public static void main(String[] args) {
        System.out.println(findLastCard(4));
        System.out.println(findLastCard(6));
        System.out.println(findLastCard(7));
        System.out.println(findLastCard(8));
        System.out.println(findLastCard(9));
    }

}
