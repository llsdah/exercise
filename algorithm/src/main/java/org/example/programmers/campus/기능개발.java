package org.example.programmers.campus;
import java.util.*;

public class 기능개발 {
    public int[] solution_my(int[] progresses, int[] speeds) {
        int[] answer = {};
        String str = "";
        List<Integer> list = new ArrayList<>();

        int yes = (100 - progresses[0]) / speeds[0];
        yes = yes + ((100 - progresses[0]) % speeds[0] > 0 ? 1 : 0);
        int cnt = 1;
        for (int i = 1; i <speeds.length;i++) {
            int pro = progresses[i];
            int spe = speeds[i];


            int now = (100 - pro) / spe;
            now = now + ((100 - pro) % spe > 0 ? 1 : 0);

            if ( now > yes ) {
                yes = now;
                list.add(cnt);
                cnt = 1;
            }  else {
                cnt ++;
            }
            if ( i == speeds.length -1 ){
                list.add(cnt);
            }
        }
        //System.out.println(list);
        answer = new int[list.size()] ;
        for (int i = 0 ; i < list.size();i++) {
            answer[i] = list.get(i);
        }

        return answer;
    }

    public int[] solution(int[] progresses, int[] speeds) {

        Queue<Integer> que = new LinkedList<>();
        // 수행 날짜
        for (int i = 0 ; i < progresses.length; i++) {
            float p = progresses[i];
            float s = speeds[i];
            // 올림처리
            int day = (int)Math.ceil((100-p)/s);
            que.offer(day);
        }
        Queue<Integer> answer = new LinkedList<>();
        int d = que.poll();
        int count = 1;
        while(!que.isEmpty()) {
            int n = que.poll();

            if(d >= n) {
                count ++;
                continue;
            }
            answer.offer(count);
            count = 1;
            d = n;
        }

        answer.offer(count);
        return answer.stream().mapToInt(Integer::intValue).toArray();
    }

}







