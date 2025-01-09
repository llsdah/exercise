package org.example.nonlinear.priorityqueue.practice;

import java.util.*;
import java.util.PriorityQueue;

/**
 * nums 배열에 주어진 정수들 중에서 가장 많이 발생한 숫자들 순으로 k번쨰 까지 출력하시오
 * 빈도가 같은 경우 값이 작은 숫자가 먼저 출력대로 하시오
 *
 *
 * 입력 1,1,1,2,2,3,
 * k = 2
 * 출력 1,2
 *
 * 입력 3,1,5,5,3,3,1,2,2,1,3
 * k = 3
 * 출력 3,1,2
 *
 */

public class Practice_3 {

    public static void main(String[] args) {
        int[] nums = {1,1,1,2,2,3};
        solution1(nums,2);
        solution2(nums,2);

        System.out.println();
        nums = new int[]{3,1,5,5,3,3,1,2,2,1,3};
        solution1(nums,3);
        solution2(nums,3);

    }

    private static void solution1(int[] nums, int k) {
        HashMap<Integer,Integer> map = new HashMap<>();

        for (int num : nums) {
            map.put(num, map.getOrDefault(num,0)+1);
        }

        PriorityQueue<Map.Entry<Integer,Integer>> pq =
                new PriorityQueue<>( (x, y) -> y.getValue() == x.getValue() ?
                    x.getKey() - y.getKey() : y.getValue() - x.getValue()
                );

        for (Map.Entry<Integer,Integer> item : map.entrySet()) {
            pq.add(item);
        }

        for (int i = 0; i < k; i++) {
            Map.Entry<Integer,Integer> cur = pq.poll();
            System.out.print(cur.getKey() + " ");
        }
        System.out.println();
    }

    private static void solution2(int[] nums, int k) {
        HashMap<Integer, Integer> map = new HashMap<>();

        for (int num : nums) {
            map.put(num, map.getOrDefault(num,0) + 1);
        }

        PriorityQueue<Num> pq = new PriorityQueue<>();
        for (Map.Entry<Integer, Integer> item : map.entrySet()) {
            pq.add(new Num(item.getKey(), item.getValue()));
        }

        for (int j = 0; j < k; j++) {
            Num cur = pq.poll();
            System.out.print(cur.key + " ");
        }

        System.out.println();

    }

    static class Num implements Comparable<Num> {
        int key;
        int freq;

        public Num(int key, int freq) {
            this.key = key;
            this.freq = freq;
        }

        @Override
        public int compareTo(Num o) {
            if (this.freq == o.freq) {
                return this.key - o.key;
            } else {
                return o.freq - this.freq;
            }
        }

    }


}
