package org.example.linear.deque.pratice;

import java.awt.image.AreaAveragingScaleFilter;
import java.util.ArrayDeque;
import java.util.Deque;

/**
 * Palindrome 찾기
 * 유무 판단해 반환
 *
 * a => true
 *
 * madam = > true
 *
 * aaddr => false
 */
public class Practice_2 {

    public static void main(String[] args) {
        System.out.println(isPalindrom("adasdaf"));
        System.out.println(isPalindrom("aadd"));
        System.out.println(isPalindrom("afdfa"));
        System.out.println(isPalindrom("addde"));
    }

    private static boolean isPalindrom(String str) {
        Deque deque = new ArrayDeque();
        boolean isFront = true;
        boolean isPalindrome = true;

        for (String s : str.split("")) {
            deque.addFirst(s);
        }

        while (!deque.isEmpty()) {
            String s1 = (String)deque.pollFirst();
            String s2 = (String)deque.pollLast();
            if (s1 != null && s2 != null && !s1.equals(s2)) {
                isPalindrome = false;
                break;
            }
        }

        return isPalindrome;
    }
}
