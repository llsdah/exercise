package org.example.programmers.two;

import java.util.*;
public class 프린터 {

    public int solution(int[] priorities, int location) {
        int answer = 0;

        Map<Integer,Integer> map = new HashMap<>();
        List<Integer> list = new ArrayList<>();

        Queue<Integer> que = new LinkedList<>();
        for (int i = 0; i < priorities.length; i++) {
            map.put(i, priorities[i]);
            que.offer(i);
        }


        while(que.size() != 0){
            int idx = que.poll();

            boolean flag = true;
            // 내 인덱스는 안돌아 + 이미 빠진 것도 안돌아 + 만약 우선 순위가 낮다면 빼
            for ( int i = 0; i < priorities.length; i ++) {
                if(idx == i || map.get(i) == -1 ) continue;
                if ( map.get(idx) < map.get(i) ) {
                    que.offer(idx);
                    flag = false;
                    break;
                }
            }

            if ( flag ) {
                map.put(idx, -1);
                list.add(idx);
            }

        }

        for(int i = 0; i<list.size(); i++) {
            if (list.get(i)==location) {
                answer = i+1;
                break;
            }
        }

        return answer;
    }
}
