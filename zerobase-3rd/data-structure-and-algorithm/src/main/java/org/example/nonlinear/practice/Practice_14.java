package org.example.nonlinear.practice;

import java.util.HashMap;

/**
 *
 * 전화번호 목록이 nums 배열, 전화번호 구성이 일관성 있는지를 체크하는 문제
 * 접두어가 안겹쳐야 일관성이 있다.
 *
 *
 * 아래와 같은 전화번호 목록이 주어진 경우
 * 긴급전화 : 911
 * 집 : 123456789
 * 회사 : 911234
 *
 * 접두어가 겹치므로 일관성 없다
 *
 *
 */
public class Practice_14 {

    public static void main(String[] args) {
        String[] nums = {"911","123456789","911234"};
        System.out.println(solution(nums));

        nums = new String[]{"113","12345","12344","98765"};
        System.out.println(solution(nums));

    }

    private static boolean solution(String[] nums) {

        if (nums == null || nums.length == 0) {
            return false;
        }

        Trie trie = new Trie();
        trie.insert(nums[0]);

        for (int i = 1; i < nums.length; i++) {
            if (isPrefix(trie.root, nums[i])) {
                return false;
            } else {
                trie.insert(nums[i]);
            }
        }

        return true;
    }


    public static boolean isPrefix(Node node, String prefix) {
        Node cur = node;

        for (int i = 0; i < prefix.length(); i++) {
            char c = prefix.charAt(i);

            if (cur.child.get(c) == null) {
                return false;
            }

            cur = cur.child.get(c);

            if (cur.isTerminal) {
                return true;
            }
        }

        return true;
    }


    static class Node{
        HashMap<Character,Node> child;
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

    }




}
