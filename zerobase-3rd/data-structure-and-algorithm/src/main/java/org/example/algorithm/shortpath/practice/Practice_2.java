package org.example.algorithm.shortpath.practice;

/**
 * N개 우주가 있고, N개의 우주 사이에 M개의 포탈과 W개의 웜홀이 있다.
 * 각 포탈에는 시간 비용이 있고, 포탈을 통해 우주를 이동했을 때 시간이 흘러 있다.
 * 웜홀에도 시간 비용이 있는데, 웜홀을 통해 우주를 이동하는 경우 시간이 거꾸로 흘러 있다.
 * N 개의 우주를 포탈과 웜홀을 통해 이동한다고 했을때, 출발했을 때 보다 시간이 거꾸로 흘러가 있는 경우가 있는지 판별하는 프로그램을 작성하시오
 *
 * 거꾸로 흘러가 있는 경우가 있으며, true 없으면 false
 *
 * 입력 N : 3, M : 3, W : 1
 * portal : {{1,2,2},{1,3,4},{2,3,1}}
 * wormhole : {{3,1,3}}
 * output false
 */
public class Practice_2 {

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

    final static int INF = 10000000;
    static Edge[] edge;
    static int[] dist;

    // 벨만포드 - 음수 사이클 존재 여부 판단
    private static void solution(int n, int m, int w, int[][] portal, int[][] wormhole) {
        edge = new Edge[m + w];// m 과 w 가  다 간선

        for (int i = 0; i < m; i++) {
            edge[i] = new Edge(portal[i][0], portal[i][1], portal[i][2]);
        }

        for (int i = m; i < m + w; i++) { // 웜홀 음수
            edge[i] = new Edge(wormhole[i-m][0], wormhole[i-m][1], -wormhole[i-m][2]);
        }

        dist = new int[n+1];
        for (int i = 0; i < n + 1; i++) {
            dist[i] = INF;
        }

        dist[1] = 0;

        System.out.println(bellmanFord(n,m+w));

    }

    private static boolean bellmanFord(int v, int e) {
        boolean isMinusCycle = false;

        for (int i = 0; i < v + 1; i++) {
            for (int j = 0; j < e; j++) {

                Edge cur = edge[j];

                if (dist[cur.from] == INF) {
                    continue;
                }

                if (dist[cur.to] > dist[cur.from] + cur.weight) {
                    dist[cur.to] = dist[cur.from] + cur.weight;

                    if (i == v) {
                        isMinusCycle = true;
                    }

                }

            }
        }
        return isMinusCycle;
    }

    public static void main(String[] args) {
        int n = 3;
        int m = 3;
        int w = 1;
        int[][] portal = {{1,2,2},{1,3,4},{2,3,1}};
        int[][] wormhole = {{3,1,3}};

        solution(n,m,w,portal,wormhole);

        System.out.println();

        n = 3;
        m = 2;
        w = 1;
        portal = new int[][] {{1,2,3},{2,3,4}};
        wormhole = new int[][] {{3,1,8}};
        solution(n,m,w,portal,wormhole);
    }
}
