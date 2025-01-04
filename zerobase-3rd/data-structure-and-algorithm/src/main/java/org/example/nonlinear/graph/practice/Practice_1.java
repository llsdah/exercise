package org.example.nonlinear.graph.practice;

/**
 * Center Node 찾기
 * Undirected 그래프에서 center node를 출력하시오
 * Center node는 다른 모든 노드와 연결된 노드를 의미
 * 다른 노드와 연결된 노드는 하나라고 가성합니다.
 *
 *
 * 입력 그래프 : [[1,2],[2,3],[4,2]]
 * 출력 : 2
 *
 * 입력 그래프 : [[1,2],[5,1],[1,3],[1,4]]
 * 출력 : 1
 *
 */
public class Practice_1 {

    private static int solution(int[][] e) {
        // 간선의 수이기에 노드의 수는 한개가 더 많다.
        MyGraphMatrix graph = new MyGraphMatrix(e.length+1);
        for (int i = 0; i < e.length; i++) {
            // 0 베이스로 입력을 위해
            graph.addEdge(e[i][0]-1,e[i][1]-1);
        }
        // 노드수 만큼
        int[] edgeCnt = new int[e.length+1];

        // 간성 수 입력
        for (int i = 0; i < graph.adjMat.length; i++) {
            for (int j = 0; j < graph.adjMat[0].length; j++) {
                if (graph.adjMat[i][j] == 1) {
                    edgeCnt[i] ++;
                }
            }
        }

        // 간선 수 확인
        int maxCnt = -1;
        int maxIdx = -1;
        for (int i = 0; i < edgeCnt.length; i++) {
            if (maxCnt < edgeCnt[i]) {
                maxCnt = edgeCnt[i];
                maxIdx = i;
            }
        }

        System.out.println("maxIdx = " + maxIdx);
        System.out.println("maxCnt = " + maxCnt);

        return maxIdx + 1;
    }
    /**
     * 아래의 제약이 있다면
     * 간선의 수 = 노드의 수 - 1
     * 모든 노드는 연결되어 있다.
    */
    private static int solution2(int[][] e) {
        return e[0][0] == e[0][1] || e[0][0] == e[1][1] ? e[0][0] : e[0][1];
    }
    
    static class MyGraphMatrix {
        char[] vertices;
        int[][] adjMat;
        int elemCnt;

        public MyGraphMatrix(){}
        public MyGraphMatrix(int size) {
            this.vertices = new char[size];
            this.adjMat = new int[size][size];
            this.elemCnt = 0;
        }

        public boolean isFull() {
            return this.elemCnt == this.vertices.length;
        }

        public void addVertex(char data) {
            if (isFull()) {
                System.out.println("Graph full");
                return;
            }
            this.vertices[this.elemCnt++] = data;
        }

        public void addEdge(int x, int y) {
            this.adjMat[x][y] = 1;
            this.adjMat[y][x] = 1;
        }
    }

    public static void main(String[] args) {
        int[][] edges = {{1,2},{2,3},{4,2}};

        System.out.println("solution(edges) : "+solution(edges));
        System.out.println("solution2(edges) : "+solution2(edges));
        System.out.println();

        edges = new int[][]{{1,2},{5,1},{1,3},{1,4}};
        System.out.println("solution(edges) : "+solution(edges));
        System.out.println("solution2(edges) : "+solution2(edges));
        System.out.println();


    }
}








