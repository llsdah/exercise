package org.example.nonlinear.trie;

import java.util.HashMap;

public class MyTrie {

    /**
     * 트라이
     * 문자열을 저장하고 빠르게 탐색하기 위한 트리형태 구조
     * 정렬된 트리 구조
     *
     *
     * 구현
     * key - value 형태의 노드 구성
     * key 알파벳
     * value 자속 노드
     * HashMap을구현 <Character,Node> + boolean
     *
     *
     *
     */


    static class Node {
        HashMap<Character, Node> child;
        boolean isTerminal;

        public Node() {
            this.child = new HashMap<>();
            this.isTerminal = false;
        }
    }

    static class Trie {
        Node root;

        public Trie() {
            this.root = new Node();
        }

        public void insert(String str) {
            Node cur = this.root;
            for (int i = 0; i < str.length(); i++) {
                char c = str.charAt(i);

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

            for (int i = 0; i < str.length(); i++) {
                char c = str.charAt(i);

                if (cur.child.containsKey(c)) {
                    cur = cur.child.get(c);
                } else {
                    return false;
                }

                if (i == str.length() - 1) {
                    if (!cur.isTerminal) {
                        return false;
                    }
                }

            }
            return true;
        }

        public void delete(String str) {

            boolean ret = this.delete(this.root, str, 0);

            if (ret) {
                System.out.println("deelte : "+ str);
            } else {
                System.out.println("delete false : "+ str);
            }
            return;
        }

        public boolean delete(Node node, String str,int idx ) {
            char c = str.charAt(idx);
            if (!node.child.containsKey(c)) {
                return false;
            }

            Node cur = node.child.get(c);
            idx ++;

            if (idx == str.length()) {
                if (!cur.isTerminal) {
                    return false;
                }

                cur.isTerminal = false;

                if (cur.child.isEmpty()) {
                    node.child.remove(c);
                }

            } else {
                if (!this.delete(cur,str,idx)) {
                    return false;
                }
                if (!cur.isTerminal && cur.child.isEmpty()) {
                    node.child.remove(c);
                }
            }
            return true;
        }
    }

    public static void main(String[] args) {

        Trie trie = new Trie();

        trie.insert("apple");
        trie.insert("april");
        trie.insert("app");
        trie.insert("ace");
        trie.insert("bear");
        trie.insert("best");
        System.out.println(trie.search("apple"));
        System.out.println(trie.search("app"));
        System.out.println(trie.search("ace"));
        System.out.println(trie.search("applu"));
        System.out.println(trie.search("apple"));

        System.out.println();
        trie.delete("apple");
        System.out.println(trie.search("apple"));
        trie.delete("apple");

    }


}
