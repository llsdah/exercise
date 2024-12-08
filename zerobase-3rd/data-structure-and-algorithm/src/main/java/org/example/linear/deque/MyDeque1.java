package org.example.linear.deque;

import org.example.linear.queue.MyQueue;

import java.util.ArrayList;

public class MyDeque1 {

    static class MyDeque{
        ArrayList list;

        MyDeque() {
            this.list = new ArrayList();
        }

        public boolean isEmpty() {
            if (this.list.size() == 0) {
                return true;
            } else {
                return false;
            }
        }
        public void addFirst(int data){
            this.list.add(0,data);
        }
        public void addLast(int data){
            this.list.add(data);
        }
        public Integer removeFirst() {
            if (this.isEmpty()) {
                System.out.println("null");
                return null;
            }

            int data = (int) this.list.get(0);
            this.list.remove(0);

            return data;

        }

        public Integer removeLast() {
            if (this.isEmpty()) {
                System.out.println("null");
                return null;
            }

            int data = (int) this.list.get(this.list.size() - 1);
            this.list.remove(this.list.size() -1);

            return data;
        }

        public void printDeque(){
            System.out.println(this.list);
        }

    }

    public static void main(String[] args) {
        MyDeque deque = new MyDeque();

        deque.addFirst(1);
        deque.addFirst(2);
        deque.addFirst(3);
        deque.printDeque();


        deque.addLast(10);
        deque.addLast(20);
        deque.addLast(30);

        deque.printDeque();
        System.out.println(deque.removeFirst());
        System.out.println();
        System.out.println(deque.removeLast());
        System.out.println();
    }



}
