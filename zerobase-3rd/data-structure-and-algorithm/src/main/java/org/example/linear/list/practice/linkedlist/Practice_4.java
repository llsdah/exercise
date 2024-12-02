package org.example.linear.list.practice.linkedlist;

/**
 * 문제 풀이 1
 * 단방향 연결리스트에서 중복 데이터를 찾아 삭제 하세요
 * 1,3,3,1,4,2,4,2
 * 1,3,4,2
 */

public class Practice_4 {

    public static void main(String[] args) {
        LinkedList list = new LinkedList();
        list.addData(1);
        list.addData(3);
        list.addData(3);
        list.addData(1);
        list.addData(4);
        list.addData(2);
        list.addData(4);
        list.addData(2);
        list.showData();

        list = removeDup(list);
        list.showData();
    }

    public static LinkedList removeDup(LinkedList listBefore) {

        LinkedList after = new LinkedList();
        Node cur = listBefore.head;
        while (cur != null) {
            if (!after.findData(cur.data)){
                after.addData(cur.data);
            }
            cur = cur.next;
        }

        return after;
    }

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

        public boolean isEmpty(){
            return this.head == null;
        }

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
                    if (cur == this.head) {
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
            if (this.head==null) {
                System.out.println("null");
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












