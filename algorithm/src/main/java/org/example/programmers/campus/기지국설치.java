package org.example.programmers.campus;

import java.util.*;

public class 기지국설치 {
    public int solution_my(int n, int[] stations, int w) {
        int answer = 0;

        // [실행] 버튼을 누르면 출력 값을 볼 수 있습니다.
        //System.out.println("Hello Java");
        // 포괄 범위
        int[] now = new int[2];
        now[0] = 1;
        now[1] = 1;//now[0] + 2*w;

        int idx = 0;
        while(now[1] <= n) {
            //System.out.println(answer+", idx : "+idx+", now[0] : "+now[0]+", now[1] : "+now[1]);
            if (idx < stations.length) {
                int min = Math.max(stations[idx]-w,1);
                int max = Math.min(stations[idx]+w,n);
                if (now[0] >= min && now[1] <= max ) {
                    idx ++;
                    now[0] = max+1;
                    now[1] = max+1;
                } else if ( now[0] < min ) {
                    answer ++;
                    now[1] = now[0]+2*w +1;
                    now[0] = now[1];
                }
            } else {
                answer ++;
                now[1] = now[0]+2*w +1;
                now[0] = now[1];
            }

        }

        return answer;
    }

    public int solution_old(int n, int[] stations, int w) {
        int answer = 0;
        // [실행] 버튼을 누르면 출력 값을 볼 수 있습니다.
        // System.out.println("Hello Java");
        Queue<Integer> pq = new PriorityQueue<>();
        int[] conn = new int[n+1];
        int idx = 0;
        for (int i = 1; i<=n; i++) {
            int min = Math.max(stations[idx]-w,1);
            int max = Math.min(stations[idx]+w,n);
            int num = i+2*w;
            // 미연결
            if (conn[i]==0) {
                if ( i < min || i > max) {
                    answer++;
                    i=num;
                    //System.out.println("i : "+i );
                    //break;
                } else if(idx < stations.length-1) {
                    i=max;
                    idx++;
                }
            }
        }

        return answer;
    }
    public int solution(int n, int[] stations, int w) {
        int answer = 0;
        int idx = 0;
        int position = 1;
        while(position <= n) {
            if (idx < stations.length && stations[idx] -w <= position ) {
                position = stations[idx] + w +1;
                idx ++;
            } else {
                answer ++;
                position += w*2 + 1;
            }
        }
        return answer;
    }
}
