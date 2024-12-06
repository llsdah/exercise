package org.example.linear.queue;

import java.util.ArrayList;

public class Practice1 {

    static class MyQueue {
        ArrayList list;

        MyQueue(){
            this.list = new ArrayList();
        }

        public boolean isEmpty(){
            if (this.list.size() == 0) {
                return true;
            } else {
                return false;
            }
        }

        public void push(int data){
            this.list.add(data);
        }

        public Integer pop() {
            if (this.isEmpty()) {
                System.out.println("empty");
                return null;
            }

            int data = (int) this.list.get(0);
            this.list.remove(0);
            return data;
        }
        public Integer peek() {
            if (this.isEmpty()) {
                System.out.println("empty");
                return null;
            }

            return (int)this.list.get(0);

        }
        public void printQueue(){
            System.out.println(this.list);
        }

    }


    public static void main(String[] args) {
        MyQueue que = new MyQueue();
        que.push(1);
        que.push(2);
        que.push(3);
        que.push(4);
        que.push(5);

        que.printQueue();

        que.pop();

        que.printQueue();

    }



}
