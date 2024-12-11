package org.example.linear.hashtable;

public class MyHashtable3 {

    public static void main(String[] args) {
        MyHashTable_2 ht = new MyHashTable_2(7);
        ht.setValue(1,1);
        ht.setValue(1,2);
        ht.setValue(1,3);
        ht.setValue(1,4);
        ht.setValue(3,5);
        ht.printHashTable();
        ht.setValue(8,6);
        ht.printHashTable();
    }
    static class MyHashTable_2 extends MyHashtable2.MyHashtable {
        MyHashTable_2(int size) {
            super(size);
        }

        public void setValue(int key, int data) {
            int idx = this.getHash(key);

            if (this.elemCnt == this.table.length) {
                System.out.println("full");
                return;
            } else if (this.table[idx] == null) {
                this.table[idx] = data;
            } else {
                int newIdx = idx;
                while (true) {
                    newIdx = (newIdx + 1) % this.table.length;
                    if (this.table[newIdx] == null) {
                        break;
                    }
                }
                this.table[newIdx] = data;
            }
            elemCnt++;
        }
    }
}
