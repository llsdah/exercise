package org.example.linear.queue;

import java.util.LinkedList;
import java.util.Queue;

/**
 * 큐
 * 선입선출
 * 프린터 출력 대기열 ,BFS
 */

public class MyQueue {

    public static void main(String[] args) {
        // 인터페이스
        Queue que = new LinkedList();

        que.add(1);
        que.add(2);
        que.add(3);
        que.add(4);

        que.peek();
        que.poll();

    }
}
