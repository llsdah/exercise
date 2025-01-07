package org.example.nonlinear.heap.practice;

import java.util.ArrayList;

/**
 * 최소힙 최대힙을 이용해서 데이터를 오름차순 내림차순으로 정렬해 주세요
 */
public class Practice_2 {
    public static void main(String[] args) {

        MyMinHeap minHeap = new MyMinHeap();
        minHeap.insert(30);
        minHeap.insert(40);
        minHeap.insert(10);
        minHeap.insert(50);
        minHeap.insert(60);
        minHeap.insert(70);
        minHeap.insert(20);
        minHeap.insert(30);

        solution(minHeap);

    }

    private static void solution(MyMinHeap minHeap) {
        MyMaxHeap maxHeap = new MyMaxHeap();
        System.out.print("오른차순 : ");

        while (minHeap.heap.size() != 1) {
            int data = minHeap.delete(); // 삭제 할때 이미 데이터 작은것 정렬합니다.
            System.out.print(data + " ");
            maxHeap.insert(data);
        }

        System.out.println();
        System.out.println();

        System.out.print("내림차순 : ");
        while (maxHeap.heap.size() != 1) {
            System.out.print(maxHeap.delete() + " ");
        }
        System.out.println();


    }


    static class MyMinHeap {
        ArrayList<Integer> heap;

        public MyMinHeap() {
            this.heap = new ArrayList<>();
            this.heap.add(0);
        }

        public void insert(int data) {
            heap.add(data); // 데이터 입력

            int cur = heap.size() - 1;

            // 부모데이터 조회 후 자리 변경
            // cur / 2 부모 값
            while (cur > 1 && heap.get(cur / 2) > heap.get(cur) ) {
                int parentVale = heap.get(cur / 2);
                heap.set(cur / 2, data);
                heap.set(cur, parentVale);

                cur /= 2;
            }
        }

        // 최상단 노드 삭제
        public Integer delete() {
            if (heap.size() == 1) { // 더미데이터 0 만 있는 경우
                System.out.println("Heap is empty!");
                return null;
            }

            int target = heap.get(1); // 더미 제외 기준 최상단

            heap.set(1,heap.get(heap.size() - 1)); // 마지막 데이터 위치 변경
            heap.remove(heap.size() - 1); // 마지막 데이터 삭제

            int cur = 1;
            while (true) {
                int leftIdx = cur * 2;
                int rightIdx = cur * 2 + 1;
                int targetIdx = -1;

                if (rightIdx < heap.size()) {
                    targetIdx = heap.get(leftIdx) < heap.get(rightIdx) ? leftIdx : rightIdx;
                } else if (leftIdx < heap.size()) { // 자식  한개?
                    targetIdx = cur * 2;
                } else { // 자식이 없는 사황
                    break;
                }

                if (heap.get(cur) < heap.get(targetIdx)) {
                    break;
                } else {
                    int parentVal = heap.get(cur);
                    heap.set(cur, heap.get(targetIdx));
                    heap.set(targetIdx, parentVal);
                    cur = targetIdx;
                }
            }

            return target; // 최초 조회 값
        }

        public void printTree() {
            for (int i = 1; i < this.heap.size(); i++) {
                System.out.print(this.heap.get(i) + " ");
            }
            System.out.println();
        }

    }

    static class MyMaxHeap {
        ArrayList<Integer> heap;

        public MyMaxHeap() {
            this.heap = new ArrayList<>();
            this.heap.add(0);
        }

        public void insert(int data) {
            heap.add(data);

            int cur = heap.size() - 1;

            while (cur > 1 && heap.get(cur / 2) < heap.get(cur)) {
                int parentVal = heap.get(cur / 2);
                heap.set(cur / 2 , data);
                heap.set(cur, parentVal);

                cur /= 2;
            }
        }

        public Integer delete() {
            if (heap.size() == 1) {
                System.out.println("Heap is empty");
                return null;
            }

            int target = heap.get(1);

            heap.set(1, heap.get(heap.size() - 1));
            heap.remove(heap.size() - 1);
            int cur = 1;
            while (true) {
                int leftIdx = cur * 2;
                int rightIdx = cur * 2 + 1;
                int targetIdx = -1;

                if (rightIdx < heap.size()) {
                    targetIdx = heap.get(leftIdx) > heap.get(rightIdx) ? leftIdx : rightIdx;
                } else if (leftIdx < heap.size()) { // 자식  한개?
                    targetIdx = cur * 2;
                } else { // 자식이 없는 사황
                    break;
                }

                if (heap.get(cur) > heap.get(targetIdx)) {
                    break;
                } else {
                    int parentVal = heap.get(cur);
                    heap.set(cur, heap.get(targetIdx));
                    heap.set(targetIdx, parentVal);
                    cur = targetIdx;
                }
            }

            return target;
        }


    }

}
