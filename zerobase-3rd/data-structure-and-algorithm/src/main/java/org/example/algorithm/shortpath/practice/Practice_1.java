package org.example.algorithm.shortpath.practice;

import java.util.ArrayList;
import java.util.PriorityQueue;

/**
 * 2차원 data 그래프
 * 무방향 간선에 가중치 값이 있는 그래프
 * 1번 정점에서 N번정점으로 최단 경로로 이동하려고하는데
 * 두정점을 경유해 가려고한다.
 * 1번 정점에서 출발하여 두정점을 경유 N번 정점으로 가는 최단 경로를 구해라
 *
 *
 * data = {{1,2,3},{1,3,5},{1,4,4},{2,3,3},{2,4,5},{3,4,1}}
 * start = 1
 * n = 4
 * via1 = 2
 * via2 = 3
 *
 * output = 7
 *
 */
public class Practice_1 {

    // 무방향 경유
    // start -> A -> B -> end , start -> B -> A -> end  비교
    static ArrayList<ArrayList<Node>> graph;
    final static int INF = 10000000;

    static class Node {
        int to;
        int weight;

        public Node(int to, int weight) {
            this.to = to;
            this.weight = weight;
        }
    }

    private static int solution(int[][] data, int v, int via1, int via2, int start, int n) {

        int case1 = 0;
        int case2 = 0;

        graph = new ArrayList<>();

        for (int i = 0; i < v + 1; i++) {
            graph.add(new ArrayList<>());
        }

        for (int i = 0; i < data.length; i++) {
            graph.get(data[i][0]).add(new Node(data[i][1],data[i][2]));
        }

        // case1 start -> A -> B -> end
        case1 += dijkstra(v, start, via1);
        case1 += dijkstra(v, via1, via2);
        case1 += dijkstra(v, via2, n);

        // case2 start -> B -> A -> end
        case2 += dijkstra(v, start, via2);
        case2 += dijkstra(v, via2, via1);
        case2 += dijkstra(v, via1, n);

        int answer = Math.min(case1, case2);

        return answer;
    }

    public static int dijkstra(int v, int start, int end) {

        PriorityQueue<Node> pq = new PriorityQueue<>((x,y) -> x.weight - y.weight);
        pq.offer(new Node(start, 0));

        int[] dist = new int[v+1];
        for (int i = 0; i < v + 1; i++) {
            dist[i] = INF;
        }

        dist[start] = 0;
        // 이번엔 방문배열
        boolean[] visited = new boolean[v + 1];

        while (!pq.isEmpty()) {
            Node curNode = pq.poll();

            if (visited[curNode.to]) {
                continue;
            }

            visited[curNode.to] = true;

            for (int i = 0; i < graph.get(curNode.to).size(); i++) {
                Node abjNode = graph.get(curNode.to).get(i);

                if (!visited[abjNode.to] && dist[abjNode.to] > curNode.weight + abjNode.weight) {
                    dist[abjNode.to] = curNode.weight + abjNode.weight;
                    pq.offer(new Node(abjNode.to, dist[abjNode.to]));
                }

            }

        }

        return dist[end];
    }


    public static void main(String[] args) {
        int[][] data = {
                {1,2,3},
                {1,3,5},
                {1,4,4},
                {2,3,3},
                {2,4,5},
                {3,4,1}
        };

        int v = 4;
        int via1 = 2;
        int via2 = 3;
        int start = 1;
        int n = 4;

        System.out.println(solution(data,v,via1,via2,start,n));

    }

}











