package org.example.linear.deque.pratice;

import org.example.linear.deque.MyDeque2;

/**
 * 기본 데크 구종 중 중간에 데이터를 추가하는 기능을 구현하세요
 * 단, 추가적인 자료구조 생성하지 않음
 *
 * 초기 데크 상태 size 5
 * 1,2,3,4
 * -> 10 추가
 * 1,2,10,3,4
 */
public class Practice_3 {

    public static void main(String[] args) {
        MyDeque deque = new MyDeque(6);

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
        deque.addMiddle(5);
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

        public boolean isEmpty() {
            return this.rear == this.front;
        }

        public boolean isFull() {
            return (this.rear + 1) % this.arr.length == this.front;
        }

        public void addFirst(int data) {
            if (this.isFull()) {
                System.out.println("full");
                return;
            }

            this.arr[this.front] = data;
            this.front = (this.front - 1 + this.arr.length) % this.arr.length;
        }

        public void addLast(int data) {
            if (this.isFull()) {
                System.out.println("full");
                return;
            }
            this.rear = (this.rear + 1) % this.arr.length;
            this.arr[this.rear] = data;

        }

        public void addMiddle(int data) {
            if (this.isFull()) {
                System.out.println("full");
                return;
            }
            int elements = this.rear - this.front;
            if (elements < 0) {
                elements = this.arr.length + elements;
            }
            int mid = (this.rear - elements / 2 + this.arr.length) % this.arr.length + 1;
            System.out.println("front = " + front+", rear = " + rear+ ", mid = " + mid);
            int start = (this.rear + 1) % this.arr.length;
            int end = (this.rear - elements / 2 + this.arr.length) % this.arr.length;

            for (int i = start; i != end; i = (i-1 + this.arr.length) % this.arr.length) {
                this.arr[i] = this.arr[(i - 1 + this.arr.length) % this.arr.length];
            }
            this.arr[mid] = data;
            this.rear = (this.rear + 1) % this.arr.length;
        }

        public Integer removeFirst() {
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
            this.rear = (this.rear - 1 + this.arr.length) % this.arr.length;
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