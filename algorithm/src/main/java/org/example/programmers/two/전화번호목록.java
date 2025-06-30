package org.example.programmers.two;

    import java.util.*;
import java.util.Arrays;
import java.util.Comparator;
public class 전화번호목록 {

    class Solution {
        public boolean solution(String[] phone_book) {
            boolean answer = true;
            int len = phone_book.length;
            Arrays.sort(phone_book);
            for(int i = 1; i < len; i++) {
                if (phone_book[i].startsWith(phone_book[i-1])) {
                    return false;
                }

            }

            return answer;
        }
    }
}
