package org.example.linear.queue.practice;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Queue;
import java.util.stream.IntStream;

/**
 * 요세푸스 문제
 * N과 K가 주어졌을때 N,K 요세푸스 순열을 구하시고
 * N K , N >= K 양의 정수
 *
 * 1 부터 N 까지 N 명이 순서대로 원을 이루어 모여 있다.
 * 이 모임에서 원을 따라 순서대로 K번쨰 사람을 제외한다.
 * 모든 사람이 제외될떄 까지 반복하며, 이떄 제외되는 순서가 요세풋 순열
 *
 */
public class Practice_2 {

    public static void main(String[] args) {
        System.out.println(getJosephusPermutation(5,2));
        System.out.println(getJosephusPermutation(7,3));
    }

    public static ArrayList getJosephusPermutation(int N, int K) {
        Queue que = new LinkedList();
        ArrayList result = new ArrayList();

        IntStream.range(1,N+1).forEach(x -> que.add(x));

        int cnt = 0;
        while (!que.isEmpty()) {
            int data = (int) que.remove();
            cnt += 1;
            if (cnt % K == 0) {
                result.add(data);
            } else {
                que.add(data);
            }
        }

        return result;
    }

}
