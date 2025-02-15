package org.example;
import java.util.*;
public class StandardAlgorithm {
    public static void main(String[] args) {

    }
    public static void twoArraySort(){
        int[][] arr = {{1, 2, 3}, {4, 5, 6}, {2, 3, 4}, {3, 4, 5}};

        // 첫 번째 열(인덱스 0) 기준으로 정렬 - 오름차순
        Arrays.sort(arr, (a, b) -> Integer.compare(a[0], b[0]));

        // 첫 번쨰 열(인덱스 0) 기준으로 정렬 - 내림차순
        Arrays.sort(arr, (a, b) -> Integer.compare(b[0], a[0]));

        // 결과 출력
        for (int[] row : arr) {
            System.out.println(Arrays.toString(row));
        }
    }
}
