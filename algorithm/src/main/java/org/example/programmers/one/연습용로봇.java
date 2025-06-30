package org.example.programmers.one;

    import java.util.*;
public class 연습용로봇 {

    class Solution {
        public int[] solution(String command) {
            int[] answer = new int[2];

            int look = 0; // 0 북 , 1 동, 2 남, 3 서

            char[] cArr = command.toCharArray();

            for (int i = 0 ; i < cArr.length;i++) {
                char c = cArr[i];
                // 오른쪽
                if ( c == 'R') {
                    look = (look + 1) % 4;
                    // 왼쪽
                } else if ( c == 'L' ) {
                    look = (look - 1 + 4) % 4;
                } else if ( c == 'G' ) {
                    if (look == 0 ) {
                        answer[1] ++;
                    }else if (look == 1) {
                        answer[0] ++;
                    }else if (look == 2) {
                        answer[1] --;
                    }else if (look == 3) {
                        answer[0] --;
                    }


                } else if ( c == 'B' ) {
                    if (look == 0 ) {
                        answer[1] --; // --
                    }else if (look == 1) {
                        answer[0] --; // --
                    }else if (look == 2) {
                        answer[1] ++; //++
                    }else if (look == 3) {
                        answer[0] ++; // ++
                    }
                }

            }

            return answer;
        }
    }
}
