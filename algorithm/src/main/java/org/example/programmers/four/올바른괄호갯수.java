package org.example.programmers.four;

public class 올바른괄호갯수 {
    class Solution {
        int count =0;
        public int solution(int n) {
            int answer = 0;

            dfs (0,0,n);

            return count;
        }

        public void dfs(int open, int close, int n) {
            if (open== n && close ==n) {
                count ++;
                return;
            }

            if ( open < n) {
                dfs(open+1,close,n);
            }

            if (close < open) {
                dfs(open,close+1,n);
            }

        }
    }

}
