package org.example.algorithm.minimunspanningtree;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.PriorityQueue;

public class Prim {

    static class Node {
        int to;
        int weight;

        public Node(int to, int weight) {
            this.to = to;
            this.weight = weight;
        }
    }

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

        System.out.println(prim(graph,v,e));
    }

    private static int prim(int[][] data, int v, int e) {
        int weight = 0;

        ArrayList<ArrayList<Node>> graph = new ArrayList<ArrayList<Node>>();

        for (int i = 0; i < v + 1; i++) {
            graph.add(new ArrayList<>());
        }

        // 양방향
        for (int i = 0; i < e; i++) {
            graph.get(data[i][0]).add(new Node(data[i][1], data[i][2]));
            graph.get(data[i][1]).add(new Node(data[i][0], data[i][2]));
        }

        boolean[] visit = new boolean[v+1];
        PriorityQueue<Node> pq = new PriorityQueue<>((x, y) -> x.weight - y.weight);
        pq.add(new Node(1,0));

        int cnt = 0;
        while (!pq.isEmpty()) {
            Node cur = pq.poll();
            cnt += 1;

            if (visit[cur.to]) {
                continue;
            } else {
                visit[cur.to] = true;
            }

            weight += cur.weight;

            if (cnt == v) {
                return weight;
            }

            for (int i = 0; i < graph.get(cur.to).size(); i++) {
                Node abjNode = graph.get(cur.to).get(i);
                if (visit[abjNode.to]) {
                    continue;
                }
                pq.offer(abjNode);
            }
        }

        return weight;
    }
}
