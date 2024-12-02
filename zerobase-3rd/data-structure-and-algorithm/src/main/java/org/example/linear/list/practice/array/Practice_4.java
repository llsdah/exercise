package org.example.linear.list.practice.array;

/**
 * 배역의 peek 값 모두를 출력
 * peek 좌우보다 큰값
 * 입력 3, 1,2,6,2,2,5,1,9,10,1,11
 * 출력 3,6,5,10,11
 */
public class Practice_4 {
    public static void main(String[] args) {
        int[] arr = {3,1,2,6,2,2,5,1,9,10,1,11};

        for (int i = 0; i < arr.length; i++) {
            if (i == 0 && arr[i] > arr[i+1]) {
                System.out.println(arr[i] + " ");
            } else if (i == arr.length-1 && arr[i] > arr[i-1]){
                System.out.println(arr[i] + " ");
            } else {
                if (arr[i] >arr[i+1] && arr[i] > arr[i-1]){
                    System.out.println(arr[i] + " ");
                }
            }
        }
    }
}
