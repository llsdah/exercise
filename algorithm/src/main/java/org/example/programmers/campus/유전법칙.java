package org.example.programmers.campus;

import java.util.*;

public class 유전법칙 {

    public String[] solution_my(int[][] queries) {
        int n = queries.length;
        String[] answer = new String[n];

        Map<String,String[]> map = new HashMap<>();
        // 4 1 2 3
        map.put("RR",new String[]{"RR","RR","RR","RR"});
        map.put("Rr",new String[]{"RR","Rr","Rr","rr"});
        map.put("rr",new String[]{"rr","rr","rr","rr"});

        for (int i = 0 ; i < n; i ++) {
            int gen = queries[i][0];
            int num = queries[i][1];
            if ( gen == 1) answer[i] = "Rr";
            else answer[i] = find(map, gen, num);
        }
        return answer;
    }
    public String find(Map<String,String[]> map, int gen, int num) {
        if ( gen == 2) return map.get("Rr")[(num-1)%4];
        int parentNum= (num-1)/4+1;
        String str = find(map,gen-1,parentNum);

        return map.get(str)[(num-1)%4];
    }

    // fail1
    Map<String,String[]> map = new HashMap<>();
    public String[] solution_fail1(int[][] queries) {
        String[] answer = new String[queries.length];

        List<String[]> list = new ArrayList<>();

        String[] arr1 = new String[]{"","Rr"};
        String[] arr2 = new String[]{"","RR","Rr", "Rr", "rr"};
        String[] arr3 = new String[]{"","RR", "RR"," RR"," RR","RR","Rr","Rr","rr","RR","Rr","Rr","rr","rr","rr","rr","rr"};
        list.add(arr2);
        list.add(arr1);
        list.add(arr2);
        list.add(arr3);

        map.put("RR",new String[]{"RR", "RR", "RR", "RR", "RR"});
        map.put("Rr",new String[]{"rr", "RR", "Rr", "Rr", "rr"});
        map.put("rr",new String[]{"rr", "rr", "rr", "rr", "rr"});


        for (int i = 0; i<queries.length;i++) {
            int[] s = queries[i];
            if (s[0]<=3) {
                answer[i] = list.get(s[0])[s[1]];
                continue;
            }
            answer[i] = find_fail1(list,new int[]{ s[0]-1, s[1]/4}, s[1]%4 );
            //System.out.println(list.get(0)[value]);
        }

        return answer;

    }

    public String find_fail1(List<String[]> list, int[] f, int rest) {
        if ( f[0] == 3 ) {
            String temp = list.get(3)[f[1]];
            //System.out.println(temp+":"+rest+", to : "+Arrays.toString(f));
            return map.get(temp)[rest];
        }

        String str = find_fail1(list, new int[] {f[0]-1, f[1]/4}, f[1]%4 );

        return map.get(str)[rest];
    }

    String[] child = {"RR","Rr","Rr","rr"};
    public String[] solution(int[][] queries) {
        String[] answer = new String[queries.length];
        for (int i = 0; i < queries.length; i++) {
            int n = queries[i][0]; // n 세대
            int p = queries[i][1]; // p 번재 : 0번시작을 위한 1개 뺴기
            answer[i] = back(n, p-1);//
        }

        return answer;
    }

    public String back(int gen, int x) {
        if (gen == 1) return "Rr";
        String parent = back ( gen -1, x / 4);
        if (parent.equals("Rr")) return child[x % 4]; // 몇번째 자식?
        else return parent;
    }

}
