package org.example.programmers.one;

public class 택배상자꺼내기 {

    class Solution {
        public int solution(int n, int w, int num) {
            int answer = 0;
            int row = 0;
            int col = 0;
            int[][] boxes = new int[101][101];
            int box = 0;

            for (int i = 1; i <= 100; i++) {

                if (i % 2 == 1) {

                    for (int l = 1; l <= w; l++) {
                        boxes[i][l] = ++box;
                        if (box == num) {
                            row = l;
                            col = i;
                        }
                    }

                } else {
                    int even = box + w;
                    for (int l = 1; l <= w; l++) {
                        boxes[i][l] = even--;

                        if (even + 1 == num) {
                            row = l;
                            col = i;
                        }
                    }
                    box += w;
                }


            }


            for (int i = col; i <= 100; i++) {
                if (boxes[i][row] <= n) {
                    answer++;

                    //System.out.println("boxes[i][row] : "+boxes[i][row]);

                } else {
                    break;
                }
            }
            return answer;
        }
    }
}
