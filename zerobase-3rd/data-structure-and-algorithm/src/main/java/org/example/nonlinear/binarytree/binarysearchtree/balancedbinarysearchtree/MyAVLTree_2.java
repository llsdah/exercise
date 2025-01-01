package org.example.nonlinear.binarytree.binarysearchtree.balancedbinarysearchtree;

public class MyAVLTree_2 {
    static class AVLTree2 extends MyAVLTree_1.AVLTree {

        public void delete(int key) {
            this.head = delete(this.head, key);

        }

        public MyAVLTree_1.Node delete(MyAVLTree_1.Node node, int key) {

            if (node == null) {
                return null;
            }

            if (key < node.key) {
                node.left = delete(node.left, key);
            } else if (key > node.key){
                node.right = delete(node.right, key);
            } else {
                if (node.left == null) {
                    return node.right;
                } else if (node.right == null) {
                    return node.left;
                } else {

                    // 좌측에서 가장 큰 것을 찾을 것임.
                    MyAVLTree_1.Node predecessor = node;
                    MyAVLTree_1.Node successor = node.left;

                    while (successor.right != null) {
                        predecessor = successor;
                        successor = successor.right;
                    }
                    predecessor.right = successor.left;
                    node.key = successor.key;
                }
            }

            node.height = Math.max(height(node.left), height(node.right)) + 1;

            int balance = getBalance(node);

            // LL
            if (balance > 1 && getBalance(node.left) > 0) {
                return rightRotate(node);
            }

            // RR
            if (balance < -1 && getBalance(node.right) < 0) {
                return leftRotate(node);
            }

            // LR
            if (balance > 1 && getBalance(node.left) < 0) {
                return lrRotate(node);
            }

            // RL
            if (balance > -1 && getBalance(node.right) > 0) {
                return rlRotate(node);
            }

            return node;
        }
    }

    public static void main(String[] args) {

        AVLTree2 avl = new AVLTree2();

        avl.insert(30);
        avl.insert(20);
        avl.insert(40);
        avl.insert(10); // LL
        avl.levelOrder(avl.head);
        avl.delete(40);
        avl.levelOrder(avl.head);


        avl.insert(40);
        avl.delete(10); // RR
        avl.levelOrder(avl.head);

        avl.insert(25);
        avl.delete(40); // LR
        avl.levelOrder(avl.head);

        avl.insert(27);
        avl.delete(20); //RL
        avl.levelOrder(avl.head);

    }
}









