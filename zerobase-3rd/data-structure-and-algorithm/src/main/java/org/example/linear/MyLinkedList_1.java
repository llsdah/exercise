package org.example.linear;

/**
 * 연결리스트
 * 데이터를 링크연결, 순서 있지만 메모리상 연속성은 없다.
 *
 * 장점
 * 데이터 공간을 미리 할당할 필요가 없다 . 추가/삭제 용이
 *
 * 단점
 * 연결구조를 위한 별도 데이터 공간 필요
 * 연결 정보찾는 시간 필요, 추가/삭제 수행시 데이터의 연결 재구성 작업필요
 */
public class MyLinkedList_1 {

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


    public static void main(String[] args) {
        LinkedList myList = new LinkedList(new Node(1,null));

        myList.addData(2);
        myList.addData(3);
        myList.addData(4);
        myList.addData(5);
        myList.showAllData();

        myList.findData(3);
        myList.findData(100);

        myList.removeData();
        myList.removeData();
        myList.removeData();
        myList.showAllData(); // 1,2

        myList.removeData();
        myList.removeData();
        myList.removeData(); // 없음

    }
}










