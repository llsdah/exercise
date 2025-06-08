package org.example.algorithm.practice2;

import java.util.PriorityQueue;

/**
 * 2차원 정수열 배열, 평명좌표 점
 *
 * 모든 점의 맨해튼 거리 최소 비용으로 연결할때 비용 출력
 * 맨해튼 거리 = |x1-x2|+|y1-y2|
 *
 *
 * input {{0,0},{2,2},{3,10},{5,2},{7,0}}
 * output 20
 *
 *
 * input {{-4,1},{3,12},{-2,5}}
 * output 18
 */
public class Practice_5 {

    static class Node {
        int idx;
        int weight;

        public Node(int idx, int weight) {
            this.idx = idx;
            this.weight = weight;
        }
    }

    static boolean[] visited;

    // 크루스칼 혹은 프림
    private static void solution(int[][] points) {
        visited = new boolean[points.length];

        PriorityQueue<Node> pq = new PriorityQueue<>((x,y)-> x.weight -y.weight);

        pq.offer(new Node(0,0));
        int weightSum = 0;
        int cnt = 0;

        while (cnt < points.length) {
            Node cur = pq.poll();

            if (visited[cur.idx]) {
                continue;
            }

            visited[cur.idx] = true;
            weightSum += cur.weight;
            cnt++;

            for (int i = 0; i < points.length; i++) {
                if (i == cur.idx) {
                    continue;
                }

                int distance = Math.abs(points[i][0] - points[cur.idx][0])
                        + Math.abs(points[i][1] - points[cur.idx][1]);
                pq.offer(new Node(i, distance));
            }

        }

        System.out.println("weightSum = " + weightSum);
    }

    public static void main(String[] args) {
        int[][] points = {{0,0},{2,2},{3,10},{5,2},{7,0}};
        solution(points);
        points = new int[][]{{-4,1},{3,12},{-2,5}};
        solution(points);

    }
}
