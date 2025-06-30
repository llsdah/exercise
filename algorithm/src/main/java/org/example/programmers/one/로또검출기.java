package org.example.programmers.one;

    import java.util.*;
public class 로또검출기 {

    class Solution {
        public boolean solution(int[] lotto) {
            boolean answer = true;

            Set<Integer> set = new HashSet<>();

            for (int i : lotto) {
                set.add(i);
            }

            return set.size() == 6 ? true : false;
        }
    }
}
