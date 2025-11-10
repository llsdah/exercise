package org.example.programmers.campus;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class 순열검사 {
        public boolean solution(int[] arr) {
            boolean answer = true;

            // [실행] 버튼을 누르면 출력 값을 볼 수 있습니다.
            // 중복없이
            //System.out.println("Hello Java");
            Set<Integer> set = new HashSet<>();
            Arrays.sort(arr);

            if (arr.length != arr[arr.length - 1]) {
                return false;
            }
            for (int item : arr) {
                if (!set.add(item)) {
                    return false;
                }
            }

            return answer;
        }
}
