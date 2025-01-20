package org.example.nonlinear.practice;

import java.util.Arrays;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Queue;

/**
 *
 * => 전후 관계 그래프 구조
 *
 * 네자리 비밀번호 자물쇠
 * 원형 바퀴형태로 돌려가며 설정하는방식 아래와 같은 숫자로 구성
 * 9 -> 0 0 ->9
 * 바퀴의 숫자는 다음과 같다
 *
 * 시작은 0000
 *
 * 지나가면 안되는 곳 + 완성해야되는 숫자 => 횟수
 * 8888 + 0009 => 1
 *
 * "0201","0101","0102","2002" + "0202" => 6
 */

public class Practice_4 {

    public static void main(String[] args) {
        String[] deadend = {"0201","0101","0102","2002"};
        System.out.println(solution(deadend,"0202"));

        deadend = new String[]{"8888"};
        System.out.println(solution(deadend,"0009"));

    }

    private static int solution(String[] deadend, String target) {
        if (target == null || target.length() == 0 ) {
            return -1;
        }

        HashSet<String> visited = new HashSet<>(Arrays.asList(deadend));

        Queue<String> que = new LinkedList<>();

        que.offer("0000");
        int cnt = 0;

        while (!que.isEmpty()) {
            int size = que.size();

            for (int i = 0; i < size; i++) {
                String curNum = que.poll();

                if (!visited.add(curNum)) {
                    continue;
                }

                if (curNum.equals(target)) {
                    return cnt;
                }

                // 이동가능한 숫자
                for (String nextNum : getNextNums(curNum.toCharArray())) {
                    // 방문한 적이 없다면.
                    if (!visited.contains(nextNum)) {
                        que.offer(nextNum);
                    }
                }
            }
            // 위에서 한번 이동할때마다 생각
            cnt++;
        }

        return -1;
    }

    // 이동가능한 숫자를 찾아서 다시 넣는 작업
    private static LinkedList<String> getNextNums(char[] charArray) {
        LinkedList<String> nums = new LinkedList<>();

        // 이동가능한 각자리 8개를 넘겨준다
        for (int i = 0; i < charArray.length; i++) {
            char c = charArray[i];
            // 9이면 0 이공이고, 아니면 하나 증가 값
            charArray[i] = c == '9' ? '0' : (char) (c + ((char) 1));
            nums.add(String.valueOf(charArray));

            // 0 이면 9이고, 아니면 하나 뺀 값
            charArray[i] = c == '0' ? '9' : (char) (c - ((char) 1));
            nums.add(String.valueOf(charArray));

            charArray[i] = c;
        }

        return nums;
    }

}

