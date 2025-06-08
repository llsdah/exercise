package org.example.algorithm.minimunspanningtree.practice;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

/**
 * 다양항 행성을 다니기 위해 노력한다 
 * ( x,y,z ) 좌표
 * 행성1 (x1,y1,z1), 행성2 (x2,y2,z2)
 * 행성간 터널 연결 비용 = min( |x1-x2|,|y1-y2|,|z1-z2| )
 * 
 * n개의행성 사이를 n-1개의 터널로 연결하는데 드는 최소 비용 구하기 
 * 
 * data = {{11,-15, -15},{14,-5,15},{-1,-1,-5},{10,-4,-1},{19,-4,19}}
 * output = 4
 * 
 */
public class Practice_1 {

    public static void main(String[] args) {
        int[][] data =  {{11,-15, -15},{14,-5,-15},{-1,-1,-5},{10,-4,-1},{19,-4,19}};
        System.out.println(solution(data));
    }

    // 행성관리 
    static class Point {
        int idx;
        int x;
        int y;
        int z;

        public Point(int idx, int x, int y, int z) {
            this.idx = idx;
            this.x = x;
            this.y = y;
            this.z = z;
        }
    }
    
    // 엣지 관리 사전 정렬
    static class Edge  implements  Comparable<Edge>{
        int from;
        int to;
        int weight;

        public Edge(int from, int to, int weight) {
            this.from = from;
            this.to = to;
            this.weight = weight;
        }

        @Override
        public int compareTo(Edge o) {
            return this.weight - o.weight;
        }
    }
    
    static int[] parent;
    // 간선정보 관리 
    static ArrayList<Edge> edges;
    
    private static int solution(int[][] data) {
        int n = data.length;
        
        Point[] points = new Point[n];
        for (int i = 0; i < n; i++) {
            points[i] = new Point(i, data[i][0], data[i][1], data[i][2]);
        }

        // 각 간선을 다 모와서 계산해야한다.
        edges = new ArrayList<>();

        // x좌표 기준 간선 추가
        Arrays.sort(points, (p1 , p2 ) -> p1.x - p2.x);
        for (int i = 0; i < n - 1; i++) {
            int weight = Math.abs(points[i].x - points[i+1].x);
            edges.add(new Edge(points[i].idx, points[i+1].idx, weight));
        }

        // y 축 기준 간선추가
        Arrays.sort(points,(p1, p2 )-> p1.y - p2.y);
        for (int i = 0; i < n -1; i++) {
            int weight = Math.abs(points[i].y -points[i+1].y);
            edges.add(new Edge(points[i].idx, points[i+1].idx, weight));
        }
        // x 축 기준 간선추가
        Arrays.sort(points,(p1, p2 )-> p1.z - p2.z);
        for (int i = 0; i < n -1; i++) {
            int weight = Math.abs(points[i].z -points[i+1].z);
            edges.add(new Edge(points[i].idx, points[i+1].idx, weight));
        }

        // 크루스칼 쓰기 위한 정렬
        Collections.sort(edges);

        // 노드수 엣지수,
        return kruskal(n,edges);
    }

    public static int kruskal(int n, ArrayList<Edge> edges) {
        parent = new int[n];
        for (int i = 0; i < n; i++) {
            parent[i] = i;
        }
        int weightSum = 0;

        for (int i = 0; i < edges.size(); i++) {
            Edge edge = edges.get(i);

            if (find(edge.from) != find(edge.to)) {
                union(edge.from, edge.to);
                weightSum += edge.weight;
            }
        }

        return weightSum;
    }

    public static void union(int a, int b) {
        int ap = find(a);
        int bp = find(b);

        if (ap != bp) {
            parent[ap] = bp;
        }
    }

    private static int find(int a) {
        if (a == parent[a]) {
            return a;
        }

        return parent[a] = find(parent[a]);

    }


}








