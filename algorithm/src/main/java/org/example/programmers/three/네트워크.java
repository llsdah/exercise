package org.example.programmers.three;

import java.util.*;

public class 네트워크 {

    class Solution {
        public int solution(int n, int[][] computers) {
            int answer = 0;

            List<Node> list = new LinkedList<>();
            for(int i =0; i < n; i++){
                Node node = new Node(i);
                list.add(node);
            }

            // 연결 작업
            for(int i = 0; i < computers.length; i++) {
                for (int k = 0; k<computers[0].length;k++){
                    if(i == k ) continue;
                    if ( computers[i][k] == 1 ) {
                        list.get(i).link(list.get(k));
                        list.get(k).link(list.get(i));
                    }
                }
            }

            int cnt = 0;

            for (int i =0; i< n;i++) {
                Node node = list.get(i);

                if( node.isVisit() ) {
                    continue;
                }
                node.visit();
                cnt++;
                Queue<Node> que = new LinkedList<>();

                que.offer(node);

                while(!que.isEmpty()) {
                    Node no = que.poll();

                    List<Node> noList = no.links;

                    for(Node item : noList) {
                        if(list.get(item.num).isVisit()) {
                            continue;
                        }else {
                            que.offer(item);
                            list.get(item.num).visit();
                            item.visit();
                        }
                    }

                }

            }

            return cnt;
        }
    }

    class Node {
        int num;
        boolean visit;
        List<Node> links;

        public Node(int num) {
            this.num = num;
            links = new LinkedList<>();
        }

        public void link(Node n) {
            links.add(n);
        }

        public void visit() {
            this.visit = true;
        }

        public boolean isVisit(){
            return this.visit;
        }

    }
}
