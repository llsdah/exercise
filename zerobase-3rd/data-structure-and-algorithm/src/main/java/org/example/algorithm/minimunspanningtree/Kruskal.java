package org.example.algorithm.minimunspanningtree;

import java.lang.reflect.Array;
import java.util.Arrays;

public class Kruskal {

    public static void main(String[] args) {
        int v = 7;
        int e = 10;
        // a -> b , weight
        int[][] graph = {
                {1,3,1},
                {1,2,9},
                {1,6,8},
                {2,4,13},
                {2,5,2},
                {2,6,7},
                {3,4,12},
                {4,7,17},
                {5,6,5},
                {5,7,20}
        };

        System.out.println(
                kruskal(graph,v,e)
        );

    }

    static int parents[];

    private static int kruskal(int[][] data, int v, int e) {

        int weightSum = 0;

        // 가중치 기준 오름 차순 정렬
        Arrays.sort(data, (x,y)-> x[2] - y[2]);

        parents = new int[v + 1];
        for (int i = 1; i < v+1; i++) {
            parents[i] = i;
        }

        // 간선 돌기
        for (int i = 0; i < e; i++) {

            // a-> b 로 가는 각 최종값이 다른 경우
            if (find(data[i][0]) != find(data[i][1])) {
                union(data[i][0],data[i][1]);
                weightSum+=data[i][2];
            }

        }

        return weightSum;
    }

    public static void union(int a, int b) {
        int ap = find(a);
        int bp = find(b);

        if (ap != bp) {
            parents[ap] = bp;
        }
    }

    public static int find(int a) {

        if (a == parents[a]) {
            return a;
        }

        return parents[a] = find(parents[a]);
    }


}











