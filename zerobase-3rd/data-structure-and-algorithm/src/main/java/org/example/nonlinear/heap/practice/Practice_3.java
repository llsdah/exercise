package org.example.nonlinear.heap.practice;

import java.util.ArrayList;
import java.util.stream.IntStream;

/**
 * 정수들을 힙 자료구조에 추가하고 n번 삭제 후 절대값이 큰값부터 출력하세요
 *
 * 입력 3, 0, -2, -5, 9, 6, -11, 20 , -30
 * 삭제 1
 * 출력 20, -11, 9, 6, -5, 3, -2, 0
 *
 *
 *
 */
public class Practice_3 {
    public static void main(String[] args) {
        int[] nums = {3, 0, -2, -5, 9, 6, -11, 20 , -30};
        int deleteCnt = 1;
        solution(nums, deleteCnt);
    }

    private static void solution(int[] nums, int deleteCnt) {
        MyMaxHeap heap = new MyMaxHeap();
        IntStream.of(nums).forEach(x -> heap.insert(x));

        int cnt = 0;
        while (heap.heap.size() != 1) {
            Num cur = heap.delete();

            if (cnt ++ < deleteCnt) {
                continue;
            }

            int originVal = cur.isMinus ? cur.val * -1 : cur.val;
            System.out.print(" " + originVal);
        }
        System.out.println();

    }

    static class Num {
        int val;
        boolean isMinus;

        public Num(int val) {
            this.val = Math.abs(val);
            this.isMinus = val < 0 ? true : false;
        }
    }

    static class MyMaxHeap {
        ArrayList<Num> heap;

        public MyMaxHeap() {
            this.heap = new ArrayList<>();
            this.heap.add(new Num(0));
        }

        public void insert(int data) {
            heap.add(new Num(data));

            int cur = heap.size() - 1;

            while (cur > 1 && heap.get(cur / 2).val < heap.get(cur).val) {
                Num parentVal = heap.get(cur / 2);
                heap.set(cur / 2, heap.get(cur));
                heap.set(cur, parentVal);

                cur /= 2;
            }
        }

        public Num delete() {
            if (heap.size() == 1) {
                System.out.println("Heap is empty");
                return null;
            }

            Num target = heap.get(1);

            heap.set(1, heap.get(heap.size() - 1));
            heap.remove(heap.size() - 1);
            int cur = 1;
            while (true) {
                int leftIdx = cur * 2;
                int rightIdx = cur * 2 + 1;
                int targetIdx = -1;

                if (rightIdx < heap.size()) {
                    targetIdx = heap.get(leftIdx).val > heap.get(rightIdx).val ? leftIdx : rightIdx;
                } else if (leftIdx < heap.size()) { // 자식  한개?
                    targetIdx = cur * 2;
                } else { // 자식이 없는 사황
                    break;
                }

                if (heap.get(cur).val > heap.get(targetIdx).val) {
                    break;
                } else {
                    Num parentVal = heap.get(cur);
                    heap.set(cur, heap.get(targetIdx));
                    heap.set(targetIdx, parentVal);
                    cur = targetIdx;
                }
            }

            return target;
        }
    }
}
