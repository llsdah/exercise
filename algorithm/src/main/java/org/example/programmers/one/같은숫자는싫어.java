package org.example.programmers.one;

    import java.util.*;
public class 같은숫자는싫어 {


    public class Solution {
        public int[] solution(int []arr) {
            int[] answer = {};

            // [실행] 버튼을 누르면 출력 값을 볼 수 있습니다.
            System.out.println("Hello Java");
            List<Integer> list = new ArrayList<>();
            list.add(arr[0]);
            Set<Integer> set = new HashSet<>();
            set.add(arr[0]);
            for (int i = 1; i < arr.length; i++) {
                set.add(arr[i]);
                if ( set.size() >=2 ) {
                    list.add(arr[i]);
                    set.clear();
                    set.add(arr[i]);
                }

            }

            answer = new int[list.size()];
            for(int i = 0; i < list.size(); i++) {
                answer[i] = list.get(i);
            }

            return answer;
        }
    }
}
