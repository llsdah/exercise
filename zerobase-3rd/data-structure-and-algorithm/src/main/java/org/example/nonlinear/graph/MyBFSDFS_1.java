package org.example.nonlinear.graph;

import java.util.LinkedList;
import java.util.Queue;
import java.util.Stack;

public class MyBFSDFS_1 {

    public static void main(String[] args) {
        MyGraphMatrix gm = new MyGraphMatrix(7);

        gm.addVertex('A');
        gm.addVertex('B');
        gm.addVertex('C');
        gm.addVertex('D');
        gm.addVertex('E');
        gm.addVertex('F');
        gm.addVertex('G');

        gm.addEdge(0,1);
        gm.addEdge(0,2);
        gm.addEdge(0,3);
        gm.addEdge(1,4);
        gm.addEdge(2,5);
        gm.addEdge(3,4);
        gm.addEdge(3,5);
        gm.addEdge(4,6);
        gm.addEdge(5,6);

        gm.printAdjacentMatrix();
        gm.dfs(0);
        System.out.println();
        gm.bfs(0);


    }

    static class MyGraphMatrix {
        char[] vertices;
        int[][] adjMat;
        int elemCnt;

        public MyGraphMatrix() {
        }

        public MyGraphMatrix(int size) {
            this.vertices = new char[size];
            this.adjMat = new int[size][size];
            this.elemCnt = 0;
        }

        public void dfs(int id) {
            boolean[] visited = new boolean[this.elemCnt];
            Stack<Integer> stack = new Stack<>();

            stack.push(id);
            visited[id] = true;

            // 인접배열 방문 확인
            while (!stack.isEmpty()) {
                int curId = stack.pop();
                System.out.print(this.vertices[curId] + " ");
                for (int i = this.elemCnt -1; i >= 0; i--) {
                    // 인접하고 방문 했는지 확인
                    if (this.adjMat[curId][i] == 1 && visited[i] == false) {
                        stack.push(i);
                        visited[i] = true;
                    }
                }
            }

        }

        public void bfs(int id) {
            boolean[] visited = new boolean[this.elemCnt];
            Queue<Integer> que = new LinkedList<>();

            que.offer(id);
            visited[id] = true;

            while (!que.isEmpty()) {
                int curID = que.poll();
                System.out.print(this.vertices[curID] + " ");

                for (int i = this.elemCnt - 1; i >= 0; i--) {
                    if (this.adjMat[curID][i] == 1 && visited[i] == false) {
                        que.offer(i);
                        visited[i] = true;
                    }
                }
            }
            System.out.println();
        }



        public boolean isFull() {
            return this.elemCnt == this.vertices.length;
        }

        // 어떤 노드 들이 있는지 추가
        public void addVertex(char data) {
            if (isFull()) {
                System.out.println("Graph is full");
                return;
            }

            this.vertices[this.elemCnt++] = data;

        }
        // 무방향 : x < - > y 연결
        public void addEdge(int x, int y) {
            this.adjMat[x][y] = 1;
            this.adjMat[y][x] = 1;
        }

        public void addDirectedEdge(int x, int y) {
            this.adjMat[x][y] = 1;
        }

        public void deleteEdge(int x, int y) {
            this.adjMat[x][y] = 0;
            this.adjMat[y][x] = 0;
        }

        public void deleteDirectedEdge(int x, int y) {
            this.adjMat[x][y] = 0;
        }

        public void printAdjacentMatrix() {
            System.out.print("  ");
            for (char item : this.vertices) {
                System.out.print(item + " ");
            }

            System.out.println();

            for (int i = 0; i < this.elemCnt; i++) {
                System.out.print(this.vertices[i] + " ");
                for (int j = 0; j < this.elemCnt; j++) {
                    System.out.print(this.adjMat[i][j] + " ");
                }
                System.out.println();
            }

        }

    }

}
