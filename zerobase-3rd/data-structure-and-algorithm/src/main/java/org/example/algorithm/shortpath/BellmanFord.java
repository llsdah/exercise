package org.example.algorithm.shortpath;

public class BellmanFord {

    static class Edge {
        int from;
        int to;
        int weight;

        public Edge(int from, int to, int weight) {
            this.from = from;
            this.to = to;
            this.weight = weight;
        }
    }
    // 간선, 데이터 갯수, 데이터, 시작점
    private static void bellmanFord(int v, int e, int[][] data, int start) {
        Edge[] edge = new Edge[e];
        for (int i = 0; i < e; i++) {
            edge[i] = new Edge(data[i][0],data[i][1],data[i][2]);
        }

        int[] dist = new int[v + 1];
        for (int i = 1; i < v + 1; i++) {
            dist[i] = Integer.MAX_VALUE;
        }
        dist[start] = 0;

        boolean isMinus = false;

        for (int i = 0; i < v + 1; i++) {
            for (int j = 0; j < e; j++) {
                Edge cur = edge[j];

                if (dist[cur.from] == Integer.MAX_VALUE) {
                    continue;
                }
                if (dist[cur.to] > dist[cur.from] + cur.weight) {
                    dist[cur.to] = dist[cur.from] + cur.weight;

                    // 음수가 있으면 해당 동일한 index에 값이 변경됩니다.
                    if (i==v) {
                        isMinus = true;
                    }
                }
            }
        }

        System.out.println("음수 발생 여부 : " + isMinus);
        for (int i = 1; i < v+1 ; i++) {
            if (dist[i] == Integer.MAX_VALUE){
                System.out.print("INF ");
            } else {
                System.out.print(dist[i] + " ");
            }
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
        bellmanFord(7,11,data,1);

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
        bellmanFord(7,11,data,1);

    }

}
