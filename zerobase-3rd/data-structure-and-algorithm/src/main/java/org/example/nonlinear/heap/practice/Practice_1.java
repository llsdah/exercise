package org.example.nonlinear.heap.practice;

import java.util.ArrayList;

/**
 * 최소힙에서 특정값을 변경하는 코드 작성
 * 특정 값이 여러개라면 모두 바꿔주세요.
 *
 * 특정값은 -> 다른 특정값으로 변경 -> 재배열 필요
 *
 */

public class Practice_1 {

    public static void main(String[] args) {
        MyHeap heap = new MyHeap();
        heap.insert(30);
        heap.insert(40);
        heap.insert(10);
        heap.insert(50);
        heap.insert(60);
        heap.insert(70);
        heap.insert(20);
        heap.insert(30);
        System.out.println("data 전 =============");
        heap.printTree();
        System.out.println("data 후 =============");
        solution(heap, 30, 100);
        heap.printTree();

        System.out.println("data 후 =============");
        solution(heap, 60, 1);
        heap.printTree();
    }

    private static void solution(MyHeap myHeap, int from, int to) {
        for (int i = 0; i < myHeap.heap.size(); i++) {
            if (myHeap.heap.get(i) == from) {
                myHeap.heap.set(i, to);

                moveUp(myHeap, i);
                moveDown(myHeap, i);
            }
        }
    }

    // 부모와 비교해 현재 값이 작으면 올리는 작업
    public static void moveUp(MyHeap myHeap,int idx) {
        int cur = idx;

        while (cur > 1 && myHeap.heap.get(cur / 2) > myHeap.heap.get(cur)) {
            int parentVal = myHeap.heap.get(cur / 2);
            myHeap.heap.set(cur / 2, myHeap.heap.get(cur));
            myHeap.heap.set(cur, parentVal);

            cur /= 2;
        }
    }

    // 현재 값이 크면 내려주는 작업
    public static void moveDown(MyHeap myHeap, int idx) {
        int cur = idx;
        while (true) {
            int leftIdx = cur * 2;
            int rightIdx = cur * 2 + 1;
            int targetIdx = -1;

            if (rightIdx < myHeap.heap.size()) {
                targetIdx = myHeap.heap.get(leftIdx) < myHeap.heap.get(rightIdx) ? leftIdx : rightIdx;
            } else if (leftIdx < myHeap.heap.size()) {
                targetIdx = leftIdx;
            } else {
                break;
            }

            if (myHeap.heap.get(cur) < myHeap.heap.get(targetIdx)) {
                break;
            } else {
                int parentVal = myHeap.heap.get(cur);
                myHeap.heap.set(cur, myHeap.heap.get(targetIdx));
                myHeap.heap.set(targetIdx, parentVal);
                cur = targetIdx;
            }
        }
    }

    static class MyHeap {
        ArrayList<Integer> heap;

        public MyHeap() {
            this.heap = new ArrayList<>();
            heap.add(0);
        }

        public void insert(int data) {
            this.heap.add(data);
            int cur = heap.size() - 1;

            while (cur > 1 && heap.get(cur / 2) > heap.get(cur)) {
                int parentValue = heap.get(cur / 2);
                heap.set(cur / 2, data);
                heap.set(cur, parentValue);

                cur /= 2;
            }
        }

        public Integer delete() {
            if (heap.size() == 1) {
                System.out.println("is empty");
                return null;
            }
            int lastValue = heap.get(this.heap.size()-1);

            heap.set(1, lastValue);
            heap.remove(this.heap.size()-1);

            int cur = 1;
            while (true) {
                int leftIdx = cur * 2;
                int rightIdx = cur * 2 + 1;
                int targetIdx = -1;

                if (rightIdx < heap.size()) {
                    targetIdx = heap.get(leftIdx) < heap.get(rightIdx) ? leftIdx : rightIdx;
                } else if (leftIdx < heap.size()) {
                    targetIdx = cur * 2;
                } else {
                    break;
                }

                if (heap.get(cur) < heap.get(targetIdx)) {
                    break;
                } else {
                    int parentVal = heap.get(cur);
                    heap.set(cur, heap.get(targetIdx));
                    heap.set(targetIdx, heap.get(cur));
                    cur = targetIdx;
                }

            }

            return lastValue;
        }


        public void printTree() {
            for (int i = 1; i < this.heap.size(); i++) {
                System.out.print(this.heap.get(i) + " ");
            }
            System.out.println();
        }
    }


}
