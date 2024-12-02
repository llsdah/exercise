package org.example.linear.list.practice.array;

/**
 * 배열 오름차순 출력
 * 입력 5,4,1,4,6,1,
 * 출력 1,1,3,4,5,6
 */
public class Practice_5 {

    public static void main(String[] args) {
        int[] arr = {5,3,1,4,6,1};
        int[] visited = new int[arr.length];
        int visitCnt = 0;
        int minVal = Integer.MAX_VALUE;
        int minIndex = -1;

        while (visitCnt < arr.length) {
            for (int i = 0; i < arr.length; i++) {
                if (arr[i] < minVal && visited[i] == 0) {
                    minVal = arr[i];
                    minIndex = i;
                }
            }

            if (minVal != -1) {
                System.out.print(minVal + " ");
                visited[minIndex] = -1;
                visitCnt ++;
            }
            minVal = Integer.MAX_VALUE;
            minIndex = -1;
        }
        System.out.println();
    }
}
