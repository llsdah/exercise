package org.example.linear.hashtable.practice;

import java.util.Hashtable;

/**
 * 해시테이블 활용한 수찾기
 * 주어진 첫번째 배열에서 해시데이블을 초기화 한후
 * 두번째 배열이 주어질때 해당 배열 내 데이터가 해시테이블에 있는지 확인하는 코드 작성
 * 1, 3, 5, 7, 9
 * 1, 2, 3, 4, 5
 * true, false, true, false, true
 *
 */

public class Practice_1 {

    public static void solution(int[] arr1, int[] arr2) {
        Hashtable<Integer,Integer> ht = new Hashtable<>();
        for (int i = 0; i < arr1.length; i++) {
            ht.put(arr1[i],arr1[i]);
        }
        for (int i = 0; i < arr2.length; i++) {
            if (ht.containsKey(arr2[i])){
                System.out.print(" true ");
            } else {
                System.out.print(" false ");
            }
        }
        System.out.println();

    }

    public static void main(String[] args) {
        int[] arr1 = {1,3,5,7,9};
        int[] arr2 = {1,2,3,4,5};
        solution(arr1,arr2);
    }
}
