package org.example.linear.practice.linkedlist;

/**
 * 양방향 림크드 리스트
 */
public class Practice_2 {

    static class NodeBi {
        int data;
        NodeBi next;
        NodeBi prev;

        NodeBi(int data, NodeBi next, NodeBi prev) {
            this.data = data;
            this.next = next;
            this.prev = prev;
        }
    }

    static class DoublyLinkedList extends LinkedList {
        NodeBi head;
        NodeBi tail;

        DoublyLinkedList (NodeBi node) {
            this.head = node;
            this.tail = node;
        }

        public boolean isEmpty() {
            if (this.head == null) {
                return true;
            }
            return false;
        }

        public void addData(int data, Integer beforeData) {
            if (this.head == null) {
                this.head = new NodeBi(data, null, null);
                this.tail = this.head;
            } else if (beforeData == null) {
                this.tail.next = new NodeBi(data, null, this.tail);
                this.tail = this.tail.next;
            } else {
                NodeBi cur = this.head;
                NodeBi prev = cur;
                while (cur != null) {
                    if (cur.data == beforeData) {
                        if (cur == this.head) {
                            this.head = new NodeBi(data, this.head, null);
                            this.head.next.prev = this.head;
                        } else {
                            prev.next = new NodeBi(data, cur, prev);
                            cur.prev = prev.next;
                        }
                        break;
                    }
                    prev = cur;
                    cur = cur.next;
                }
            }
        }

        public void removeData(int data) {
            if (this.isEmpty()) {
                System.out.println("삭제할 데이터가 없다");
                return;
            }

            NodeBi cur = this.head;
            NodeBi pre = cur;

            while (cur != null) {
                if (cur.data == data) {
                    if (cur == this.head && cur == this.tail) {
                        this.head = null;
                        this.tail = null;
                    } else if (cur == this.head) {
                        this.head = cur.next;
                        this.head.prev = null;
                    } else if (cur == this.tail) {
                        this.tail = this.tail.prev;
                        this.tail.next = null;
                    } else {
                        pre.next = cur.next;
                        cur.next.prev = pre;
                    }
                    break;
                }
                pre = cur;
                cur = cur.next;
            }
        }

        public void showData() {
            if (this.isEmpty()) {
                System.out.println("볼게 없다");
                return;
            }
            NodeBi cur = this.head;
            while (cur != null) {
                System.out.print(cur.data + " ");
                cur = cur.next;
            }
            System.out.println();
        }

        public void showDataFromTail() {
            if (this.isEmpty()) {
                System.out.println("볼게 없다");
                return;
            }
            NodeBi cur = this.tail;
            while (cur != null) {
                System.out.print(cur.data + " ");
                cur = cur.prev;
            }
            System.out.println();

        }


    }


    public static void main(String[] args) {
        DoublyLinkedList myList = new DoublyLinkedList(new NodeBi(1,null,null));
        myList.showData();

        myList.addData(2,null);
        myList.addData(3,null);
        myList.addData(4,null);
        myList.addData(5,null);
        myList.showData();
        myList.showDataFromTail();

        myList.addData(100, 1);
        myList.addData(200, 2);
        myList.addData(300, 3);
        myList.addData(400, 4);
        myList.addData(500, 5);
        myList.showData();
        myList.showDataFromTail();

        myList.removeData(100);
        myList.removeData(200);
        myList.removeData(300);
        myList.removeData(400);
        myList.removeData(500);
        myList.showData();
        myList.showDataFromTail();

        myList.removeData(1);
        myList.removeData(2);
        myList.removeData(3);
        myList.removeData(4);
        myList.removeData(5);
        myList.showData();
        myList.showDataFromTail();

    }


    // 기존 데이터
    static class LinkedList {
        Practice_1.Node head;
        LinkedList(){}
        LinkedList(Practice_1.Node node){
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
                this.head = new Practice_1.Node(data,null);
            } else {
                Practice_1.Node cur = this.head;
                while (cur.next != null) {
                    cur = cur.next;
                }
                cur.next = new Practice_1.Node(data, null);
            }
        }
        // 맨뒤의 데이터 삭제
        public void removeData() {
            if (this.isEmpty()) {
                return;
            }
            Practice_1.Node cur = this.head;
            Practice_1.Node prev = cur; // 이전의 next는 null이여야합니다
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
            Practice_1.Node cur = this.head;
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
            Practice_1.Node cur = this.head;
            while (cur != null) {
                System.out.print(cur.data + " ");
                cur = cur.next;
            }
            System.out.println();
        }
    }

}
