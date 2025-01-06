package org.example.nonlinear.heap;

import java.util.ArrayList;

public class MyMaxHeap_1 {

    public static void main(String[] args) {
        MyMaxHeap heap = new MyMaxHeap();
        System.out.println("data insert=====================");
        heap.insert(30);
        heap.insert(40);
        heap.insert(10);
        heap.printTree();
        heap.insert(50);
        heap.insert(60);
        heap.insert(70);
        heap.printTree();
        heap.insert(20);
        heap.printTree();
        heap.insert(30);
        heap.printTree();

        System.out.println();
        System.out.println("data delete ============");
        System.out.println("delete : "+ heap.delete());
        heap.printTree();
        System.out.println("delete : "+ heap.delete());
        heap.printTree();
        System.out.println("delete : "+ heap.delete());
        heap.printTree();

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

            return target; // 최초 조회 값
        }

        public void printTree() {
            for (int i = 1; i < this.heap.size(); i++) {
                System.out.print(this.heap.get(i) + " ");
            }
            System.out.println();
        }
    }

}
