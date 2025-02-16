package org.example.algorithm.shortpath;

public class FloydWarshall {

    static int[][] dist;
    static final int INF = 100000000;

    // 간선
    public static void floydWarshall(int v, int e, int[][] data, int start){
        dist = new int[v + 1][v + 1];
        for (int i = 1; i < v + 1; i++) {
            for (int j = 1; j < v + 1; j++) {
                if (i != j) { // 대각이 아닌 경우 , 같은값이 아닌경우
                    dist[i][j] = INF;
                }
            }
        }

        // 단순 가는 경우 업데이트
        for (int i = 0; i < e; i++) {
            // 출발지 - 도착지 = 거리
            dist[data[i][0]][data[i][1]] = data[i][2];
        }

        // 거쳐가는 경우에 대한 생각
        for (int k = 1; k < v + 1; k++) {
            // i -> j ( k를 거쳐서 가는 경우가 짧을 때 업데이트
            for (int i = 1; i < v + 1; i++) {
                for (int j = 1; j < v + 1; j++) {
                    // 거쳐가는 것
                    if (dist[i][k] != INF && dist[k][j] != INF) {
                        dist[i][j] = Math.min(dist[i][j], dist[i][k] + dist[k][j]);
                    }
                }
            }
        }

        // 출력
        for (int i = 1; i < v+1; i++) {
            for (int j = 1; j < v+1; j++) {
                if (dist[i][j] >= INF) {
                    System.out.printf("%5s ","INF");
                } else {
                    System.out.printf("%5d ",dist[i][j]);
                }
            }
            System.out.println();
        }
        System.out.println();
    }

    public static void main(String[] args) {
        int[][] data = {
                {1,2,8},
                {1,3,6},
                {1,5,5},
                {2,3,-5},
                {2,4,1},
                {2,6,4},
                {3,4,4},
                {4,7,3},
                {5,6,5},
                {6,2,8},
                {6,7,-7}
        };
        floydWarshall(7,11,data,1);

        data = new int[][]{
                {1,2,8},
                {1,3,6},
                {1,5,5},
                {2,3,-5},
                {2,4,1},
                {2,6,4},
                {3,4,4},
                {4,7,3},
                {5,6,5},
                {6,2,-5},
                {6,7,-7}
        };
        System.out.println();
        floydWarshall(7,11,data,1);
    }
}
