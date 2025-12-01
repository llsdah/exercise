package org.example.programmers.campus;

import java.util.*;

public class 단어변환 {
    public int solution_my(String begin, String target, String[] words) {
        int answer = 0;
        boolean[] visit = new boolean[words.length];

        Stack<String> st = new Stack<>();
        st.push(begin);
        int cnt = 0;
        while(!st.isEmpty()) {
            String str = st.pop();
            answer ++;
            for (int i = 0; i<words.length; i++) {
                if(visit[i]) continue;
                if(check(str, words[i])){
                    //System.out.println("words : "+words[i]);
                    st.push(words[i]);
                    visit[i] = true;

                    if (words[i].equals(target)) return answer;
                }
            }
        }


        return 0;
    }

    public boolean check(String s1, String s2) {
        int cnt = 0;
        char[] c1 = s1.toCharArray();
        char[] c2 = s2.toCharArray();
        for (int i = 0; i <c1.length;i++) {
            if(c1[i] != c2[i]) cnt++;

            if (cnt >= 2) return false;
        }
        return cnt == 1;
    }

    public int solution(String begin, String target, String[] words) {
        int answer = 0;
        if ( ! Arrays.asList(words).contains(target)) return 0;
        Set<String> used = new HashSet<>();
        Stack<Word> stack = new Stack<>();
        stack.push(new Word (begin, 0));
        while(!stack.isEmpty()) {
            Word w = stack.pop();
            if (w.word.equals(target)) return w.depth;

            for (String s : words) {
                if (!changable(w.word,s)) continue;
                if (used.contains(s)) continue;
                used.add(s);
                stack.push(new Word(s, w.depth+1));
            }
        }
        return answer;
    }
    class Word {
        String word;
        int depth;
        Word(String s, int i) {word = s; depth = i;}
    }
    boolean changable(String s1, String s2) {
        int count = 0;
        for (int i = 0; i < s1.length() && count <2; i++) {
            if(s1.charAt(i) != s2.charAt(i)) count ++;
        }
        return count == 1;
    }

}
