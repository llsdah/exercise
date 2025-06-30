package org.example.programmers.two;

    import java.util.*;
public class 스킬트리 {
    class Solution {
        public int solution(String skill, String[] skill_trees) {
            int answer = 0;

            Queue<Character> que = new LinkedList<>();

            for (Character c : skill.toCharArray()){
                que.add(c);
            }

            for (String str : skill_trees) {
                // 스킬트리 순서 복제 결론적으로 다 꺼내야함
                Queue<Character> temp = new LinkedList<>(que);
                List<Character> list = new LinkedList<>();
                for(int i = 0; i < str.length() ;i++) {
                    char c = str.toCharArray()[i];
                    list.add(c);
                    if(!temp.isEmpty() && temp.peek() == c){
                        temp.poll();
                    }else if (temp.isEmpty()) {
                        break;
                    }
                }

                boolean flag = true;
                while(!temp.isEmpty()){
                    if( list.contains(temp.poll()) ){
                        flag = false;
                        break;
                    }
                }
                if(flag) answer++;

            }


            return answer;
        }
    }
}
