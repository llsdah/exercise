package org.example.nonlinear.binarytree;

import java.util.LinkedList;
import java.util.Queue;

public class MyTree_3 {

    static class Node {
        char data;
        Node left;
        Node right;
        Node parent;

        public Node(char data, Node left, Node right, Node parent) {
            this.data = data;
            this.left = left;
            this.right = right;
            this.parent = parent;
        }
    }

    static class BinaryTree{
        Node head;

        public BinaryTree(char[] arr) {
            Node[] nodes = new Node[arr.length];
            for (int i = 0; i < arr.length; i++) {
                nodes[i] = new Node(arr[i],null,null,null);
            }

            for (int i = 0; i < arr.length; i++) {
                int left = 2 * i + 1;
                int right = 2 * i + 2;

                if (left < arr.length) {
                    nodes[i].left = nodes[left];
                    nodes[left].parent = nodes[i];
                }

                if (right < arr.length) {
                    nodes[i].right = nodes[right];
                    nodes[right].parent = nodes[i];
                }
            }
            this.head = nodes[0];
        }

        public Node searchNode(char data) {
            Queue<Node> que = new LinkedList<>();
            que.add(this.head);
            while (!que.isEmpty()) {
                Node cur = que.poll();
                if (cur.data == data) {
                    return cur;
                }
                if (cur.left != null) {
                    que.offer(cur.left);
                }
                if (cur.right != null) {
                    que.offer(cur.right);
                }
            }
            return null;
        }

        // 들어온값을 포함한 하위 갯수
        public Integer checksize(char data) {
            Node node = this.searchNode(data);

            Queue<Node> que = new LinkedList<>();
            que.add(node);

            int size = 0;
            while (!que.isEmpty()) {
                Node cur = que.poll();

                if (cur.left != null) {
                    que.offer(cur.left);
                    size++;
                }

                if (cur.right != null) {
                    que.offer(cur.right);
                    size++;
                }
            }
            return size + 1;
        }



    }


    public static void main(String[] args) {
        char[] arr = new char[10];
        for (int i = 0; i < arr.length; i++) {
            arr[i] = (char)('A'+i);
        }

        BinaryTree bt = new BinaryTree(arr);

        System.out.println("bt.head.data = " + bt.head.data);

        Node B = bt.searchNode('B');
        if (B.left != null) {
            System.out.println("b -> left child  "+ B.left.data);
        } else if (B.right != null ) {
            System.out.println("b -> right child  "+ B.right.data);
        }

        Node F = bt.searchNode('F');
        System.out.println("F.parent.data = " + F.parent.data);

        System.out.println("leaf node");
        for (int i = 0; i < arr.length; i++) {
            Node cur = bt.searchNode(arr[i]);

            if (cur.left == null && cur.right == null) {
                System.out.print(cur.data + " ");
            }

        }

        System.out.println();
        System.out.println();

        Node E = bt.searchNode('E');
        Node cur = E;
        int cnt = 0;
        while (cur.parent != null) {
            cur = cur.parent;
            cnt ++;
        }
        System.out.println(" E depth : " + cnt );


        System.out.println("level 2 node ");
        for (int i = 0; i < arr.length; i++) {
            Node target = bt.searchNode((char)('A'+i));
            cur = target;
            cnt = 0;
            while (cur.parent != null) {
                cur = cur.parent;
                cnt ++;
            }
            if (cnt == 2) {
                System.out.print(target.data + " ");
            }

        }
        System.out.println();

        // Height
        int maxCnt = Integer.MIN_VALUE;
        for (int i = 0; i < arr.length; i++) {
            Node target = bt.searchNode((char)('A'+i));
            cur = target;
            cnt = 0;
            while (cur.parent != null) {
                cur = cur.parent;
                cnt++;
            }

            if (maxCnt < cnt) {
                maxCnt = cnt;
            }
        }
        System.out.println("Height : "+ maxCnt);

        
        // B size
        int size = bt.checksize('B');
        System.out.println("size = " + size);
        
    }

}















