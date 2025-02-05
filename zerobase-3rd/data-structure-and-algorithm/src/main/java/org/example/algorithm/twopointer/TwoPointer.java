package org.example.algorithm.twopointer;


import java.util.Arrays;

public class TwoPointer {
    /**
     * 투포인터 알고리즘
     *
     * 투 포인터 = 어떤 값을 가르키는 두개의 변수
     *
     * 두개의 포인터 사용해 원하는 결과를 얻는 방법
     * 같은 방향 시작 : 첫번쨰 원소에 둘다 배치
     * 다른방향 시작 : 첫번째 원소와 마지막 원소에 배치
     *
     * 예시) 배열의 부분합의 9가 되는 구간 찾기
     *
     */

    public static void main(String[] args) {
        int[] arr = {1,2,5,3,7,2,4,3,2};
        System.out.println("answer : "+ Arrays.toString(forLoop(arr,9)));
        System.out.println("answer : "+ Arrays.toString(forLoop(arr,14)));
        System.out.println();

        System.out.println("answer : "+ Arrays.toString(twoPointers(arr,9)));
        System.out.println("answer : "+ Arrays.toString(twoPointers(arr,14)));

    }

    private static int[] twoPointers(int[] arr, int target) {

        int p1 = 0;
        int p2 = 0;
        int sum = 0;
        int[] result = {-1,-1};
        while (true) {
            if (sum > target) {
                sum -= arr[p1++];
            } else if (p2 == arr.length) {
                break;
            } else {
                sum += arr[p2++];
            }

            if (sum == target) {
                result[0] = p1;
                result[1] = p2 - 1;
                break;
            }

        }

        return result;
    }

    private static int[] forLoop(int[] arr, int target) {
        int[] result = {-1,-1};

        for (int i = 0; i < arr.length; i++) {
            int sum = arr[i]; // 부분합
            for (int j = i+1; j < arr.length; j++) {
                if (sum == target) {
                    result[0] = i;
                    result[1] = j-1;
                    break;
                }
                sum += arr[j];
            }

            if (sum == target) {
                break;
            }
        }

        return result;
    }


}
