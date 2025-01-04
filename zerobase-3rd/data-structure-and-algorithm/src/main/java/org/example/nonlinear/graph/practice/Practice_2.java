package org.example.nonlinear.graph.practice;

import java.util.ArrayList;
import java.util.Stack;

/**
 * 주어진 그래프에서 시작 노드에서 끝 노드로 가는 길이 있는지 확인하는 프로그램
 * true / false
 *
 * 노드 수 : 3
 * 간성 정보 : {{0,1},{1,2},{2,0}}
 * 출발 노드 = 0
 * 종착 노드 = 2
 * 출력 : true
 *
 */
public class Practice_2 {

    public static void main(String[] args) {
        int n = 3;
        int[][] edges = {{0,1},{1,2},{2,0}};
        int source = 0;
        int dest = 2;
        solution(n, edges,source,dest);

        n = 6;
        edges = new int[][] {{0,1},{0,2},{3,5},{5,4},{4,3}};
        source = 0;
        dest = 5;
        solution(n, edges,source,dest);

    }

    private static void solution(int n, int[][] edges, int source, int dest) {
        MyGraphList graph = new MyGraphList(n);

        for (int i = 0; i < n; i++) {
            graph.addVertex(i);
        }

        for (int i = 0; i < edges.length; i++) {
            graph.addEdge(edges[i][0], edges[i][1]);
        }
        ArrayList<Integer> visitedItem = new ArrayList<>();

        dfs(graph, 0, visitedItem);

        if (visitedItem.contains(source) && visitedItem.contains(dest)) {
            System.out.println("true");
        } else {
            System.out.println("false");
        }

    }

    public static void dfs(MyGraphList graph, int id, ArrayList<Integer> visitedItem) {
        boolean[] visited = new boolean[graph.vertices.length];
        Stack<Integer> stack = new Stack<>();

        stack.push(id);
        visited[id] = true;

        while (!stack.isEmpty()) {
            int curId = stack.pop();

            visitedItem.add(curId);

            Node cur = graph.adjList[curId];
            while (cur != null) {
                if (visited[cur.id] == false) {
                    stack.push(cur.id);
                    visited[cur.id] = true;
                }
                cur = cur.next;
            }
        }


    }

    static class Node {
        int id;
        Node next;

        public Node(int id, Node next) {
            this.id = id;
            this.next = next;
        }
    }


    static class MyGraphList {
        int[] vertices;
        Node[] adjList;
        int elemCnt;

        public MyGraphList() {};

        public MyGraphList(int size) {
            this.vertices = new int[size];
            this.adjList = new Node[size];
            this.elemCnt = 0;
        }

        public boolean isFull() {return this.elemCnt == this.vertices.length;}

        public void addVertex(int data) {
            if (isFull()) {
                System.out.println("data full = " + data);
                return;
            }
            this.vertices[this.elemCnt++] = data;
        }

        public void addEdge(int x, int y) {
            this.adjList[x] = new Node(y,this.adjList[x]);
            this.adjList[y] = new Node(x,this.adjList[y]);
        }



    }

}
