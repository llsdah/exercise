package org.example.algorithm.minimunspanningtree.practice;

import java.util.ArrayList;
import java.util.PriorityQueue;

/**
 * Y개 건물 과 E 개의 도로로 구성된 도시
 * 도로는 양방향이고 연결된 도로는 유지하는데 비용든다.
 *
 * 새롭게 도시 계획을 개편하며 기존의 도시를 두개의 도시로 분할해서 관리하고자 한다.
 * 도시에는 하나의 이상의 건물이 있어야하고,
 * 도시 내의 임의의 두 건물은 도로를 통해 이동 간으해야한다.
 * 두 건물 간 도로가 직접 연결이 되지 않고 다른 건물을 통해서 이동해도 가능하다.
 * 개롭게 개편하는 도시 계획에 따라 구성했을 때 최소한의 도로 유지비 비용 계산 프로그램작성
 *
 *
 *  v : 7
 *  e : 12
 *  data : {{1,2,3},{1,3,2},{1,6,2},{2,5,2},
 *  {3,2,1},{3,4,4,},{4,5,3},{5,1,5},
 *  {6,4,1},{6,5,3,},{6,7,4},{7,3,6}}
 *  출력 8
 *
 *
 */
public class Practice_2 {

    static class Node {
        int to ;
        int weight;

        public Node(int to, int weight) {
            this.to = to;
            this.weight = weight;
        }
    }

    static ArrayList<Node>[] graph;
    static boolean[] visited;

    private static void solution(int v, int e, int[][] data) {
        graph = new ArrayList[v+1];
        for (int i = 0; i < v + 1; i++) {
            graph[i] = new ArrayList<>();
        }

        // 양방향
        for (int i = 0; i < e; i++) {
            graph[data[i][0]].add(new Node(data[i][1],data[i][2]));
            graph[data[i][1]].add(new Node(data[i][0],data[i][2]));
        }

        visited = new boolean[v + 1];

        // prim 알고리즘 사용
        System.out.println(prim());

    }

    public static int prim() {
        PriorityQueue<Node> pq = new PriorityQueue<>((x,y)-> x.weight - y.weight);
        pq.offer(new Node(1,0));

        int weightSum = 0;
        int maxWeight = 0;

        while(! pq.isEmpty()) {
            Node cur = pq.poll();
            if (visited[cur.to]) {
                continue;
            }

            visited[cur.to] = true;
            weightSum += cur.weight;

            // 최대 값 뺴야 합니다.
            maxWeight = Math.max(maxWeight, cur.weight);

            for (int i = 0; i < graph[cur.to].size(); i++) {
                Node abjNode = graph[cur.to].get(i);
                if (visited[abjNode.to]) {
                    continue;
                }
                pq.offer(abjNode);
            }

        }

        return weightSum - maxWeight;
    }


    public static void main(String[] args) {
        int v = 7;
        int e = 12;
        int[][] data = {
                {1,2,3},{1,3,2},{1,6,2},{2,5,2},
                {3,2,1},{3,4,4},{4,5,3},{5,1,5},
                {6,4,1},{6,5,3},{6,7,4},{7,3,6}
        };

        solution(v,e,data);
    }
}
