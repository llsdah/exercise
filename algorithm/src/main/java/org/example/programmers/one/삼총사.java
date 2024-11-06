package org.example.programmers.one;

import org.example.programmers.ProgrammersSolutionSample;
import java.util.*;

/**
 * ThreeSum 문제 3Sum
 *
 */
public class 삼총사 implements ProgrammersSolutionSample {

    @Override
    public boolean solution() {
        return false;
    }

    class Solution {
        public int solution(int[] numbers) {
            int count = 0;
            int n = numbers.length;

            // 모든 세 수 조합을 다 탐색
            for (int i = 0; i < n - 2; i++) {
                for (int j = i + 1; j < n - 1; j++) {
                    for (int k = j + 1; k < n; k++) {
                        if (numbers[i] + numbers[j] + numbers[k] == 0) {
                            count++;
                        }
                    }
                }
            }

            return count;
        }
    }

}
