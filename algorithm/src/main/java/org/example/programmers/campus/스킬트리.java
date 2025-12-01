package org.example.programmers.campus;
import java.util.*;
public class 스킬트리 {
    public int solution_my(String skill, String[] skill_trees) {
        int answer = 0;
        // 스파크 → 라이트닝 볼트 → 썬더
        List<Character> list = new ArrayList<>();
        char[] sk = skill.toCharArray();
        for( char c : sk ) {
            list.add(c);
        }
        int idx = 0;
        Queue<Character> que = new LinkedList<>();

        for (String item : skill_trees) {
            char[] arr = item.toCharArray();
            idx = 0;
            boolean flag = true;
            for (int i = 0; i < arr.length; i++) {
                if (list.contains(arr[i]) ) {
                    //System.out.println("index : "+i);

                    if (list.get(idx)==arr[i]) {
                        idx ++;
                    } else {
                        flag = false;
                        break;
                    }
                }
                if(idx == list.size()) break;
            }
            if (flag) answer ++;


        }


        return answer;
    }


    public int solution(String skill, String[] skill_trees) {
        int answer = 0;

        for (String s : skill_trees) {
            String s2 = s.replaceAll("[^"+s+"]","");
            if (skill.startsWith(s2)) answer ++;
        }

        Arrays.stream(skill_trees)
                .map(s -> s.replaceAll("[^"+s+"]",""))
                .filter(s -> skill.startsWith(s))
                .count();
        return answer;
    }

}
