package org.example.programmers.three;
import java.util.*;
import java.util.*;


public class 숫자게임 {
    class Solution {
        public int solution(int[] A, int[] B) {
            int answer = 0;

            Arrays.sort(A);
            Arrays.sort(B);

            int index = B.length -1;
            for(int i = A.length -1 ; i >= 0;i--) {
                if ( B[index] > A[i]) {
                    answer ++;
                    index --;
                }

            }

            return answer;
        }
    }
    class Solution1 {
        public int solution(int[] A, int[] B) {
            int answer = 0;

            Arrays.sort(A);
            Arrays.sort(B);

            int index = 0;
            for(int i = 0; i <A.length;i++) {
                for ( int k = 0; k <B.length;k++) {
                    if ( B[k] > A[i]) {
                        answer ++;
                        B[k] = 0;
                        break;
                    }
                }
            }

            return answer;
        }
    }
}
