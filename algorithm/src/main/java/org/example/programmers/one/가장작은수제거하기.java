package org.example.programmers.one;

public class 가장작은수제거하기 {

    class Solution {
        public int[] solution(int[] arr) {
            int[] answer = new int[arr.length-1];

            int min = Integer.MAX_VALUE;
            int minIndex = -1;

            for(int i = 0; i<arr.length; i++) {
                if (arr[i] < min) {
                    min = arr[i];
                    minIndex = i;
                }
            }

            arr[minIndex] = -1;

            if(arr.length == 1) {
                return arr;
            } else {
                int idx = 0;
                for(int i = 0; i<arr.length;i++){
                    if(arr[i] != -1) {
                        answer[idx++] = arr[i];
                    }
                }
            }

            return answer;
        }
    }
}
