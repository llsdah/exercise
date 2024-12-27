package org.example.nonlinear.binarytree;

import java.util.LinkedList;
import java.util.Queue;

public class MyTree_2 {

    public static void main(String[] args) {
        char[] arr = new char[10];
        for (int i = 0; i < arr.length; i++) {
            arr[i] = (char)('A'+i);
        }

        BinaryTree bt = new BinaryTree(arr);
        System.out.println("=== Preorder start ===");
        bt.preOrder(bt.head);

        System.out.println();
        System.out.println("=== Preorder end ===");

        System.out.println();

        System.out.println("=== inorder start ===");
        bt.inOrder(bt.head);

        System.out.println();
        System.out.println("=== inorder end ===");

        System.out.println();

        System.out.println("=== postorder start ===");

        bt.postOrder(bt.head);
        System.out.println();
        System.out.println("=== postorder end ===");

        System.out.println("=== levelorder start ===");

        bt.levelOrder(bt.head);
        System.out.println();
        System.out.println("=== levelorder end ===");

    }

    static class Node {
        char data;
        Node left;
        Node right;

        public Node(char data, Node left, Node right) {
            this.data = data;
            this.left = left;
            this.right = right;
        }
    }

    static class BinaryTree {
        Node head;

        public BinaryTree(char[] arr) {
            Node[] nodes = new Node[arr.length];
            for (int i = 0; i < arr.length; i++) {
                nodes[i] = new Node(arr[i],null,null);
            }

            for (int i = 0; i < arr.length; i++ ) {
                int left = 2 * i + 1;
                int right = 2 * i + 2;

                if (left < arr.length) {
                    nodes[i].left = nodes[left];
                }

                if (right < arr.length) {
                    nodes[i].right = nodes[right];
                }

            }
            this.head = nodes[0];
        }

        public void preOrder(Node node) {
            if (node == null) {
                return;
            }

            System.out.print(node.data + " ");
            this.preOrder(node.left);
            this.preOrder(node.right);
        }


        public void inOrder(Node node) {
            if ( node == null ) {
                return;
            }
            this.inOrder(node.left);
            System.out.print(node.data + " ");
            this.inOrder(node.right);

        }

        public void postOrder(Node node) {
            if (node == null) {
                return;
            }

            this.postOrder(node.left);
            this.postOrder(node.right);
            System.out.print(node.data + " ");

        }

        // que 구조와 비슷하다.
        public void levelOrder(Node node) {
            Queue<Node> que = new LinkedList<>();
            que.add(node);

            while (!que.isEmpty()) {
                Node cur = que.poll();

                System.out.print(cur.data + " ");
                if (cur.left != null) {
                    que.offer(cur.left);
                }

                if (cur.right != null) {
                    que.offer(cur.right);
                }
            }
        }

    }
}
