package org.example.linear.practice.linkedlist;

/**
 * 원형 연결리스트
 * 
 */
public class Practice_3 {
    
    static class CircularLinkedList {
        NodeBi head;
        NodeBi tail;
        
        CircularLinkedList(NodeBi node){
            this.head = node;
            this.tail = node;
            node.next = this.head; // 자기자신 참조
            node.prev = this.head; // 자기자신 참조
        }

        public boolean isEmpty() {
            if (this.head == null) {
                return true;
            }
            return false;
        }

        // 데이터 추가, beforedat가 null 이면 가장 뒤에, 값이 있다면 해당 값을 가진 노드 앞에 추가
        public void addData(int data, Integer beforeData){
            if (this.head == null) {
                NodeBi newNodeBi = new NodeBi(data, null, null);
                this.head = newNodeBi;
                this.tail = newNodeBi;
                newNodeBi.next = newNodeBi;
                newNodeBi.prev = newNodeBi;
            } else if (beforeData == null) {
                NodeBi newNodeBi = new NodeBi(data, this.head, this.tail);
                this.tail.next = newNodeBi;
                this.head.prev = newNodeBi;
                this.tail = newNodeBi;
            } else {
                NodeBi cur = this.head;
                NodeBi pre = cur;
                do {
                    if (cur.data == beforeData) {
                        if (cur == this.head) {
                            NodeBi newNodeBi = new NodeBi(data, this.head, this.tail);
                            this.tail.next = newNodeBi;
                            this.head.prev = newNodeBi;
                            this.head = newNodeBi;
                        } else {
                            NodeBi newNodeBi = new NodeBi(data, cur, pre);
                            pre.next = newNodeBi;
                            cur.prev = newNodeBi;
                        }
                        break;
                    }
                    pre = cur;
                    cur = cur.next;
                } while (cur != this.head);
            }
        }

        public void removeData(int data) {
            if (this.isEmpty()) {
                System.out.println("data is null");
                return;
            }
            NodeBi cur = this.head;
            NodeBi pre = cur;

            while (cur != null) {
                if (cur.data == data) {
                    // 노드가 하나일때
                    if (cur == this.head && cur == this.tail) {
                        this.head = null;
                        this.tail = null;
                    } else if (cur == this.head) {
                        cur.next.prev = this.head.prev; // tail도 된다.
                        this.head = cur.next;
                        this.tail.next = this.head;
                    } else if (cur == this.tail) {
                        pre.next = this.tail.next;
                        this.tail = pre;
                        this.head.prev = this.tail;
                    // 노드가 중간일때
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
                System.out.println("list is empty");
                return;
            }

            NodeBi cur = this.head;
            while (cur.next != this.head) {
                System.out.print(cur.data + " ");
                cur = cur.next;
            }
            System.out.println(cur.data);
        }

    }

    public static void main(String[] args) {
        CircularLinkedList myList = new CircularLinkedList(new NodeBi(1,null,null));
        myList.showData();

        myList.addData(2,null);
        myList.addData(3,null);
        myList.addData(4,null);
        myList.addData(5,null);
        myList.showData();

        myList.addData(100, 1);
        myList.addData(200, 2);
        myList.addData(300, 3);
        myList.addData(400, 4);
        myList.addData(500, 5);
        myList.showData();

        myList.removeData(100);
        myList.removeData(200);
        myList.removeData(300);
        myList.removeData(400);
        myList.removeData(500);
        myList.showData();

        myList.removeData(1);
        myList.removeData(2);
        myList.removeData(3);
        myList.removeData(4);
        myList.removeData(5);
        myList.showData();

    }

    // 기존 데이터
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
    
}
