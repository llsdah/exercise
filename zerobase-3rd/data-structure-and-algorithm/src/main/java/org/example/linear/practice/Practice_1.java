package org.example.linear.practice;

import java.util.Arrays;

/**
 * 실제 md 파일 내용 참고
 * modifiation 된내용 복원
 *
 */
public class Practice_1 {




    public static int[] modification(int[] arr) {
        int[] arrNew = new int[arr.length];

        int idx = 0;
        int cnt = 0;
        int val = arr[idx];

        while (cnt < arr.length) {
            while (val == 0)     {
               idx = (idx + 1) % arr.length;
               val = arr[idx];
            }
            arrNew[cnt++] = val;
            arr[idx] = 0;
            idx = (val + idx) % arr.length;
            val = arr[idx];
        }
        return arrNew;
    }


    public static void main(String[] args) {
        int[] arr = {1,3,7,9,5};
        int[] arrNew = modification(arr);
        System.out.println("Arrays.toString(arrNew) = " + Arrays.toString(arrNew));

        arr = new int[]{1,3,5,7,9};
        arrNew = solution(arr);
        System.out.println("Arrays.toString(arrNew) = " + Arrays.toString(arrNew));


    }


    private static int[] solution(int[] arr) {
        int[] arrNew = new int[arr.length];
        int idx = 0;

        for (int i = 0; i < arr.length; i++) {
            for (int j = 0; j <arr.length; j++) {
                if (arrNew[idx] == 0){
                    break;
                }
                idx = (idx + 1) % arr.length;
            }
            arrNew[idx] = arr[i];
            idx = (idx + arr[i]) % arr.length;
        }

        return arrNew;
    }

}
