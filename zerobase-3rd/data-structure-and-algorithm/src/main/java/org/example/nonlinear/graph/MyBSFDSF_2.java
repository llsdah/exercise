package org.example.nonlinear.graph;

import java.util.LinkedList;
import java.util.Queue;
import java.util.Stack;

public class MyBSFDSF_2 {

    public static void main(String[] args) {
        MyGraphList graph = new MyGraphList(7);

        graph.addVertex('A');
        graph.addVertex('B');
        graph.addVertex('C');
        graph.addVertex('D');
        graph.addVertex('E');
        graph.addVertex('F');
        graph.addVertex('G');

        graph.addEdge(0,1);
        graph.addEdge(0,2);
        graph.addEdge(0,3);
        graph.addEdge(1,4);
        graph.addEdge(2,5);
        graph.addEdge(3,4);
        graph.addEdge(3,5);
        graph.addEdge(4,6);
        graph.addEdge(5,6);
        graph.printAdjacentList();

        graph.dfs(0);
        System.out.println();
        graph.bfs(0);

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

        public void dfs(int id) {
            boolean[] visited = new boolean[this.elemCnt];
            Stack<Integer> stack = new Stack<>();

            stack.push(id);
            visited[id] = true;

            while (!stack.isEmpty()) {
                int curId = stack.pop();
                System.out.print(this.verties[curId] + " ");
                Node cur = this.adjList[curId];

                while (cur != null) {
                    if (visited[cur.id] == false) {
                        stack.push(cur.id);
                        visited[cur.id] = true;
                    }
                    cur = cur.next;
                }
            }
            System.out.println();
        }

        public void bfs(int id) {
            boolean[] visited = new boolean[this.elemCnt];
            Queue<Integer> que = new LinkedList<>();

            que.offer(id);
            visited[id] = true;

            while (!que.isEmpty()) {
                int curId = que.poll();
                System.out.print(this.verties[curId] + " ");

                Node cur = this.adjList[curId];
                while (cur != null) {
                    if (visited[cur.id] == false) {
                        que.offer(cur.id);
                        visited[cur.id] = true;
                    }

                    cur = cur.next;
                }

            }
            System.out.println();
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
