package org.example.programmers.two;

    import java.util.*;
public class 유전법칙 {
    class Solution {
        public String[] solution(int[][] queries) {

            int n = queries.length;
            String[] answer = new String[n];
            for (int i = 0; i<n; i++ ) {
                int gen = queries[i][0];
                int number = queries[i][1];

                String str = find(gen,number);
                answer[i] = str;

            }
            return answer;
        }


        public String find(int gen, int num ) {

            if (gen == 1) {
                return "Rr";
            } else if (gen == 2){
                String[] temp = {"RR","Rr","Rr","rr"};

                return temp[num-1];
            }

            String parent = find(gen-1, (num +3) / 4  );

            if (parent.equals("RR") || parent.equals("rr")) {
                return parent;
            }

            int index = (num -1 ) % 4;
            String[] tmp = {"RR","Rr","Rr","rr"};

            return tmp[index];
        }
    }
}
