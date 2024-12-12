package org.example.linear.hashtable;

public class MyHashtable2 {

    public static void main(String[] args) {
        MyHashtable ht = new MyHashtable(7);
        ht.setValue(1,1);
        ht.setValue(2,2);
        ht.setValue(3,3);
        ht.setValue(4,4);
        ht.setValue(5,5);
        ht.printHashTable();
        ht.setValue(8,6);
        ht.printHashTable();
    }

    static class MyHashtable{
        Integer[] table;
        int elemCnt;

        MyHashtable(){

        }
        MyHashtable(int size) {
            this.table = new Integer[size];
            this.elemCnt = 0;
        }

        public int getHash(int key) {
            return key % this.table.length;
        }

        public void setValue(int key, int data) {
            int idx = this.getHash(key);
            this.table[idx] = data;
            this.elemCnt++;
        }
        public int getValue(int key) {
            int idx = this.getHash(key);
            return this.table[idx];
        }

        public void removeValue(int key) {
            int idx = this.getHash(key);
            this.table[idx] = null;
            this.elemCnt--;
        }

        public void printHashTable() {
            System.out.println("hashtable data");
            for (int i = 0; i < this.table.length; i++) {
                System.out.println(i + " : "+ this.table[i]);
            }
        }
    }
}









