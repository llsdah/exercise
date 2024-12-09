package org.example.linear.deque.pratice;

/**
 * 데크 사이즈 두배 업그레이드 하기
 *
 */
public class Practice_4 {


    public static void main(String[] args) {
        MyDeque deque = new MyDeque(3);

        deque.addFirst(1);
        deque.addFirst(2);
        deque.addFirst(3);
        deque.printDeque();

        deque.addLast(10);
        deque.addLast(20);
        deque.addLast(30);

        deque.printDeque();
        System.out.println(deque.removeFirst());
        deque.printDeque();
        System.out.println();
        System.out.println(deque.removeLast());
        System.out.println();
    }

    static class MyDeque{
        int[] arr;
        int front = 0;
        int rear = 0;

        MyDeque(int size) {
            this.arr = new int[size + 1];
        }
        public boolean isEmpty(){
            return this.rear == this.front;
        }

        public boolean isFull() {
            return (this.rear + 1) % this.arr.length == this.front;
        }

        public void addFirst(int data) {
            if (this.isFull()) {
                System.out.println(" = ");
                increaseSize();
            }

            this.arr[this.front] = data;
            this.front = (this.front - 1 + this.arr.length) % this.arr.length;
        }

        public void addLast(int data) {
            if (this.isFull()) {
                System.out.println(" = ");
                increaseSize();
            }

            this.rear = (this.rear + 1) % this.arr.length;
            this.arr[rear] = data;
        }
        public void increaseSize(){
            int[] arrTmp = this.arr.clone();
            this.arr = new int[this.arr.length*2];

            int start = (this.front+1)%arrTmp.length;
            int end = (this.rear+1)%arrTmp.length;

            int idx = 1; // 사이즈 하나 늘려놓음
            for (int i = start; i != end; i = (i+1) % arrTmp.length ){
                this.arr[idx++] = arrTmp[i];
            }

            this.front = 0;
            this.rear = idx - 1;
        }

        public Integer removeFirst(){
            if (this.isEmpty()) {
                System.out.println("empty");
                return null;
            }

            this.front = (this.front + 1) % this.arr.length;
            return this.arr[this.front];
        }


        public Integer removeLast() {
            if (this.isEmpty()) {
                System.out.println("empty");
                return null;
            }
            int data = this.arr[this.rear];
            this.rear = (this.rear -1 + this.arr.length) % this.arr.length;
            return data;
        }

        public void printDeque(){
            int start = (this.front + 1) % this.arr.length;
            int end = (this.rear + 1) % this.arr.length;

            for (int i = start; i != end; i = (i+1) % this.arr.length) {
                System.out.print(this.arr[i] + " ");
            }
            System.out.println();
        }


    }
}






