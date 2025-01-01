package org.example.nonlinear.binarytree.binarysearchtree.balancedbinarysearchtree;

import java.util.LinkedList;
import java.util.Queue;

public class MyRedBlackTree_1 {

    static class Node {
        int key;
        int color;
        Node left;
        Node right;
        Node parent;

        public Node(int key, int color, Node left, Node right, Node parent) {
            this.key = key;
            this.color = color;
            this.left = left;
            this.right = right;
            this.parent = parent;
        }
    }

    static class RedBlackTree {
        final int BLACK = 0;
        final int RED = 1;

        Node head;

        public void insert(int key) {
            Node checkNode = null;
            if (this.head == null) {
                this.head = new Node(key, BLACK, null, null, null);
            } else {
                Node cur = this.head;

                while (true) {
                    Node pre = cur;

                    if (key < cur.key) {
                        cur = cur.left;
                        if (cur == null) {
                            pre.left = new Node(key, RED,null, null, pre);
                            checkNode = pre.left; // reBalancing 확인
                            break;
                        }
                    } else {
                        cur = cur.right;
                        if (cur == null) {
                            pre.right = new Node(key, RED, null, null, pre);
                            checkNode = pre.right; // reBalancing 확인
                            break;
                        }
                    }
                }

                reBalance(checkNode);

            }
        }

        private void reBalance(Node node) {
            while (node.parent != null && node.parent.color == RED) { // 더블레드 상황
                Node sibling = null;

                // 형제 노드 찾기
                if (node.parent == node.parent.parent.left) {
                    sibling = node.parent.parent.right;
                } else {
                    sibling = node.parent.parent.left;
                }

                // 형제 노드가 붉은 색 ReColoring
                if (sibling != null && sibling.color == RED) {
                    node.parent.color = BLACK;
                    sibling.color = BLACK;
                    node.parent.parent.color = RED;

                    if (node.parent.parent == this.head) {
                        node.parent.parent.color = BLACK;
                        break;
                    } else {
                        node = node.parent.parent; // 재 ReBalancing 대상
                        continue;
                    }
                    
                } else { // reStructuring
                    if (node.parent == node.parent.parent.left) {
                        if (node == node.parent.right) { // LR 인경우 LL로
                            node = node.parent;
                            // 회전
                            leftRotate(node);
                        }

                        node.parent.color = BLACK;
                        node.parent.parent.color = RED;

                        rightRotate(node.parent.parent);

                    } else if (node.parent == node.parent.parent.right) {
                        if (node == node.parent.left) {
                            node = node.parent;
                            rightRotate(node);
                        }

                        node.parent.color = BLACK;
                        node.parent.parent.color = RED;

                        leftRotate(node.parent.parent);

                    }

                    break;

                }
            }
        }

        public void leftRotate(Node node) {
            if (node.parent == null) {

                Node rNode = this.head.right;
                this.head.right = rNode.left;
                rNode.left.parent = this.head;
                this.head.parent = rNode;
                rNode.left = this.head;
                rNode.parent = null;
                this.head = rNode;

            } else {
                // 자식이 있는 경우
                if (node == node.parent.left) {
                    node.parent.left = node.right;
                } else {
                    node.parent.right = node.right;
                }

                node.right.parent = node.parent;
                node.parent = node.right;

                if (node.right.left != null) {
                    node.right.left.parent = node;
                }
                node.right = node.right.left;
                node.parent.left = node;

            }
        }

        public void rightRotate(Node node) {
            if (node.parent == null) {

                Node lNode = this.head.left;
                this.head.left = lNode.right;
                lNode.right.parent = this.head;
                this.head.parent = lNode;
                lNode.right = this.head;
                lNode.parent = null;
                this.head = lNode;

            } else {
                if (node == node.parent.left) {
                    node.parent.left = node.left;
                } else {
                    node.parent.right = node.left;
                }

                node.left.parent = node.parent;
                node.parent = node.left;

                if (node.left.right != null) {
                    node.left.right.parent = node;
                }

                node.left = node.left.right;
                node.parent.right = node;

            }
        }

        public void levelOrder(Node node) {
            char[] color = {'B','R'};

            Queue<Node> que = new LinkedList<>();
            que.add(node);

            while (!que.isEmpty()) {
                Node cur = que.poll();

                System.out.print("["+  color[cur.color] + "]" + cur.key + " ");
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

        RedBlackTree rb = new RedBlackTree();

        rb.insert(20);
        rb.insert(10);
        rb.insert(30);
        rb.levelOrder(rb.head);
        rb.insert(25);
        rb.levelOrder(rb.head);

        rb.insert(5);
        rb.insert(7);
        rb.levelOrder(rb.head);

        rb.insert(20);
        rb.levelOrder(rb.head);

    }


}













