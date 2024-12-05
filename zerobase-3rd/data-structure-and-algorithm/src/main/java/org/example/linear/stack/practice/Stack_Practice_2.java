package org.example.linear.stack.practice;

public class Stack_Practice_2 {

    static class MyStack{
        int[] arr;
        int top = -1;
        MyStack (int size) {
            arr = new int[size];
        }

        public boolean isEmpty() {
            if (this.top == -1) {
                return true;
            } else {
                return false;
            }
        }
        public boolean isFull() {
            if (this.top == this.arr.length -1) {
                return true;
            } else {
                return false;
            }
        }

        public void push(int data) {
            if (this.isFull()) {
                System.out.println("full");
                return;
            }
            this.top ++;
            this.arr[this.top] = data;
        }

        public Integer pop() {
            if (this.isEmpty()) {
                System.out.println("empty");
                return null;
            }
            int data = this.arr[this.top];

            this.arr[this.top] = 0;
            this.top--;

            return data;
        }


        public Integer peek() {
            if (this.isEmpty()) {
                System.out.println("sEmpty()");
                return null;
            }
            return this.arr[this.top];
        }

        public void printStack(){
            for (int i = 0; i <= top; i++) {
                System.out.print(this.arr[i]+" ");
            }
            System.out.println();
        }


    }

    public static void main(String[] args) {
        MyStack myStack = new MyStack(5);

        myStack.push(1);
        myStack.push(2);
        myStack.push(3);
        myStack.push(4);
        myStack.push(5);
        myStack.printStack();
        System.out.println(myStack.peek());
        myStack.printStack();


    }
}
