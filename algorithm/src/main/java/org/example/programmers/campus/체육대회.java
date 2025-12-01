package org.example.programmers.campus;
import java.util.*;

public class 체육대회 {
    public int solution_my(int[][] ability) {
        int answer = 0;
        int[] remove = new int[ability[0].length];
        Arrays.fill(remove,-1);
        answer = sum(remove, ability, 0, 0);
        return answer;
    }

    public int sum(int[] remove,int[][] a, int find, int cnt) {

        if (find == a[0].length) {
            //System.out.println("cnt: "+cnt+", find: "+find +", remove: "+Arrays.toString(remove));
            return cnt;
        }
        int max = 0;
        for (int i =0; i<a.length;i++){
            boolean flag = false;
            for(int re : remove) {
                if(re == i) {
                    flag = true;
                    break;
                }
            }
            if(flag) continue;
            remove[find] = i;
            max = Math.max(max,sum(remove,a,find+1,cnt+a[i][find]));
            //System.out.println("i: "+i+", max: "+max+", find: "+find +", remove: "+Arrays.toString(remove));
            remove[find] = -1;
        }

        return max;
    }

    public int solution(int[][] ability) {
        int answer = 0;
        boolean[] select = new boolean[ability.length];
        answer = dfs(ability, select, 0);
        return answer ;
    }

    int dfs(int[][] a, boolean[] b, int index) {
        int student = a.length;
        int part = a[0].length;
        if (index == part) return 0;
        int ret = 0;
        for (int i = 0; i < student; i++) {
            if (b[i]) continue;
            b[i] = true;
            ret =Math.max(ret, a[i][index] + dfs(a,b, index+1));
            b[i] = false;
        }
        return ret;
    }






}
