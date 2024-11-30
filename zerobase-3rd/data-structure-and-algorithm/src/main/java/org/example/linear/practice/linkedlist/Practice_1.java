package org.example.linear.practice.linkedlist;

public class Practice_1 {

    static class LinkedList2 extends LinkedList{
        LinkedList2(Node node){
            this.head = node;
        }

        // beforeData 전에 입력될 데이터가 data 이다 
        // 데이터 추가 : 직전 데이터가 null인 경우 가장 뒤에 추가, 직전데이터가 존재 한다면 해당값노드 앞에 추가
        public void addData(int data, Integer beforeData) {
            if (this.head == null) {
                this.head = new Node(data, null);
            } else if (beforeData == null) {
                Node cur = this.head;
                while (cur.next != null) {
                    cur = cur.next;
                }
                cur.next = new Node (data, null);
            } else {
                Node cur = this.head;
                Node pre = cur;
                while (cur != null) {
                    if (cur.data == beforeData) {
                        if (cur == this.head) {
                            this.head = new Node(data,this.head);
                        } else {
                            pre.next = new Node(data, cur);
                        }
                        break;
                    }
                    pre = cur;
                    cur = cur.next;
                }
            }
        }

        public void removeData(int data) {
            if (this.head == null) {
                System.out.println("삭제할거 없음");
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
    }

    public static void main(String[] args) {
        LinkedList2 mylist = new LinkedList2(new Node(1,null));
        mylist.addData(2);
        mylist.addData(3);
        mylist.addData(4);
        mylist.addData(5);
        mylist.addData(100,1);
        mylist.addData(200,2);
        mylist.addData(300,3);
        mylist.addData(400,4);
        mylist.addData(500,5);
        mylist.showAllData();

        mylist.removeData(300);
        mylist.removeData(100);
        mylist.removeData(500);
        mylist.removeData(200);
        mylist.removeData(400);
        mylist.showAllData();

        mylist.removeData(3);
        mylist.removeData(1);
        mylist.removeData(4);
        mylist.removeData(2);
        mylist.removeData(5);
        mylist.showAllData();

    }

    // 기존 데이터 MyLinkedList_1
    static class Node {
        int data;
        Node next;
        Node() {}
        Node(int data, Node next){
            this.data = data;
            this.next = next;
        }
    }

    static class LinkedList {
        Node head;
        LinkedList(){}
        LinkedList(Node node){
            this.head = node;
        }
        public boolean isEmpty() {
            if (this.head == null) {
                System.out.println("비었다.");
                return true;
            }
            return false;
        }
        // 맨뒤에 데이터 추가하는 경우
        public void addData(int data) {
            if (this.head == null) { // 없으면 헤드에다가 넣기
                this.head = new Node(data,null);
            } else {
                Node cur = this.head;
                while (cur.next != null) {
                    cur = cur.next;
                }
                cur.next = new Node(data, null);
            }
        }
        // 맨뒤의 데이터 삭제
        public void removeData() {
            if (this.isEmpty()) {
                return;
            }
            Node cur = this.head;
            Node prev = cur; // 이전의 next는 null이여야합니다
            while (cur.next != null) {
                prev = cur;
                cur = cur.next;
            }
            if (cur == this.head) {
                this.head = null;
            } else {
                prev.next = null;
            }

        }

        public void findData(int data) {
            if (this.isEmpty()) {
                return;
            }
            Node cur = this.head;
            while (cur != null) {
                if (cur.data == data) {
                    System.out.println("Data exist");
                    return;
                }
                cur = cur.next;
            }
            System.out.println("없다");
        }
        public void showAllData() {
            if (this.isEmpty()){
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
