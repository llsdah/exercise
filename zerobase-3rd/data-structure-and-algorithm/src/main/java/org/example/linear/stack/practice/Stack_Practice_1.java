package org.example.linear.stack.practice;

import java.util.ArrayList;

public class Stack_Practice_1 {

    static class MyStact1 {
        ArrayList list;

        MyStact1(){
            this.list = new ArrayList();
        }

        public boolean isEmpty(){
            if (this.list.size() == 0) {
                return true;
            } else {
                return false;
            }
        }

        public void push(int data) {
            this.list.add(data);
        }

        public Integer pop() {
            if (this.isEmpty()) {
                System.out.println("pop is empty");
                return null;
            }

            int data = (int) this.list.get(this.list.size()-1);
            this.list.remove(this.list.size()-1);
            return data;
        }

        public Integer peek() {
            if (this.isEmpty()) {
                System.out.println("peek is empty");
                return null;
            }
            int data = (int) this.list.get(this.list.size()-1);
            return data;
        }

        public void printStack(){
            System.out.println("this.list = " + this.list);
        }
    }

    public static void main(String[] args) {
        MyStact1 myStact1 = new MyStact1();
        myStact1.isEmpty();
        myStact1.push(1);
        myStact1.push(2);
        myStact1.push(3);
        myStact1.push(4);
        myStact1.push(5);
        myStact1.printStack();

        System.out.println("myStact1 = " + myStact1.peek());

        System.out.println("myStact1.pop() = " + myStact1.pop());
        System.out.println("myStact1.pop() = " + myStact1.pop());
        myStact1.printStack();

    }
}










