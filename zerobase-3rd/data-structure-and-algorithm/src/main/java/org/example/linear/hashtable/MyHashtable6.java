package org.example.linear.hashtable;

public class MyHashtable6 {
    /**
     * 분리 연결법
     */
    public static void main(String[] args) {
        MyHashtable ht = new MyHashtable(11);
        ht.setValue(1,10);
        ht.setValue(2,20);
        ht.setValue(3,30);
        ht.printHashtable();
        ht.setValue(12,11);
        ht.setValue(23,12);
        ht.setValue(34,13);

        ht.setValue(13,21);
        ht.setValue(24,22);
        ht.setValue(35,23);

        ht.setValue(5,1);
        ht.setValue(16,2);
        ht.setValue(27,3);

        ht.printHashtable();

        System.out.println("찾기");
        System.out.println(ht.getValue(1));
        System.out.println(ht.getValue(12));

        System.out.println("지우기");
        ht.removeValue(1);
        ht.removeValue(5);
        ht.removeValue(16);
        ht.printHashtable();
    }
    static class MyHashtable {
        LinkedList[] table;
        MyHashtable(int size) {
            this.table = new LinkedList[size];
            for (int i = 0; i < this.table.length; i++) {
                this.table[i] = new LinkedList(null);
            }
        }

        public int getHash(int key) {
            return key % this.table.length;
        }

        public void setValue(int key, int data) {
            int idx = this.getHash(key);
            this.table[idx].addData(key, data);
        }

        public int getValue(int key){
            int idx = this.getHash(key);
            int data = this.table[idx].findData(key);
            return data;
        }

        public void removeValue(int key) {
            int idx = this.getHash(key);
            this.table[idx].removeData(key);
        }

        public void printHashtable(){
            System.out.println("hash table");
            for (int i = 0; i < this.table.length; i++) {

                this.table[i].showAllData();
            }
        }

    }


    static class Node {
        int key;
        int data;
        Node next;
        Node() {}
        Node(int key, int data, Node next){
            this.key = key;
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
        public void addData(int key, int data) {
            if (this.head == null) { // 없으면 헤드에다가 넣기
                this.head = new Node(key, data,null);
            } else {
                Node cur = this.head;
                while (cur.next != null) {
                    cur = cur.next;
                }
                cur.next = new Node(key, data, null);
            }
        }
        // 맨뒤의 데이터 삭제
        public void removeData(int data) {
            if (this.isEmpty()) {
                return;
            }
            Node cur = this.head;
            Node prev = cur; // 이전의 next는 null이여야합니다
            while (cur.next != null) {
                if (cur.key == data) {
                    if (cur == this.head) {
                        this.head = cur.next;
                    } else {
                        prev.next = cur.next;
                    }
                    break;
                }
                prev = cur;
                cur = cur.next;
            }


        }

        public Integer findData(int data) {
            if (this.isEmpty()) {
                return null;
            }
            Node cur = this.head;
            while (cur != null) {
                if (cur.key == data) {
                    System.out.println("Data exist");
                    return cur.data;
                }
                cur = cur.next;
            }
            System.out.println("없다");
            return null;
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
