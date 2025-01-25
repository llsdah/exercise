package org.example.nonlinear.practice;


import java.awt.event.WindowStateListener;
import java.util.HashMap;

/**
 * 길이가 N인 영어단어를 입력하려면 키보드를 N번 눌러야 한다.
 *
 * 단어 입력시 누르는 횟수를 줄이기 위해 간단한 자동완성 프로그램을 개발,
 * 주어진 단어 목록을 바탕으로 해당 사전내에,
 * 가능한 다음 글자가 하나뿐이면 그 글자를 버튼 입력 없이 자동으로 입력하게 한다.
 * 예> hell hello heaven java 이면 -> h 입력시 뒷 단어는 무조건 e 이므로 패스 -> l,v 중 입력받는다.
 * 그래서 자동완성이 되면 hell = 2번, hello = 3번, heaven = 2번 , java = 1번
 *
 *
 * 총 단어의 평균 입력횟수를 출력
 *
 *
 */

public class Practice_15 {

    public static void main(String[] args) {
        String[] words = {"hell","hello","heaven","java"};
        System.out.printf("%.2f\n", solutions(words));

        words = new String[]{"abca","abcb","abcc"};
        System.out.printf("%.2f\n", solutions(words));

        words = new String[]{"cloud","cloudy","rain","rainy","sun","sunny"};
        System.out.printf("%.2f\n", solutions(words));

    }

    private static double solutions(String[] words) {
        if (words == null || words.length == 0) {
            return 0;
        }

        if (words.length == 1) {
            return 1;
        }

        Trie trie = new Trie();
        for (String word : words) {
            trie.insert(word);
        }

        double sum = 0;
        for (String word : words) {
            sum += 1;
            Node cur = trie.root;
            cur = cur.child.get(word.charAt(0));

            for (int i = 1; i < word.length(); i++) {
                if (cur.child.size() > 1) { // 갈래길이 있으면, 
                    sum += 1;
                } else if (cur.child.size() == 1 && cur.isTermial) { // 만약 마지막이면 한번더 입력해야됨
                    sum += 1;
                }

                cur = cur.child.get(word.charAt(i));
            }
        }

        return sum / words.length;
    }

    static class Node {
        HashMap<Character,Node> child;
        boolean isTermial;

        public Node(){
            this.child = new HashMap<>();
            this.isTermial = false;
        }
    }

    static class Trie {
        Node root;

        public Trie(){
            this.root = new Node();
        }

        public void insert(String str){
            Node cur = this.root;

            for (int i = 0; i < str.length(); i++) {
                char c = str.charAt(i);

                cur.child.putIfAbsent(c, new Node());
                cur = cur.child.get(c);

                if (i == str.length() - 1) {
                    cur.isTermial = true;
                    return;
                }

            }
        }
    }



}
