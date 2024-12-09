package org.example.linear.deque.pratice;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.stream.IntStream;

/**
 *
 * 1 2 3 4 5 ... n 순서를
 * 1 n 2 n-1 .... 순서로 하기
 *
 * 데이터 재설정
 * 입력 데이터 : 1 -> 2 -> 3 -> 4 -> 5
 * 출력 데이터 : 1 -> 5 -> 2 -> 4 -> 3
 *
 */
public class Practice_1 {

    public static void reorderData(int[] arr) {
        Deque deque = new ArrayDeque();
        ArrayList result = new ArrayList();

        IntStream.of(arr).forEach(x -> deque.addLast(x));
        System.out.println("deque = " + deque);

        while (!deque.isEmpty()) {
            result.add(deque.removeFirst());

            if (!deque.isEmpty()) {
                result.add(deque.removeLast());
            }

        }

        System.out.println("하하");
        printData(arr);

        System.out.println("하하");
        printData(result.stream().mapToInt(x -> (int)x).toArray());

    }

    private static void printData(int[] arr) {
        for (int i = 0; i < arr.length; i++){
            System.out.print(arr[i] + " -> ");
        }
        System.out.println();
    }

    public static void main(String[] args) {
        int[] arr = { 1, 2, 3, 4, 5};
        reorderData(arr);

        int[] arr2 = {1,2,3,4,5,6,7};
        reorderData(arr2);
    }
}
