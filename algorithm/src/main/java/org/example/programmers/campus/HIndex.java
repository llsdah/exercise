package org.example.programmers.campus;

import java.util.*;

public class HIndex {
    public int solution_my(int[] citations) {
        int answer = 0;

        Arrays.sort(citations);
        // 논문수 n
        int n = citations.length;
        // h 번이상 인용된 논문이 h편이상 이고 나머지가 h번이하
        int sum = 0;
        for (int item : citations) sum += item;
        // 평균 인용수
        int mid = (sum/n) +1;

        while(true) {
            // 평균인용수 이상 횟수
            int cnt = 0;
            for (int i = n-1; i >= 0 ;i--) {
                //    System.out.println(mid +": "+citations[i]);

                if (mid <= citations[i]) {
                    //  System.out.println(citations[i]);
                    cnt ++;
                } else {
                    break;
                }
            }
            // if (cnt == 0) break;

            if ( cnt >= mid && n-cnt <= mid) {
                return mid;
            } else if (cnt < mid) {
                mid --;
            } else if(n -cnt > mid ) {
                mid ++;
            }
        }

        //return answer;
    }
    public int solution(int[] citations) {
        Arrays.sort(citations);
        for (int i = 0; i < citations.length; i++) {
            int h = citations.length - 1;
            if ( citations[i] >= h) return h;
        }
        return 0;
    }
}
