package org.example.programmers.campus;
import java.util.*;
public class 같은숫자는싫어 {
    public int[] solution(int []arr) {
        int[] answer = {};
        List<Integer> list = new ArrayList<>();
        // [실행] 버튼을 누르면 출력 값을 볼 수 있습니다.
        Set<Integer> set = new HashSet<>();
        int num = arr[0];
        set.add(arr[0]);
        list.add(num);
        for (int item : arr) {

            if(set.add(item)) {
                list.add(item);
                set.clear();
                set.add(item);
            }
        }
        answer = new int[list.size()];
        for (int i = 0; i < list.size(); i++) {
            answer[i] = list.get(i);
        }

        return answer;
    }
}
