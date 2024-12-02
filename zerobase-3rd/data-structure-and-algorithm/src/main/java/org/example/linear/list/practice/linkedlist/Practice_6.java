package org.example.linear.list.practice.linkedlist;

/**
 * 연습문제 3
 * 주어진 연결 리스트에서 시작 위치부터 끝 위치 사이의 노드들을 뒤집으시오
 *
 * 1,2,3,4,5 시작위치2 끝위치 4
 * 1,4,3,2,5
 *
 */

public class Practice_6 {

    public static LinkedList reverseList(LinkedList list, int left, int right) {
        Node cur1 = null;
        Node pre1 = null;

        cur1 = list.head;

        for (int i = 0; i < left - 1; i++) {
            pre1 = cur1;
            cur1 = cur1.next;
        }

        Node cur2 = cur1;
        Node pre2 = pre1;
        Node after = null;
        for (int i = left; i <= right ; i++) {
            after = cur2.next;
            cur2.next = pre2;
            pre2 = cur2;
            cur2 = after;
        }
        pre1.next = pre2;
        cur1.next = cur2;

        return list;
    }



    public static void main(String[] args) {
        LinkedList list = new LinkedList();
        list.addData(1);
        list.addData(2);
        list.addData(3);
        list.addData(4);
        list.addData(5);
        list.showData();

        reverseList(list,2,4);
        list.showData();

        list = new LinkedList();
        list.addData(1);
        list.addData(2);
        list.addData(3);
        list.addData(4);
        list.addData(5);
        list.addData(6);
        list.addData(7);
        list.showData();
        reverseList(list,3,5);
        list.showData();


    }

    static class Node {
        int data;
        Node next;
        Node(){}
        Node(int data, Node next) {
            this.data = data;
            this.next = next;
        }
    }

    static class LinkedList {
        Node head;
        LinkedList(){}
        LinkedList(Node node){ this.head = node;}

        public boolean isEmpty(){return this.head == null;}

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
            if (this.head == null) {
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
