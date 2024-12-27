package org.example.nonlinear.binarytree;

public class MyTree_1 {

    static class BinaryTree{
        char[] arr;

        BinaryTree(char[] data) {
            this.arr = data.clone();
        }

        public void preOrder(int idx) {
            // 처음 데이터 출력
            System.out.print(this.arr[idx] + " ");

            int left = 2 * idx + 1;
            int right = 2 * idx + 2;

            if (left < this.arr.length) {
                this.preOrder(left);
            }

            if (right < this.arr.length) {
                this.preOrder(right);
            }

        }

        public void inOrder(int idx) {
            int left = 2 * idx + 1;
            int right = 2 * idx + 2;

            if (left < this.arr.length) {
                this.inOrder(left);
            }

            System.out.print(this.arr[idx] + " ");

            if (right < this.arr.length) {
                this.inOrder(right);
            }

        }



        public void postOrder(int idx){
            int left = 2 * idx + 1;
            int right = 2 * idx + 2;


            if (left < this.arr.length) {
                this.postOrder(left);
            }

            if (right < this.arr.length) {
                this.postOrder(right);
            }

            System.out.print(this.arr[idx] + " ");

        }


    }

    public static void main(String[] args) {
        char[] arr = new char[10];
        for (int i = 0; i < arr.length; i++) {
            arr[i] = (char)('A'+i);
        }

        BinaryTree bt = new BinaryTree(arr);
        System.out.println("=== Preorder start ===");
        bt.preOrder(0);

        System.out.println();
        System.out.println("=== Preorder end ===");

        System.out.println();

        System.out.println("=== inorder start ===");
        bt.inOrder(0);

        System.out.println();
        System.out.println("=== inorder end ===");

        System.out.println();

        System.out.println("=== postorder start ===");

        bt.postOrder(0);
        System.out.println();
        System.out.println("=== postorder end ===");

    }
}
