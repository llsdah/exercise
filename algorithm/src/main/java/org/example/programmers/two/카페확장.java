package org.example.programmers.two;

    import java.util.*;
public class 카페확장 {
    class Solution {
        public int solution(int[] menu, int[] order, int k) {
            int answer = 0;

            // 동시에 손님이 머무는 시간
            int[][] p = new int[order.length][2];
            p[0][0] = k;
            p[0][1] = k + menu[order[0]];

            for (int i = 1; i < order.length;i++) {
                p[i][0] = k * (i+1);
                int max = 0;
                for (int j = 0 ; j < i ; j++) {
                    max = Math.max(max, p[j][1]);
                }

                p[i][1] = p[i][0] > max ? p[i][0] + menu[order[i]]
                        : max + menu[order[i]];
                //System.out.println(Arrays.toString(p[i]));
            }

            int[] num = new int[p[order.length-1][1]+1];
            for (int i = 0; i<p.length;i++) {
                for (int j = p[i][0]; j < p[i][1]; j ++) {
                    num[j] ++;
                }
            }
            answer = Arrays.stream(num).max().getAsInt();

            return answer;
        }
    }

}
