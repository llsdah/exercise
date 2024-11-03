package org.example.programmers.one;

import org.example.programmers.ProgrammersSolutionSample;
import java.util.*;

/*
동적 프로그래밍 (Dynamic Programming) DP 문제

동적 프로그래밍은 문제를 여러 작은 하위 문제로 나누고,
각 하위 문제의 답을 저장하여 재사용함으로써 전체 문제를 해결하는 최적화 기법입니다.
이를 통해 중복 계산을 줄이고 효율적으로 문제를 해결할 수 있습니다.

 */
public class 공원 implements ProgrammersSolutionSample {
    @Override
    public boolean solution() {
        return false;
    }
    class Solution {
        public int solution(int[] mats, String[][] park) {
            int answer = -1;

            Arrays.sort(mats);

            int maxSize = 0; // 정사각형 가장큰 크기
            int maxRow = -1; // 정사각형 시작 위치 행
            int maxCol = -1; // 정사각형 시작 위치 열

            // DP 배열 생성
            int rows = park.length;
            int cols = park[0].length;
            int[][] dp = new int[rows][cols];

            // DP 배열 채워보기
            for ( int i = 0; i<rows; i++ ){
                for( int j = 0; j<cols; j++){
                    // -1 인경우
                    if ( park[i][j].equals("-1")){

                        if ( i == 0 || j == 0 ){
                            // 첫행이나 첫 열에서는 "-1"만 있으면 1로 설정?
                            dp[i][j] = 1;
                        }else {
                            dp[i][j] = Math.min(dp[i-1][j],
                                    Math.min(dp[i][j-1],dp[i-1][j-1]))+1;
                        }

                        if ( dp[i][j] > maxSize ){
                            maxSize = dp[i][j];
                            maxRow = i;
                            maxCol = j;
                        }

                    }
                }
            }

            System.out.println("maxSize"+ maxSize);

            for(int i = mats.length-1; i >=0 ;i-- ){
                if(mats[i] <= maxSize){
                    answer = mats[i];
                    break;
                }
            }

            return answer;
        }
    }
}
