package org.example.nonlinear.trie.practice;

/**
 * 문자열 배열 strs 가 targets 이 주어질때 
 * targets 내의 한문자만 바꾸면 strs 단어가 되는 지 판별하는 문제 
 * 
 * 
 * strs : "apple","banana", "kiwi"
 * targets : "applk", "bpple", "apple"
 * 출력 true true false
 * 
 */
public class Practice_3 {

    public static void main(String[] args) {
        
        String[] strs  = {"apple","banana", "kiwi"};
        String[] targets = {"applk", "bpple", "apple"};
        
        solution(strs, targets);
        
    
    }

    private static void solution(String[] strs, String[] targets) {

        Practice_1.Trie trie = new Practice_1.Trie();
        for (String str : strs) {
            trie.insert(str);
        }
        
        
        for (String item : targets) {
            boolean result = examineWord(trie.root,item,0,false);
            System.out.println("result = " + result);
        }
        
    }

    private static boolean examineWord(Practice_1.Node node, String item, int i, boolean flag) {
        if (i < item.length()) {
            if (node.child.containsKey(item.charAt(i))) {
                if (examineWord(node.child.get(item.charAt(i)), item, i+1,flag )) {
                    return true;
                }
            }

            if (!flag) {
                for (char c : node.child.keySet()) {
                    if ( c != item.charAt(i) && examineWord(node.child.get(c),item, i + 1, true)) {
                        return true;
                    }
                }
            }
            return false;
        }
        return  flag && node.isTerminal;
    }

}
