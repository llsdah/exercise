package org.example.linear.list.practice.array;

/**
 * 배열 중복값을 제거한 새배력을 만드시오
 * 입력 1,5,3,2,2,3,1,4,1,2,3,5
 * 출력 1,5,4,2,4
 */

public class Practice_6 {

    public static void main(String[] args) {
        int[] arr = {1,5,4,2,2,3,1,4,1,2,3,5};
        int[] arrResult = new int[arr.length];
        int cnt = 0;

        for (int i = 0; i < arr.length; i++) {
            boolean dupFlag = false;
            for (int j = 0; j < cnt; j++) {
                if (arr[i] == arrResult[j]) {
                    dupFlag = true;
                }
            }

            if (dupFlag == false) {
                arrResult[cnt++] = arr[i];
            }

        }

        for (int i = 0; i < cnt; i++) {
            System.out.println("arrResult[i] = " + arrResult[i]);
        }
    }
}
