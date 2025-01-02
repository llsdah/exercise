package org.example.nonlinear.binarytree.binarysearchtree.practice;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Queue;

/**
 * 인접한 노드간의 차이값 중 최소 값을 구하세요 
 * 
 * 입력트리 : 4,2,6,1,3
 * 출력 : 1
 * 
 * 입력트리 : 5,1,48,null,null,12,51
 * 출력 : 3
 * 
 * 
 */
public class Practice_2 {

    public static void main(String[] args) {
        Integer[] data = {4,2,6,1,3};
        solution(data);
        
        data = new Integer[]{5,1,48,null,null,12,51};
        solution(data);

    }

    private static void solution(Integer[] data) {
        BinarySearchTree bst = new BinarySearchTree();
        for (int i = 1; i < data.length; i++) {
            if (data[i] == null) {
                continue;
            }
            bst.addNode(data[i]);
        }

        ArrayList<Integer> list = new ArrayList<>();
        levelOrder(bst.head, list);

        int min = list.stream().min((x1,x2) -> x1 > x2 ? 1 : -1 ).get();

        System.out.println("min = " + min);
    }

    private static void levelOrder(Node node, ArrayList list) {
        Queue<Node> que = new LinkedList<>();
        que.add(node);

        while (!que.isEmpty()) {
            Node cur = que.poll();

            if (cur.left != null) {
                que.offer(cur.left);
                list.add(Math.abs(cur.key - cur.left.key));
            }

            if (cur.right != null) {

                que.offer(cur.right);
                list.add(Math.abs(cur.key - cur.right.key));
            }

        }
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
    
}
