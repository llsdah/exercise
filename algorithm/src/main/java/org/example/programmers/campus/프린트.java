package org.example.programmers.campus;

import java.util.*;
public class 프린트 {

    public int solution_my(int[] priorities, int location) {
        int answer = 0;
        int n = priorities.length;

/* 1. 실행 대기 큐(Queue)에서 대기중인 프로세스 하나를 꺼냅니다.
2. 큐에 대기중인 프로세스 중 우선순위가 더 높은 프로세스가 있다면 방금 꺼낸 프로세스를 다시 큐에 넣습니다.
3. 만약 그런 프로세스가 없다면 방금 꺼낸 프로세스를 실행합니다.
  3.1 한 번 실행한 프로세스는 다시 큐에 넣지 않고 그대로 종료됩니다. **/
        Queue<Node> que = new LinkedList<>();
        List<Node> list = new ArrayList<>();
        for (int i =0; i <n;i++) {
            que.offer(new Node(i,priorities[i]));
            list.add(new Node(i,priorities[i]));
        }
        while(!que.isEmpty()) {
            Node no = que.poll();
            boolean flag = true;
            for (int i = 1; i <list.size();i++) {
                if (no.pri < list.get(i).pri) {
                    flag = false;
                    break;
                }
            }
            if (!flag) {
                que.offer(no);
                list.remove(0);
                list.add(no);
            } else {
                answer++;
                if ( no.idx != location ) {
                    list.remove(0);
                }else {
                    break;
                }
            }
        }

        return answer;
    }

    class Node {
        int idx;
        int pri;

        Node(int idx, int pri){
            this.idx = idx;
            this.pri = pri;
        }
    }

    class Paper {
        boolean mine;
        int p;
        Paper(int p, boolean b) {
            this.p =p;
            this.mine =b;
        }

    }
    public int solution(int[] priorities, int location) {
        List<Paper> list = new LinkedList<>();
        for (int i = 0 ; i<priorities.length;i++) {
            list.add(new Paper(priorities[i], i == location));
        }

        int answer = 0;

        while(!list.isEmpty()) {

            Paper now = list.remove(0);

            boolean max = true;
            for (Paper p : list) {
                if (now.p < p.p ) {
                    max = false;
                    break;
                }
            }

            if (!max) {
                list.add(now);
                continue;
            }
            answer ++;
            if(now.mine) return answer;
        }

        return answer;

    }
}
