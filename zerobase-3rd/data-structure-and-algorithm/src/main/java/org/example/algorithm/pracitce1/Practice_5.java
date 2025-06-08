package org.example.algorithm.pracitce1;

import java.util.ArrayList;
import java.util.Collections;

/**
 * 회사 건물 투어 피로고
 *
 * 오르막길 경우 n , n^2 피로드
 * 내리막길은 피로도 변화 없다
 * 내리막길로 내려갔다가 동일 경로로 다시 올라오는 경우 피로도 변화 없다
 *
 * n 건물, m경로, weight 오르막길 0, 내리막길 1
 * 최악과 최적의 경로 차이를 계산하자
 *
 * n 4
 * m 5
 * data {{1,2,0},{1,4,0},{2,3,0},{3,4,1},{4,2,1}}
 * output 8;
 *
 */

public class Practice_5 {

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

    static int[] parent;
    static ArrayList<Edge> edgeList;

    //크루스탈 로 사용
    private static void solution(int n, int m, int[][] data) {

        edgeList = new ArrayList<>();

        for (int i = 0; i < m; i++) {
            edgeList.add(new Edge(data[i][0],data[i][1],data[i][2]));
        }

        //최악경로
        // 오름차순 정렬, 오르막길부터 선택됨 최악
        Collections.sort(edgeList, (x,y) -> x.weight - y.weight);
        int worst = kruskal(n,edgeList);


        //최적경로
        // 내림차순 정렬, 내림차순 정렬
        Collections.sort(edgeList, (x,y) -> y.weight - x.weight);
        int best = kruskal(n,edgeList);

        System.out.println("worst = " + worst);
        System.out.println("best = " + best);
        System.out.println("answer : "+(Math.pow(worst,2)-Math.pow(best,2)));
    }
    public static int kruskal(int n , ArrayList<Edge> edges) {
        parent = new int[n+1];
        for (int i = 0; i < n+1; i++) {
            parent[i] = i;
        }

        int weightSum = 0;
        for (int i = 0; i < edges.size(); i++) {
            Edge edge = edges.get(i);

            if (find(edge.from) != find(edge.to)) {
                union(edge.from, edge.to);
                
                if (edge.weight == 0) { // 오르막길인 경우만 하면됨
                    weightSum ++;
                }
                
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

    public static int find(int a) {
        if (a == parent[a]) {
            return a;
        }
        return parent[a] = find(parent[a]);
    }


    // mst kruskal
    public static void main(String[] args) {
        int n = 4;
        int m = 5;
        int[][] data = {{1,2,0},{1,4,0},{2,3,0},{3,4,1},{4,2,1}};

        solution(n,m,data);
    }
}
