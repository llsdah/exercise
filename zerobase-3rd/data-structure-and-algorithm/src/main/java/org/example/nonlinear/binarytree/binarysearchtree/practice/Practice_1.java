package org.example.nonlinear.binarytree.binarysearchtree.practice;

import java.util.ArrayList;

/**
 * 주어진 이진 탐색 트렝서 N번쨰로 작은 수 구하기
 *
 * 입력 트리 : 3,1,4,null,2
 * N:1, 결과:1
 *
 * 입력트리: 5,3,6,2,4,null,1
 * N:3, 결과:3
 *
 */
public class Practice_1 {

    // 좌우 비교하는 방식이 아닌 오늘차순 나열이 하고 구함.
    public static void solution(Integer[] data, int n) {
        BinarySearchTree bst = new BinarySearchTree(data[0]);

        for (int i = 1; i < data.length; i++) {
            if (data[i] == null) {
                continue;
            }
            bst.addNode(data[i]);
        }

        ArrayList<Integer> list = new ArrayList<>();
        inOrder(bst.head, list);
        System.out.println("result : "+list.get(n-1));
    }
    public static void inOrder(Node node, ArrayList list){
        if (node == null) {
            return;
        }

        inOrder(node.left,list);
        list.add(node.key);
        inOrder(node.right,list);

    }

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

        BinarySearchTree() {};

        public BinarySearchTree(int key) {
            this.head = new Node(key,null,null);
        }

        public void addNode(int key){
            this.head = this.addNode(this.head,key);
        }

        public Node addNode(Node cur, int key) {
            if (cur == null) {
                return new Node(key, null,null);
            }

            if (key < cur.key) {
                cur.left = addNode(cur.left, key);
            } else {
                cur.right = addNode(cur.right,key);
            }

            return cur;
        }
    }

    public static void main(String[] args) {

        Integer[] data = {3,1,4,null,2};
        int n = 1;
        solution(data, n);

        data = new Integer[]{5,3,6,2,4,null,null,1};
        n = 3;
        solution(data, n);

    }

}













