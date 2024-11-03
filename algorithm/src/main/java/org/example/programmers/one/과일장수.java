package org.example.programmers.one;

    import org.example.programmers.ProgrammersSolutionSample;

    import java.util.*;
public class 과일장수 implements ProgrammersSolutionSample {
    @Override
    public boolean solution() {
        return false;
    }

    class Solution {
        public int solution(int k, int m, int[] score) {
            int answer = 0;
            Arrays.sort(score);
            //System.out.println("item + "+ Arrays.toString(score) );

            // 단순하게 최고로 모은 것들만 가장 열심히 담자
            List<int[]> scoreList = new ArrayList<>();
            int[] point = new int[m];
            int index = 0;
            for(int i = score.length - 1; i >= 0; i-- ){
                point[index] = score[i];
                index++;
                if( index == m ){
                    //System.out.println("min + "+score[i]);
                    index = 0;
                    scoreList.add(point);
                    point = new int[m];
                }
            }

            for(int[] items : scoreList){
                //System.out.println("item + "+ Arrays.toString(items) );
                int min = Arrays.stream(items).min().orElseThrow();
                //System.out.println("min + "+min);
                answer += (min*m);
            }


            return answer;
        }
    }
}
