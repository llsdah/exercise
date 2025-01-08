package org.example.nonlinear.priorityqueue;

import java.time.chrono.MinguoDate;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.PriorityQueue;

public class MyPriorityQueue_1 {

    public static void main(String[] args) {
        System.out.println("== 연결 리스트 방식 우선순위 큐");
        LinkedList<Integer> pqList = new LinkedList<>();
        enqueue(pqList,5);
        enqueue(pqList,7);
        enqueue(pqList,3);
        enqueue(pqList,1);
        enqueue(pqList,9);

        System.out.println(pqList);

        System.out.println(dequeue(pqList));
        System.out.println(dequeue(pqList));

        System.out.println();
        System.out.println(pqList);

        System.out.println("java priorityqueue 사용");

        PriorityQueue<Integer> que = new PriorityQueue<>();
        que.add(9);
        que.add(3);
        que.add(1);
        que.add(5);
        que.add(7);
        System.out.println(que);

        System.out.println("역정렬");

        PriorityQueue<Integer> que2 = new PriorityQueue<Integer>(Collections.reverseOrder());

        que2.add(9);
        que2.add(5);
        que2.add(1);
        que2.add(3);
        que2.add(7);

        // to String 방식으로 구현한다면 정렬이 안된것처럼 보인다
        System.out.println(que2);

        System.out.println("poll 이용 ");
        while (!que2.isEmpty()) {
            System.out.print(que2.poll() + " ");
        }
        System.out.println();



    }

    private static Integer dequeue(LinkedList<Integer> pqList) {

        if (pqList.size() == 0) {
            return null;
        }

        int data = pqList.get(0);
        pqList.remove(0);
        return data;
    }

    private static void enqueue(LinkedList<Integer> pqList, int data) {

        int idx = pqList.size();

        for (int j = 0; j < pqList.size(); j++) {
            if (pqList.get(j) > data) {
                idx = j;
                break;
            }
        }
        pqList.add(idx,data);

    }



}
