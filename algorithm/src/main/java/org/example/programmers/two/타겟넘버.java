package org.example.programmers.two;

import java.util.*;
public class 타겟넘버 {
    class Solution {

        int cnt = 0;
        public int solution(int[] numbers, int target) {
            int answer = 0;

            // 어떤 수든 2가지가 있다. 음수 양수
            // 모든 경우의 수를 다 써야한다. 결과를 만들어야함

            temp(numbers, target, 0, 0);
            //System.out.println(cnt);
            return cnt;
        }

        public void temp(int[] arr, int target , int idx, int sum) {

            if(arr.length == idx) {

                if(target == sum) {
                    cnt ++;
                }
                return;
            }

            temp(arr, target , idx+1, sum + arr[idx]);
            temp(arr, target , idx+1, sum - arr[idx]);
        }
    }
}
