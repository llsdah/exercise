package org.example.nonlinear.practice;

import java.util.ArrayList;
import java.util.HashMap;


/**
 * 물자열과 pattern과 매칭되면 true
 *
 * 각 문자 전후 소문자 알파벳을 추가 했을때 만들 수 있는 문자열을 기준으로 한다.
 */
public class Practice_10 {
    public static void main(String[] args) {
        String[] queris = {"FooBar","FooBatTest","FrameBuffer","ForceFeedBack"};
        String pattern = "FB";

        System.out.println("pattern = " + solution(queris,pattern));
        pattern = "FoBa";
        System.out.println("pattern = " + solution(queris,pattern));

        pattern = "FoBaT";
        System.out.println("pattern = " + solution(queris,pattern));

    }

    private static ArrayList<Boolean> solution(String[] queris, String pattern) {

        Trie trie = new Trie();
        trie.insert(pattern);

        ArrayList<Boolean> result = new ArrayList<>();

        for (String str : queris) {
            result.add(trie.search(str));
        }

        return result;
    }

    static class Node {
        HashMap<Character,Node> child;
        boolean isTerminal;

        public Node() {
            this.child = new HashMap<>();
            this.isTerminal = false;
        }
    }

    static class Trie {
        Node root;
        ArrayList<Character> capitals;

        public Trie() {
            this.root = new Node();
            this.capitals = new ArrayList<>();
        }

        public void insert (String str) {
            Node cur = this.root;

            for (int i = 0; i < str.length(); i++) {
                char c = str.charAt(i);

                // 대문자 < 소문자
                if (c < 'a') {
                    capitals.add(c);
                }

                cur.child.putIfAbsent(c, new Node());
                cur = cur.child.get(c);

                if (i == str.length() - 1) {
                    cur.isTerminal = true;
                    return;
                }
            }
        }

        public boolean search(String str) {
            Node cur = this.root;
            ArrayList<Character> cap = new ArrayList<>(capitals);

            for (int i = 0; i < str.length(); i++) {
                char c = str.charAt(i);

                if (cur.child.containsKey(c)) {
                    cap.remove(new Character(c));
                    cur = cur.child.get(c);
                } else {
                    // 대문자 false
                    if (c < 'a') {
                        return false;
                    } else {
                        continue;
                    }
                }

                if (i == str.length() - 1) {
                    if (!cur.isTerminal) {
                        return false;
                    }
                }

            }

            return cap.size() == 0;
        }


    }

}
