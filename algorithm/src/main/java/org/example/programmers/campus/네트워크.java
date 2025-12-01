package org.example.programmers.campus;

import java.util.*;
public class 네트워크 {

    boolean[] visit;
    public int solution_my(int n, int[][] computers) {
        int answer = 0;
        List<Node> nodes = new LinkedList<>();
        Stack<Node> st = new Stack<>();
        visit = new boolean[n];
        for (int i = 0; i <n;i++) {
            Node node = new Node(i);
            for (int k = 0; k <n;k++) {
                if (i == k) continue;
                if (computers[i][k] == 1) {
                    node.add(new Node(k));
                }
            }
            nodes.add(node);
            st.push(node);
        }



        while(!st.isEmpty()) {
            Node node = st.pop();
            //            System.out.println(Arrays.toString(visit));
            if (!visit[node.idx] ) {
                //System.out.println("node.idx : "+node.idx);
                visit[node.idx] = true;
                answer++;
            }
            //System.out.println(Arrays.toString(visit));
            //System.out.print(node.idx+" : ");

            for(int i = 0; i< node.list.size();i++) {
                if (visit[node.list.get(i).idx]) continue;
                //System.out.print(node.list.get(i).idx+", ");

                visit[node.list.get(i).idx] = true;
                st.push(nodes.get(node.list.get(i).idx));
            }
            //System.out.println();

        }


        return answer;
    }

    class Node {
        int idx;
        List<Node> list;
        boolean visit;

        Node(int i) {
            this.idx = i;
            this.list = new ArrayList<>();
        }

        public void add(Node i) {
            list.add(i);
        }

        public void visit(){
            this.visit = true;
        }

    }

    public int solution(int n, int[][] computers) {
        int answer = 0;
        boolean[] visited = new boolean[computers.length];
        for (int i = 0 ; i <computers.length;i++) {
            if(visited[i]) continue;
            answer++;
            visitAllComputer(computers,visited,i);
        }
        return answer;
    }
    void visitAllComputer(int[][] c, boolean[] v, int n){
        Queue<Integer> que = new LinkedList<>();
        que.offer(n);
        while(!que.isEmpty()) {
            int item = que.poll();
            v[item] = true;
            for (int i = 0; i < c[item].length;i++) {
                if (v[i]) continue;
                if (c[item][i] == 1) que.offer(c[item][i]);
            }
        }


    }
}
