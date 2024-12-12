package org.example.linear.hashtable;

public class MyHashtable5 {
    /**
     * 개방주소법 - 이중 해싱
     */
    public static void main(String[] args) {
        MyHashtable ht = new MyHashtable(11);
        ht.setValue(1,1);
        ht.setValue(2,2);
        ht.setValue(3,3);
        ht.printHashTable();
        ht.setValue(1,100);
        ht.setValue(1,200);
        ht.setValue(1,300);
        ht.printHashTable();
    }

    static class MyHashtable extends MyHashtable2.MyHashtable{
        int c;

        MyHashtable(int size) {
            super(size);
            this.c = this.getHashC(size);
        }

        public int getHash2(int key) {
            int hash = 1+ key % this.c;
            return hash;
        }
        public int getHashC(int size) {
            int c = 0;
            if (size <= 2) {
                return size;
            }

            for (int i = size - 1; i > 2; i--) {
                boolean isPrime = true;
                for (int j = 2; j < i; j++) {
                    if (i % j == 0) {
                        isPrime = false;
                        break;
                    }
                }
                if (isPrime) {
                    c = i;
                    break;
                }
            }
            return c;
        }

        public void setValue(int key, int data) {
            int idx = this.getHash(key);

            if (this.elemCnt == this.table.length) {
                System.out.println("null");
                return;
            } else if (this.table[idx] == null) {
                this.table[idx] = data;
            } else {
                int newIdx = idx;
                int cnt = 1;
                while (true) {
                    newIdx = (newIdx + this.getHash2(newIdx) * cnt) % this.table.length;
                    if (this.table[newIdx] == null) {
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
