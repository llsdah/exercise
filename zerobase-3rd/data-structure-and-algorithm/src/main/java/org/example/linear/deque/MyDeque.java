package org.example.linear.deque;

import java.util.ArrayDeque;
import java.util.Deque;

public class MyDeque {
    /**
     * 데크 양방향 삽입 삭제가능구조
     *
     */

    public static void main(String[] args) {
        Deque deque = new ArrayDeque();

        deque.addFirst(1);
        deque.addFirst(2);
        deque.addFirst(3);
        System.out.println(deque);


        deque.addLast(10);
        deque.addLast(20);
        deque.addLast(30);
        System.out.println(deque);

        System.out.println(deque.removeFirst());
        System.out.println();
        System.out.println(deque.removeLast());
        System.out.println();
    }
}
