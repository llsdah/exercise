package org.example.programmers.three;

    import java.util.*;
public class 가장먼노드 {
    class Solution {

        class Node {
            int num;
            List<Integer> links;
            int cnt = 0;//Integer.MAX_VALUE;
            boolean visited;

            Node(int num){
                this.num = num;
                links = new ArrayList<>();
            }
            public void count(int num){
                this.cnt = num + 1;
            }
            public int get(){
                return cnt;
            }
            public void set(int n){
                this.cnt = n;
            }
            public void link(int n){
                links.add(n);
            }

            public void visit(){
                this.visited = true;
            }

        }

        public int solution(int n, int[][] edge) {
            int answer = 0;
            List<Node> list = new ArrayList<>();
            for (int i = 0; i< n;i++){
                list.add(new Node(i));
                if(i == 0) {
                    list.get(i).set(0);
                    list.get(i).visit();
                }

            }

            for (int i = 0; i < edge.length; i ++ ){
                int[] temp = edge[i];
                Node o = list.get(temp[0]-1);
                Node d = list.get(temp[1]-1);
                o.link(temp[1]-1);
                d.link(temp[0]-1);
            }

            Queue<Node> que = new LinkedList<>();
            que.add(list.get(0));

            while(!que.isEmpty()) {
                Node node = que.poll();
                for(int num : node.links) {
                    Node no = list.get(num);
                    if(no.visited) {
                        continue;
                    }
                    //int min = Math.min( node.cnt + 1, no.cnt );
                    no.set(node.cnt + 1);
                    no.visit();
                    que.add(no);
                }
            }

            int max = -1;
            for(Node no : list) {
                if ( no.cnt > max ) {
                    answer = 1;
                    max = no.cnt;
                } else if ( no.cnt == max ){
                    answer ++;
                }
                //System.out.println("no "+ (no.num +1 ) + ", cnt : "+no.cnt);
            }

            return answer;
        }
    }
}
