package org.example.programmers.campus;
import java.util.*;
public class 로또번호검출기 {

    public boolean solution(int[] lotto) {
        boolean answer = true;

        Set<Integer> set = new HashSet<>();

        for (int i : lotto) {
            if (!set.add(i)) return false;
        }

        return answer;
    }
}
