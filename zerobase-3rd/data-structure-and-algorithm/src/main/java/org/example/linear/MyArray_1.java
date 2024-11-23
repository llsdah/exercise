package org.example.linear;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * 배열
 * 많은 수의 데이터를 다룰때 사용 - 연속적으로 사용
 * 인덱스로 데이터에 빠르게 접근
 * 길이 고정 - 최대 길이를 설정해야됨
 *
 */

public class MyArray_1 {

    public static void main(String[] args) {

        int[] arr = {1, 2, 3, 4, 5};
        for (int item : arr) {
            System.out.println("item = " + item);
        }

        arr[1] = 100;
        System.out.println("Arrays.toString(arr) = " + Arrays.toString(arr));
        //               0 번째 1번째 2
        int[][] arr2 = {{1,2,3}, {4,5,6}};
        System.out.println("arr2[0][1] = " + arr2[0][1]); // 2

        for (int[] item : arr2) {
            for (int item2 : item) {
                System.out.println("item2 = " + item2);
            }
        }

        System.out.println("=== Array ===");
        ArrayList list1 = new ArrayList(Arrays.asList(1,2,3));
        System.out.println("list1 = " + list1);
        list1.add(4);
        list1.add(5);
        list1.add(6);

        System.out.println("list1 = " + list1);
        list1.remove(2); // 기본적으로 인덱스

        list1.remove(Integer.valueOf(2));// 값을 통해 제거
        // 배열 두개를 더하면 이차원 배열이 됩니다.


    }


}
