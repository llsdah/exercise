package org.example.nonlinear.binarytree.practice;

/**
 * 각 에지에 가중치가 있는 포화이진트리가 있다.
 * 루트에서 각 리프까지의 경로 길이를 모두 같게 설정하고,
 * 이때, 모든 가중치들의 총합이 최소가 되도록하는 프로그램을 작성하시오
 *
 */
public class Practice_2 {

    public static void main(String[] args) {
        int h = 2; // 트리의 높이
        int[] w = {2,2,2,1,1,3}; // 각 엣지의 가중치 입니다.
        solution(h,w);
        System.out.println();

        h = 3;
        w = new int[]{1,2,1,3,2,4,1,1,1,1,1,1,1,1};
        solution(h,w);
    }

    static class BinaryTree{
        int h;
        int[] arr;
        int res;

        public BinaryTree(int h, int[] w) {
            this.h = h;

            arr = new int[(int)Math.pow(2, h + 1)];
            res = 0;

            for (int i = 2; i < (int)Math.pow(2,h+1); i++) {
                arr[i] = w[i-2];
            }


        }

        public int dfs(int idx, int h) {
            if (this.h == h) {
                res += arr[idx];
                return arr[idx];
            }

            int left = dfs(idx * 2, h + 1);
            int right = dfs(idx * 2 + 1 ,h + 1);
            res += arr[idx] + Math.abs(left - right);
            return arr[idx] + Math.max(left, right);
        }

    }


    private static void solution(int h, int[] w) {
        BinaryTree bt = new BinaryTree(h,w);
        bt.dfs(1,0);
        System.out.println("bt.res : "+bt.res);
    }



}







