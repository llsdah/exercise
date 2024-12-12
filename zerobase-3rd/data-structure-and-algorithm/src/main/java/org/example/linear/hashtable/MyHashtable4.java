package org.example.linear.hashtable;

public class MyHashtable4 {
    /**
     * 개방주소법 - 제곱탐사법
     */
    public static void main(String[] args) {
        MyHashtable ht = new MyHashtable(10);
        ht.setValue(1,1);
        ht.setValue(1,2);
        ht.setValue(1,3);
        ht.setValue(1,4);
        ht.setValue(1,5);
        ht.printHashTable();
        ht.setValue(8,6);
        ht.printHashTable();
    }

    static class MyHashtable extends MyHashtable2.MyHashtable {
        MyHashtable(int size){
            super(size);
        }

        @Override
        public void setValue(int key, int data) {
            int idx = this.getHash(key);

            if (this.elemCnt == this.table.length) {
                System.out.println("Hash table is full");
                return;
            } else if (this.table[idx] == null) {
                this.table[idx] = data;
            } else {
                int newIdx = idx;
                int cnt = 0;
                while (true) {
                    newIdx = (newIdx + (int)Math.pow(2, cnt)) % this.table.length;
                    if (this.table[newIdx] == null){
                        break;
                    }
                    cnt ++;
                }
                this.table[newIdx] = data;
            }
            this.elemCnt++;
        }
    }
}
