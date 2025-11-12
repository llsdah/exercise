package org.example.programmers.campus;
import java.util.*;
public class 주식가격 {

    public int[] solution(int[] prices) {
        int n = prices.length;
        int[] answer = new int[n];

        Queue<Integer> que = new LinkedList<>();
        for (int i = 0; i < n; i++) {
            que.add(prices[i]);
        }
        int idx = 0;
        while(!que.isEmpty()) {
            int num = que.poll();
            int cnt = 0;
            for (int i = idx+1; i < n; i++) {
                if(num <= prices[i]) cnt ++;
                else {
                    cnt++;
                    break;
                }
            }
            answer[idx] = cnt;
            idx ++;
        }

        answer[answer.length-1] =0;
        return answer;
    }
}
