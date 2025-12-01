package org.example.programmers.campus;

import java.util.*;

public class 가장먼노드 {
    public int solution_my(int n, int[][] edge) {
        int answer = 0;

        List<Node> list = new LinkedList<>();
        boolean[] visit = new boolean[n];
        int c = edge.length;
        int r = edge[0].length;

        for (int i =0; i < n; i++) {
            list.add(new Node(i));
        }
        for(int[] items : edge) {
            list.get(items[0]-1).add(list.get(items[1]-1));
            list.get(items[1]-1).add(list.get(items[0]-1));
        }

        Queue<Node> que = new LinkedList<>();
        que.add(list.get(0));
        while(!que.isEmpty()) {
            Node node = que.poll();
            if(!visit[node.idx]) {
                visit[node.idx] = true;
                node.cnt(node.depth);
            }
            //System.out.println("size() : "+node.list.size());

            for (int i =0; i < node.list.size();i++){
                Node next = list.get(node.list.get(i).idx);
                if(!visit[next.idx]) {
                    visit[next.idx] = true;
                } else {
                    continue;
                }
                list.get(next.idx).cnt(node.depth);
                //System.out.println("idx : "+list.get(next.idx).idx+", depth : "+list.get(next.idx).depth);

                que.offer(list.get(next.idx));
            }

        }

        int max = 0;

        for (int i = 0; i < list.size();i++) {
            //System.out.println(list.get(i).idx + ", depth : " + list.get(i).depth +", max : "+max);
            if(max < list.get(i).depth) {
                max =list.get(i).depth;
                answer =1;
            } else if (max == list.get(i).depth) {
                answer ++;
            }
        }

        return answer;
    }

    class Node {
        int idx;
        int depth;
        boolean visit = false;
        List<Node> list;

        Node(int i){
            this.idx = i;
            list = new ArrayList<>();
        }

        public void cnt(int n){
            depth = n+1;
        }
        public void add(Node n){
            list.add(n);
        }

    }

    public int solution(int n, int[][] edge) {
        List<Node> list = new ArrayList<>();
        for (int i = 0; i < edge.length; i++) list.add(new Node(i+1));

        for (int[] e : edge) {
            Node n1 = list.get(e[0]-1);
            Node n2 = list.get(e[1]-1);
            n1.list.add(n2);
            n2.list.add(n1);
        }
        int max = 0;
        Queue<Node> que = new LinkedList<>();
        Node f = list.get(0);
        f.visit = true;
        que.offer(f);

        while(!que.isEmpty()) {
            Node now = que.poll();

            for (Node node : now.list) {
                if (node.visit) continue;
                node.visit = true;
                node.depth = now.depth + 1;
                que.offer(node);

                max = Math.max(max, node.depth);
            }
        }
        int answer = 0;
        for (Node node : list) {
            if (node.depth == max) answer ++;
        }
        return answer;
    }
}








