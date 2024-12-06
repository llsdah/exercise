package org.example.linear.queue;

public class Practice_2 {

    // 원형 큐 처럼 해야됨
    static class MyQueue_Array{
        int[] arr;
        int front = 0;
        int rear = 0;

        MyQueue_Array(int size) {
            arr = new int[size+1];
        }
        public boolean isEmpty(){
            return this.rear == this.front;
        }

        // 여기가 힘드네
        private boolean isFull() {
            return (this.rear + 1) % this.arr.length == this.front;
        }

        public void enqueue(int data){
            if (this.isFull()){
                System.out.println("full");
                return;
            }
            this.rear = (this.rear + 1) % this.arr.length;
            this.arr[this.rear] = data;
        }

        public Integer dequeue() {
            if (this.isEmpty()) {
                System.out.println("emtpy");
                return null;
            }

            front = (front + 1) % this.arr.length;
            return this.arr[front];
        }

        public void printQueue() {
            int start = (this.front + 1) % this.arr.length;
            int end = (this.rear + 1)  % this.arr.length;

            for (int i = start; i != end; i = (i + 1) % this.arr.length) {
                System.out.print(this.arr[i] + "");
            }
            System.out.println();
        }
    }

    public static void main(String[] args) {
        MyQueue_Array que = new MyQueue_Array(5);
        que.enqueue(1);
        que.enqueue(2);
        que.enqueue(3);
        que.enqueue(4);
        que.enqueue(5);
        que.enqueue(6);
        que.printQueue();

        System.out.println("que.dequeue() = " + que.dequeue());
        que.printQueue();
        que.enqueue(6);
        que.printQueue();
        System.out.println("que.dequeue() = " + que.dequeue());

        que.enqueue(7);
        que.enqueue(8);

        que.printQueue();

        que.dequeue();
        que.printQueue();
    }
}
