package org.example.nonlinear.graph.practice;

/**
 * 하나의 그래프를 두개의 그래프로 변경이 가능한지 확인하는 프로그램 생성
 * 분리 조건 : 인접하지 않은 노드끼리 분기
 *
 * 모든 노드는 연결되어 있다.
 *
 *
 * 그래프 : {{1,3}{0,2},{1,3},{0,2}} => true
 * 각 노드들의 연결된 노드를 표현한것
 * 즉, 0번 인덱스는 1,3 의 값을 가지므로 0번 노드는 1과 3번 노드에 연결되어있다.
 *
 *
 * 그래프 : {{1,2,3}{0,2},{0,1,3},{0,2}} => false
 *
 */

public class Practice_3 {

    public static void main(String[] args) {

        int[][] graph = {{1,3},{0,2},{1,3},{0,2}};
        solution(graph);
        graph = new int[][]{{1,2,3},{0,2},{0,1,3},{0,2}};
        solution(graph);

    }

    private static void solution(int[][] graph) {
        int[] flags = new int[graph.length];

        if (checkSplit(graph, flags, 1, 0 ) == true) {
            System.out.println("true");
        } else {
            System.out.println(false);
        }

    }

    private static boolean checkSplit(int[][] graph, int[] flags, int flag, int node) {
        if (flags[node] != 0) { // 내가 가진 값과 동일하면 상관없음, 다르면 인접한 값이다. 문제 발생
            return flags[node] == flag;
        }

        flags[node] = flag;

        for (int adjacentNode : graph[node]) {
            if (!checkSplit(graph, flags, -flag, adjacentNode)){ // 인접한것을 반대 부호로
                return false;
            }
        }

        return true;

    }

}
