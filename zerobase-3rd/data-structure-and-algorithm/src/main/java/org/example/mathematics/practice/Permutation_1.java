package org.example.mathematics.practice;

public class Permutation_1 {

    private void permutation(int[] arr, int depth, int n, int r){

        if (depth == r) {
            for (int i = 0; i < r; i++) {
                System.out.print(arr[i] + " ");
            }
            System.out.println();
            return;
        }

        for (int i = depth; i < n; i++) {
            swap(arr, depth, i);
            permutation(arr, depth+1, n, r);
            swap(arr, depth, i); // 원복
        }

    }

    // 자리 수 변경 코드
    // idx 어디랑 위치를 바꿀지
    private void swap(int[] arr, int depth, int idx) {

        int temp = arr[depth];
        arr[depth] = arr[idx];
        arr[idx] = temp;
    }

    public static void main(String[] args) {

        int[] arr = {1, 2, 3, 4};

        Permutation_1 p = new Permutation_1();
        // depth 자리수 , n 총 갯수 , r 몇개 조함
        p.permutation(arr, 0, 4, 3);
    }
}
