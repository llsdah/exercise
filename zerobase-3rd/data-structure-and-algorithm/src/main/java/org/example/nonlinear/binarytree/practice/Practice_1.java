package org.example.nonlinear.binarytree.practice;

/**
 * 종이 접기 - 상상 해보기
 * 종이를 반으로 접었을때 안으로 파인부분은 0, 볼록 튀어나온 부분은 1
 * 종이를 접을 때는 오른쪽에서 왼쪽으로 접는다.
 * 종이를 N번 접었을 때의 접힌 상태를 출력하는 문제를 작성하시오.
 *
 *  1-> 0
 *
 *  2 -> 0 0 1
 *
 *  3 -> 0 0 1 0 0 1 1
 *
 *  4 -> 0 0 1 0 0 1 1 0 1 1 0 1 1 0 0
 */
public class Practice_1 {

    public static void main(String[] args) {
        solution(1);
        solution(2);
        solution(3);
        solution(4);
    }

    private static void solution(int n) {
        int[] binaryTree = new int[(int)Math.pow(2,n)];

        // 한번은 무조건 0
        binaryTree[0] = 0;

        // 2이 들어올떈 좌우만 해주면됩니다.
        for (int i = 0; i < (int)Math.pow(2,n-1)-1; i++) {
            binaryTree[i * 2 + 1] = 0;
            binaryTree[i * 2 + 2] = 1;
        }

        inOrder(binaryTree, 0);
        System.out.println();

    }

    private static void inOrder(int[] arr, int idx) {

        int left = 2 * idx + 1;
        int right = 2 * idx + 2;

        if (left < arr.length - 1) {
            inOrder(arr, left);
        }

        System.out.print(arr[idx]+" ");

        if (right < arr.length - 1) {
            inOrder(arr, right);
        }
    }


}
