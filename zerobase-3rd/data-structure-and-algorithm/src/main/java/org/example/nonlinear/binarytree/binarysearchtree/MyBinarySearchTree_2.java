package org.example.nonlinear.binarytree.binarysearchtree;

import java.util.LinkedList;
import java.util.Queue;

public class MyBinarySearchTree_2 {


    static class Node {
        int key;
        Node left;
        Node right;

        public Node(int key, Node left, Node right) {
            this.key = key;
            this.left = left;
            this.right = right;
        }
    }

    static class BinarySearchTree {
        Node head;

        BinarySearchTree(int key) {
            this.head = new Node(key, null,null);
        }

        public Node addNodeRecursive(Node cur, int key) {
            if (cur == null) {
                return new Node(key,null,null);
            }

            if (key < cur.key) {
                cur.left = addNodeRecursive(cur.left, key);
            } else {
                cur.right = addNodeRecursive(cur.right,key);
            }

            return cur;
        }

        public Node removeNodeRecursive(Node cur, int key) {

            if (cur == null) {
                return null;
            }

            if (key < cur.key) {
                cur.left = removeNodeRecursive(cur.left, key);
            } else if (key > cur.key) {
                cur.right = removeNodeRecursive(cur.right, key);
            } else {

                if (cur.left == null) {
                    return cur.right;
                } else if (cur.right == null) {
                    return cur.left;
                } else {

                    Node predecessor = cur;
                    Node successor = cur.left;

                    while (successor.right != null) {
                        predecessor = successor;
                        successor = successor.right;
                    }

                    predecessor.right = successor.left;
                    cur.key = successor.key;
                }
            }

            return cur;
        }

        public void levelOrder(Node node){
            Queue<Node> que = new LinkedList<>();
            que.add(node);
            while (!que.isEmpty()) {
                Node cur = que.poll();

                System.out.print(cur.key + " ");
                if (cur.left != null) {
                    que.offer(cur.left);
                }

                if (cur.right != null) {
                    que.offer(cur.right);
                }
            }
            System.out.println();

        }

    }


    public static void main(String[] args) {
        BinarySearchTree bst = new BinarySearchTree(20);

        bst.head = bst.addNodeRecursive(bst.head,10);
        bst.head = bst.addNodeRecursive(bst.head,30);
        bst.head = bst.addNodeRecursive(bst.head,1);
        bst.head = bst.addNodeRecursive(bst.head,15);
        bst.head = bst.addNodeRecursive(bst.head,25);
        bst.head = bst.addNodeRecursive(bst.head,13);
        bst.head = bst.addNodeRecursive(bst.head,35);
        bst.head = bst.addNodeRecursive(bst.head,27);
        bst.head = bst.addNodeRecursive(bst.head,40);
        bst.levelOrder(bst.head);


        bst.head = bst.removeNodeRecursive(bst.head,40);
        bst.levelOrder(bst.head);
        bst.head = bst.removeNodeRecursive(bst.head,25);
        bst.levelOrder(bst.head);
        bst.head = bst.removeNodeRecursive(bst.head,20);
        bst.levelOrder(bst.head);

    }

}
