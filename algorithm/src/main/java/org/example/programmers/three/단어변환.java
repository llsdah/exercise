package org.example.programmers.three;
    import java.util.*;

public class 단어변환 {

    // 전체를 봐야한다는 점에서 BFS DFS 생삿
    class Solution {

        class Word {
            String str;
            int depth;

            Word (String s, int n) {
                this.str = s;
                this.depth = n;
            }
        }
        public int solution(String begin, String target, String[] words) {
            int answer = 0;
            boolean contain = false;
            for(String word : words){
                if(word.equals(target)) {
                    contain = true;
                    break;
                }
            }
            // 없으면 0 반환
            if(!contain) return 0;

            // DFS
            Set<String> set = new HashSet<>();
            Stack<Word> stack = new Stack<>();
            stack.push(new Word(begin,0));

            while(!stack.isEmpty()){
                Word word = stack.pop();
                set.add(word.str);
                if(word.str.equals(target)) {
                    return word.depth;
                }
                String str = word.str;
                for (String item : words){
                    if(!changeable(str,item)) continue;
                    if(set.contains(item)) continue;

                    stack.push(new Word(item,word.depth+1));

                }
            }

            return answer;
        }

        boolean changeable(String str1, String str2){

            int cnt = 0;
            for(int i = 0; i <  str1.length() ; i ++) {
                if( str1.charAt(i) != str2.charAt(i)) cnt++;
            }

            return cnt == 1;
        }
    }

}

/*
// 최초 소스

import java.util.*;


class Node{
    String str;
    List<Node> links;
    boolean visit;

    public Node(String str){
        this.str = str;
        links = new LinkedList<>();
    }

    public void visit(){
        this.visit = true;
    }
    public boolean isVisit(){
        return this.visit;
    }
    public void link(Node node) {
        this.links.add(node);
    }
}

class Solution {
    int count = Integer.MAX_VALUE;
    public int solution(String begin, String target, String[] words) {
        int answer = 0;

        Map<String,Node> map = new HashMap<>();

        for(int i = 0; i < words.length; i++) {
            if(words[i].equals(target)) continue;
            Node node = new Node(words[i]);

            for(int k = 0; k<words.length;k++){
                if(words[k].equals(target)) continue;
                if(k == i) continue;
                if(check(words[k], words[i])) {
                    node.link(new Node(words[k]));
                }
            }
            //System.out.println("str : "+node.str+", "+node.links.size());
            map.put(words[i],node);
        }

        // map에서 있는지 확인
        if(map.size() == words.length) return 0;

        Node be = new Node(begin);
        for (String item : map.keySet()) {
            if(check(be.str, map.get(item).str)) {
                be.link(map.get(item));
            }
        }
        map.put(be.str,be);

        temp(map,be,target,0);

        System.out.println(count);

        return count+1;
    }

    public void temp(Map<String,Node> map, Node now, String target, int cnt) {

        if (cnt >= count || map.size() <= cnt ) return;

        if( check(target, now.str) ) {
            //System.out.println("???");
            count = Math.min(count,cnt);
            return;
        } else {
            // 여기서 작업
            //System.out.println(now.str+", cnt :"+cnt+", "+map.get(now.str).links.size());
            List<Node> link = map.get(now.str).links;

            for(Node item : link) {
                temp(map, item, target, cnt+1);
            }
        }

    }

    public boolean check(String str1, String str2) {

        int cnt = 0;

        Map<Character,Boolean> map = new HashMap<>();

        for (int i = 0; i < str1.length(); i ++) {
            map.put(str1.charAt(i),true);
        }

        for (int i = 0; i < str2.length(); i ++) {
            if( map.getOrDefault(str2.charAt(i),false) ){
                cnt++;
            };
        }


        return cnt == 2;
    }
}


 */