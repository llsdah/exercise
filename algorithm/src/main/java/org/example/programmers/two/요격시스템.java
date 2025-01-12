package org.example.programmers.two;
import java.util.*;
public class 요격시스템 {

    public int solution(int[][] targets) {
        int answer = 0;

        // 내림차순 정렬 (targets[a][b] 기준)
        Arrays.sort(targets, (o1, o2) -> Integer.compare(o1[1], o2[1]));

        // PriorityQueue 정의 (data[a][b] 기준 내림차순)
        PriorityQueue<int[]> pq = new PriorityQueue<>((o1, o2) -> Integer.compare(o2[1], o1[1]));
        Queue<int[]> que = new LinkedList<>();

        int point = 0;
        for(int[] target : targets) {
            //System.out.println(target[0]+": "+target[1]);

            if(point <= target[0]) {
                point = target[1];
                answer ++;
            }

        }

        return answer;
    }
}
