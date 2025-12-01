package org.example.programmers.campus;
import java.util.*;
public class 숫자게임 {

    public int solution_my(int[] A, int[] B) {
        int answer = 0;

        Queue<Node> a = new PriorityQueue<>();
        Queue<Node> b = new PriorityQueue<>();

        for (int i = 0 ; i< A.length;i++) {
            a.add(new Node(i,A[i]));
            b.add(new Node(i,B[i]));
        }
        List<Node> list = new ArrayList<>();
        while(!b.isEmpty()) {
            list.add(b.poll());
        }
        //System.out.println(a.peek().idx+":"+a.peek().num);

        while(true) {
            Node an = a.poll();
            boolean flag = false;
            if (list.size() == 0 ) break;
            for (int i = 0; i <list.size();i++) {
                // 최소로 이김
                if(an.num < list.get(i).num) {
                    list.remove(i);
                    flag = true;
                    answer ++;
                    break;
                }
            }
            if (!flag) break;

        }

        return answer;
    }

    class Node implements Comparable{
        int idx;
        int num;
        Node (int i, int n) {
            idx = i;
            num = n;
        }

        public int compareTo(Object o) {
            return num -((Node)o).num;
        }

    }

    public int solution(int[] A, int[] B) {
        int answer = 0;
        Arrays.sort(A);
        Arrays.sort(B);
        int idx = B.length -1;
        for (int i = A.length-1;i >=0; --) {
            if (A[i] < B[idx]) {
                idx --;
                answer ++;
            }
        }
        return answer;
    }

}
