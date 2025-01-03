package org.example.nonlinear.graph;

public class MyGraphMatrix_1 {

    public static void main(String[] args) {
        MyGraphMatrix gm = new MyGraphMatrix(4);

        gm.addVertex('A');
        gm.addVertex('B');
        gm.addVertex('C');
        gm.addVertex('D');

        gm.addEdge(0,1);
        gm.addEdge(0,2);
        gm.addEdge(1,2);
        gm.addEdge(1,3);
        gm.addEdge(2,3);
        gm.printAdjacentMatrix();
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
