package org.example.nonlinear.trie.practice;

/**
 * 문자열 배열 dictionary와 문자열 sentence가 주어졌을때
 * sentence 내의 단어 중 dictionary의 단어로 시작하는 경우
 * 해당 단어를 출력하는 프로그램
 *
 * dictionary = "cat","bat","rat"
 * sentence = "the cattle war rattled by the battery"
 * "the cat was rat by the bat"
 *
 *
 * dictionary = "a","b","c"
 * sentence = "apple banana carrot water"
 * "a b c water "
 *
 */
public class Practice_2 {

    public static void main(String[] args) {
        String[] dics = {"cat","bat","rat"};
        String sentence = "the cattle war rattled by the battery";
        String result = "the cat was rat by the bat";

        String answer = solution(dics, sentence);
        System.out.println("answer = " + answer + ", result : "+ result);

        dics = new String[]{"a","b","c"};
        sentence = "apple banana carrot water";
        result = "a b c water";

        answer = solution(dics, sentence);
        System.out.println("answer = " + answer + ", result : "+ result);


    }

    private static String solution(String[] dics, String sentence) {
        Practice_1.Trie trie = new Practice_1.Trie();
        for ( String str : dics) {
            trie.insert(str);
        }

        StringBuffer result = new StringBuffer();

        for (String word : sentence.split(" ")) {
            Practice_1.Node cur = trie.root;
            StringBuffer sb = new StringBuffer();

            for (char c : word.toCharArray()) {
                sb.append(c);

                if (cur.child.get(c) != null) {
                    if (cur.child.get(c).isTerminal) {
                        break;
                    }
                    cur = cur.child.get(c);
                } else {
                    sb = new StringBuffer(word);
                    break;
                }
            }
            result.append(sb);
            result.append(" ");

        }

        return result.toString();


    }

}









