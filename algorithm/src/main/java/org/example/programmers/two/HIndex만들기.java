package org.example.programmers.two;

    import java.util.*;
public class HIndex만들기 {


    class Solution {
        public int solution(int[] citations) {
            int answer = 0;

            // 편수와 번수
            Arrays.sort(citations);
            int p = citations.length;
            for( int i = 0; i < p; i ++) {
                if( p - i <= citations[i]) return p - i;
            }

            return 0 ;
        }
    }
}
