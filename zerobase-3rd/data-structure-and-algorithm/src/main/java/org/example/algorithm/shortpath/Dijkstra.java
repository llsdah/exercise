package org.example.algorithm.shortpath;

import java.util.ArrayList;
import java.util.PriorityQueue;

public class Dijkstra {

    static class Node {
        int to;
        int weight;

        public Node(int to, int weight) {
            this.to = to;
            this.weight = weight;
        }
    }
    // 다익스트라 기본 구현
    private static void dijkstra(int v, int[][] data, int start) {
        ArrayList<ArrayList<Node>> graph  = new ArrayList<>();
        for (int i = 0; i < v+1; i++) {
            graph.add(new ArrayList<>());
        }
        // 그래프 구성
        for (int i = 0; i < data.length; i++) {
            graph.get(data[i][0]).add(new Node(data[i][1], data[i][2]));
        }
        // v + 1
        int[] dist = new int[v+1];

        //
        for (int i = 1; i < dist.length; i++) {
            dist[i] = Integer.MAX_VALUE;
        }

        dist[start] = 0;
        // 방문 배열
        boolean[] visited = new boolean[v + 1];

        for (int i = 0; i < v; i++) {
            int mindist = Integer.MAX_VALUE;
            int curIdx = 0;
            for (int j = 1; j < v+1; j++) {
                if (!visited[j] && dist[j] < mindist){
                    mindist = dist[j];
                    curIdx = j;
                }
            }

            visited[curIdx] = true;

            // 인접 노드
            for (int j = 0; j < graph.get(curIdx).size(); j++) {
                // 인접노드 하나씩 가져 와서
                Node abjNode = graph.get(curIdx).get(j);
                // 원래 배열에
                if (dist[abjNode.to] > dist[curIdx] + abjNode.weight) {
                    dist[abjNode.to] = dist[curIdx] + abjNode.weight;
                }
            }
        }

        for (int i = 1; i < v + 1 ; i++) {
            if (dist[i] == Integer.MAX_VALUE) {
                System.out.print("INF ");
            } else {
                System.out.print(dist[i] + " ");
            }
        }
        System.out.println();
    }


    private static void dijkstra_queue(int v, int[][] data, int start) {
        ArrayList<ArrayList<Node>> graph  = new ArrayList<>();
        for (int i = 0; i < v+1; i++) {
            graph.add(new ArrayList<>());
        }
        // 그래프 구성
        for (int i = 0; i < data.length; i++) {
            graph.get(data[i][0]).add(new Node(data[i][1], data[i][2]));
        }
        // v + 1
        int[] dist = new int[v+1];

        //
        for (int i = 1; i < dist.length; i++) {
            dist[i] = Integer.MAX_VALUE;
        }

        dist[start] = 0;

        // 우선순위에 weight 기준으로 정렬 오름차순
        PriorityQueue<Node> pq = new PriorityQueue<>((x, y)-> x.weight - y.weight);
        pq.offer(new Node(start, 0));

        while(!pq.isEmpty()) {
            Node curNode = pq.poll();

            if (dist[curNode.to] < curNode.weight) {
                continue;
            }

            for (int i = 0; i < graph.get(curNode.to).size(); i++) {
                Node abjNode = graph.get(curNode.to).get(i);

                if (dist[abjNode.to] > curNode.weight + abjNode.weight) {
                    dist[abjNode.to] = curNode.weight + abjNode.weight;
                    pq.offer(new Node(abjNode.to, dist[abjNode.to]));
                }
            }

        }


        for (int i = 1; i < v + 1 ; i++) {
            if (dist[i] == Integer.MAX_VALUE) {
                System.out.print("INF ");
            } else {
                System.out.print(dist[i] + " ");
            }
        }
        System.out.println();
    }


    public static void main(String[] args) {
        int[][] data = {
                {1,2,2},
                {1,3,3},
                {2,3,4},
                {2,4,5},
                {3,4,5},
                {5,1,1}
        };
        // 다익스트라 기본 구현
        System.out.println("standard");
        dijkstra(5,data,1);
        System.out.println("priority queue");
        // 다익스트라 -우선순위 큐
        dijkstra_queue(5, data , 1);
    }

}
