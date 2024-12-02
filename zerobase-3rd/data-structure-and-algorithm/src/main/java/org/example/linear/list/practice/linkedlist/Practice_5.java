package org.example.linear.list.practice.linkedlist;

/**
 * 연습문제 2
 * 펠림드롬 구현해보기
 * 1,3,2,3,1
 */
public class Practice_5 {

    public static boolean checkPalindrome(LinkedList list){
        Node cur = list.head;
        Node left = list.head;
        Node right = null;

        int cnt = 0;
        while (cur != null) {
            cnt++; // 원소가 몇개 인지
            right = cur;
            cur = cur.next;
        }

        // cur == null , right 마지막 노드
        Node prevRight = right;
        for (int i = 0; i < cnt / 2; i++) {
            if (left.data != right.data) {
                return false;
            }

            left = left.next;
            right = left;
            while (right.next != prevRight) {
                right = right.next;
            }

        }

        return true;
    }





    public static void main(String[] args) {
        LinkedList list = new LinkedList();
        list.addData(1);
        list.addData(3);
        list.addData(2);
        list.addData(3);
        list.addData(1);
        list.showData();

        System.out.println(checkPalindrome(list));

        list = new LinkedList();
        list.addData(3);
        list.addData(5);
        list.addData(5);
        list.addData(3);
        list.addData(1);
        list.showData();
        System.out.println(checkPalindrome(list));


        list.showData();
    }


    // 단반향연결리스트 사용 기존내용
    static class Node {
        int data;
        Node next;

        Node(){}
        Node(int data, Node node){
            this.data = data;
            this.next = node;
        }

    }
    static class LinkedList{
        Node head;

        LinkedList(){}
        LinkedList(Node node) {
            this.head = node;
        }

        public boolean isEmpty(){ return this.head == null; }

        public void addData(int data) {
            if (this.head == null) {
                this.head = new Node(data, null);
            } else {
                Node cur = this.head;
                while (cur.next != null) {
                    cur = cur.next;
                }
                cur.next = new Node(data, null);
            }
        }

        public void removeData(int data) {
            if (this.head==null) {
                System.out.println("null");
                return;
            }

            Node cur = this.head;
            Node pre = cur;

            while (cur != null) {
                if (cur.data == data) {

                    if (cur==this.head) {
                        this.head = cur.next;
                    } else {
                        pre.next = cur.next;
                    }
                    break;
                }
                pre = cur;
                cur = cur.next;
            }

        }

        public boolean findData(int data) {
            Node cur = this.head;
            while (cur != null) {
                if (cur.data == data) {
                    return true;
                }
                cur = cur.next;
            }
            return false;
        }

        public void showData() {
            if (this.head == null) {
                System.out.println(" is null ");
                return;
            }

            Node cur = this.head;
            while (cur != null) {
                System.out.print(cur.data + " ");
                cur = cur.next;
            }
            System.out.println();
        }

    }


}














