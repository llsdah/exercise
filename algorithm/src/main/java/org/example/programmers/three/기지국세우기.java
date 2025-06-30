package org.example.programmers.three;

public class 기지국세우기 {

    class Solution {
        public int solution(int n, int[] stations, int w) {
            int answer = 0;
            boolean[] a = new boolean[n+1];
            // [실행] 버튼을 누르면 출력 값을 볼 수 있습니다.
            // System.out.println("Hello Java");


            for (int i = 0; i < stations.length;i++){
                int num = stations[i];
                int start = num - w > 0 ? num - w : 1;
                int end = num + w <=n ? num + w : n;
                for(int k = start; k <= end ; k++){
                    a[k] = true;
                }

            }
            //System.out.println(Arrays.toString(a));
            // 값 조회
            for (int i = 1; i <= n;i++){
                if(!a[i]) {

                    //System.out.println("false : "+i);
                    answer ++;
                    int end = i + (w*2) <= n ? i + (w*2) : n;
                    for (int k = i; k <= end;k++) {
                        a[k] = true;
                    }
                    //System.out.println(Arrays.toString(a));

                    i = i + w;
                }
            }

            return answer;
        }
    }

    public int solution(int n, int[] stations, int w) {
        int answer = 0;

        // [실행] 버튼을 누르면 출력 값을 볼 수 있습니다.
        //System.out.println("Hello Java");
        int pos = 1;
        int dist = 0;
        int index = 0;
        int cover = w*2 + 1;
        while(pos <= n) {
            if(index < stations.length && pos >= stations[index] - w){
                pos = stations[index] + w + 1;
                index ++;
            } else {
                answer++;
                pos += cover;
            }
        }


        return answer;
    }
}
