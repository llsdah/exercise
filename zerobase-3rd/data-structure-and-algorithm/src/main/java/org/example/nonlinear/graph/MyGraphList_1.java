package org.example.nonlinear.graph;

public class MyGraphList_1 {

    public static void main(String[] args) {
        MyGraphList graph = new MyGraphList(4);

        graph.addVertex('A');
        graph.addVertex('B');
        graph.addVertex('C');
        graph.addVertex('D');

        graph.addEdge(0,1);
        graph.addEdge(0,2);
        graph.addEdge(1,2);
        graph.addEdge(1,3);
        graph.addEdge(2,3);
        graph.printAdjacentList();
    }

    static class Node {
        int id;
        Node next;

        public Node(int id, Node next) {
            this.id = id;
            this.next = next;
        }
    }

    static class MyGraphList {
        char[] verties;
        Node[] adjList;
        int elemCnt;

        public MyGraphList() {
        }

        public MyGraphList(int size) {
            this.verties = new char[size];
            this.adjList = new Node[size];
            this.elemCnt = 0;
        }

        public boolean isFull() {
            return this.elemCnt == this.verties.length;
        }

        public void addVertex(char data) {
            if (isFull()) {
                System.out.println("full");
                return;
            }
            this.verties[elemCnt++] = data;
        }

        // 연결시 원래는 위에 붙여야 하는데 어짜피 다 동일하기에 앞에다 빠르게 붙이는 것으로 함
        public void addEdge(int x, int y) {
            this.adjList[x] = new Node(y, this.adjList[x]);
            this.adjList[y] = new Node(x, this.adjList[y]);
        }

        public void addDirectedEdge(int x, int y) {
            this.adjList[x] = new Node(y, this.adjList[x]);
        }

        public void printAdjacentList() {
            for (int i = 0; i < this.elemCnt; i++) {
                System.out.print(this.verties[i] + " ");

                Node cur = this.adjList[i];
                while (cur != null) {
                    System.out.print(this.verties[cur.id] + " ");
                    cur = cur.next;
                }
                System.out.println();
            }
        }
    }
}








