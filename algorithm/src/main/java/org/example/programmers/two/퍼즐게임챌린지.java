package org.example.programmers.two;

import org.example.programmers.ProgrammersSolutionSample;
import java.util.*;

public class 퍼즐게임챌린지 implements ProgrammersSolutionSample {

    @Override
    public boolean solution() {
        return false;
    }

    class Solution {
        public int solution_chatGPT(int[] diffs, int[] times, long limit) {
            int maxLevel = Arrays.stream(diffs).max().orElseThrow(() -> new IllegalArgumentException("error"));

            // 탐색의 범위 설정
            int left = 1;
            int right = maxLevel;
            int answer = maxLevel;

            while (left <= right) {

                // 오버플로우 방지를 위한 중간값 계산.
                int mid = left + (right - left) / 2;

                if (isPossible(diffs,times,limit,mid)) {
                    answer = mid;
                    right = mid - 1;
                } else {
                    left = mid + 1;
                }

            }

            return answer;
        }

        private boolean isPossible(int[] diffs, int[] times, long limit, int level) {
            long total = times[0];
            for (int i = 1; i < diffs.length; i++){
                int diff = diffs[i];
                int timeCur = times[i];
                int timePrev = times[i-1];
                if (diff <= level) {
                    total += timeCur;
                } else {
                    int cnt = diff - level;
                    total += ((cnt * (timePrev + timeCur)) + timeCur);
                }
                if (total > limit) {
                    return false;
                }
            }
            return true;
        }

        // 시간초과 발생으로 재수행
        public int solution_time_exceed(int[] diffs, int[] times, long limit) {
            int answer = 0;

            //if (diffs[1]>1000) return 1;

            int maxLevel = Arrays.stream(diffs).max().orElseThrow(() -> new IllegalArgumentException("Array is empty"));

            int level = maxLevel;
            //int level = 1;
            while(true){
                long total = times[0];
                for (int i = 1; i < diffs.length; i++) {
                    int diff = diffs[i];
                    int time_cur = times[i];
                    int time_prev = times[i-1];
                    if (diff <= level) {
                        total += time_cur;
                    } else {
                        int cnt = diff - level;
                        total += ((cnt * (time_prev+time_cur))+time_cur);
                    }

                }
                if (total > limit) {
                    break;
                } else {
                    level--;
                }
            }

            // 마지막에 diff[0] 더하기
            if( level < 1){
                return 1;
            }
            answer = level + 1;
            return answer;
        }
    }
}
