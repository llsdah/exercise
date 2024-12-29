package org.example.nonlinear.binarytree.binarysearchtree;

import javax.imageio.plugins.jpeg.JPEGImageReadParam;
import java.util.LinkedList;
import java.util.Queue;

public class MyBinarySearchTree_1 {

    public static void main(String[] args) {
        BinarySearchTree bst = new BinarySearchTree(20);
        bst.addNode(10);
        bst.addNode(30);
        bst.addNode(1);
        bst.addNode(15);
        bst.addNode(25);
        bst.addNode(13);
        bst.addNode(35);
        bst.addNode(27);
        bst.addNode(40);
        bst.levelOrder(bst.head);

        // 노드 삭제
        bst.removeNode(40);
        bst.levelOrder(bst.head);
        bst.removeNode(25);
        bst.levelOrder(bst.head);
        bst.removeNode(20);
        bst.levelOrder(bst.head);
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

        BinarySearchTree(int key) {
            this.head = new Node(key, null, null);
        }

        public void addNode(int key) {
            if (this.head == null) {
                this.head = new Node(key,null,null);
            } else {
                Node cur = this.head;
                while (true) {
                    Node pre = cur; // 현재값을 쫒아 가는 것

                    if (key < cur.key) { // 추가하려고하는 key, 현재 key
                        cur = cur.left; // 넣으려고하는게 작으니 왼쪽이동
                        if (cur == null) {
                            pre.left = new Node(key, null, null);
                            break;
                        }
                    } else {
                        cur = cur.right;
                        if (cur == null) {
                            pre.right = new Node(key,null,null);
                            break;
                        }
                    }
                }
            }
        }

        public void removeNode(int key) {
            Node parent = null;
            Node successor = null; // 변경해야될 후계자 
            Node predecessor = null; // 후계자의 부모
            Node child = null; // 후계자의 자식

            Node cur = this.head;
            while (cur != null) { // 삭제할 노드 찾기 
                if (key == cur.key) {
                    break;
                }

                parent = cur;
                if (key < cur.key) {
                    cur = cur.left;
                } else {
                    cur = cur.right;
                }
            }
            
            if (cur == null) { // 삭제할 노드가 없다.
                System.out.println("key node is not exist");
                return;
            }

            if (cur.left == null && cur.right == null) { // Leaf 노드 인경우 
                if (parent == null) { // 트리의 노드가 1개 밖에 없는경우 
                    this.head = null; // 그 노드 삭제 
                } else {
                    if (parent.left == cur) { 
                        parent.left = null;
                    } else {
                        parent.right = null;
                    }
                }
            } else if (cur.left != null && cur.right == null || cur.left == null && cur.right != null) { // 자식노드가 1개인 경우
                if (cur.left != null) { // 왼쪽에 자식노드가 있는 경우
                    child = cur.left;
                } else {
                    child = cur.right;
                }

                if (parent == null) { // 루트노드에 자식이 하나만 있는 경우
                    this.head = child;
                } else {
                    if (parent.left == cur) {
                        parent.left = child;
                    } else {
                        parent.right = child;
                    }
                }
            } else { // 자식 노드가 둘인 경우
                predecessor = cur;
                successor = cur.left;

                while (successor.right != null) {
                    predecessor = successor;
                    successor = successor.right;
                }

                predecessor.right = successor.left;
                successor.left = cur.left;
                successor.right = cur.right;

                if (parent == null) {
                    this.head = successor;
                } else {
                    if (parent.left == cur) {
                        parent.left = successor;
                    } else {
                        parent.right = successor;
                    }
                }
            }
        }

        public void levelOrder (Node node) {
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
}





