package org.example.programmers.two;
import java.util.*;
public class 전력망둘로나누기 {

    public int solution(int n, int[][] wires) {
        int answer = -1;

        int k = wires.length;
        int min = Integer.MAX_VALUE;
        for (int i = 0;  i < k ; i++) {
            //System.out.println("index : "+ i);
            Queue<Integer> que = new LinkedList<>();
            boolean[] visit = new boolean[n+1];
            int start = wires[i][0];
            int last = wires[i][1]; // 첫번째 방문?
            int cnt = 0;
            que.offer(last);
            visit[last] = true;
            //System.out.println(last+", main : "+Arrays.toString(wires[i]));

            while( !que.isEmpty() ) {
                int st = que.poll();
                cnt ++;
                for (int num = 0 ; num < k; num++) {
                    if ( i == num) continue;
                    //System.out.println(Arrays.toString(wires[num]));

                    int s = wires[num][0];
                    int l = wires[num][1];

                    if(st == s && !visit[l]) {
                        //System.out.println(Arrays.toString(wires[num]));
                        que.offer(l);
                        visit[l]=true;
                        continue;
                    }
                    if(st == l && !visit[s]) {
                        //System.out.println(Arrays.toString(wires[num]));
                        que.offer(s);
                        visit[s]=true;
                        continue;
                    }
                }
            }


            min = Math.min( min , Math.abs(cnt-Math.abs(n - cnt) ) );
            //System.out.println(min + ", n : "+n+", cnt : "+ cnt);

        }


        return min;
    }

    public int solution_fail1(int n, int[][] wires) {
        int answer = -1;
        int k = wires.length;
        int min = Integer.MAX_VALUE;
        for (int con = 0; con < k;con ++) {

            Queue<int[]> que = new LinkedList<>();
            List<Integer> remove = new ArrayList<>();
            if( con == 0 ){
                que.offer(wires[k-1]);
                remove.add(k-1);

                //System.out.println("Delete :"+Arrays.toString(wires[k-1]));
            } else {
                que.offer(wires[con-1]);
                remove.add(con-1);

                //System.out.println("Delete :"+Arrays.toString(wires[con-1]));
            }

            Set<Integer> set = new HashSet<>();
            while( !que.isEmpty() ) {
                int[] cur = que.poll();
                set.add(cur[0]);
                set.add(cur[1]);
                for (int i = 0 ; i < k; i ++) {
                    if (remove.contains(i)) continue;
                    if ( cur[0] == wires[i][0] || cur[1] == wires[i][0] ) {
                        que.offer(wires[i]);
                        remove.add(i);
                        set.add(wires[i][0]);
                        set.add(wires[i][1]);
                        //System.out.println("Con :"+Arrays.toString(wires[i]));
                    }
                }

            }

            int mi = n - set.size();
            if (mi > set.size()) {
                min = Math.min( min, mi -set.size());
            } else {
                min = Math.min( min, set.size()-mi);
            }

            //System.out.println("min : "+min+", mi : "+mi+", "+set.size());

        }
        //System.out.println("min :"+min);
        return min;
    }
}
