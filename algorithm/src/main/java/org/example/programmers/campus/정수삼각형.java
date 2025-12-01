package org.example.programmers.campus;

public class 정수삼각형 {
    public int solution_my(int[][] triangle) {
        int answer = 0;

        int c = triangle.length;
        int r = triangle[0].length;
        /**
         for (int i = c-1; i >=0 ; i--) {
         for (int k=0; k<triangle[i].length;k++){
         System.out.print(triangle[i][k]+" ");
         }
         System.out.println();
         }
         */
        for (int i = c-1; i >=1; i--) {
            for (int k=0; k<triangle[i].length-1;k++){
                int next = triangle[i-1][k];
                triangle[i-1][k] += Math.max(triangle[i][k],triangle[i][k+1]);
            }
        }
        return triangle[0][0];
    }

    public int solution(int[][] triangle) {
        int answer =0;
        // 위에서 부터 내려오며 최대값
        for (int i = 1; i < triangle.length; i++) {
            for (int k = 0; k < triangle[i].length; k ++) {

                if (k == 0) {
                    triangle[i][k] = triangle[i][k] + triangle[i-1][k];
                } else if (k == triangle[i].length -1 ) {
                    triangle[i][k] = triangle[i][k] + triangle[i-1][k-1];
                } else {
                    int left = triangle[i][k] = triangle[i][k] + triangle[i-1][k-1];
                    int right = triangle[i][k] = triangle[i][k] + triangle[i-1][k];
                    triangle[i][k] = Math.max(left, right);
                }

                answer = Math.max ( answer , triangle[i][k] );
            }
        }

        return answer;
    }


}
