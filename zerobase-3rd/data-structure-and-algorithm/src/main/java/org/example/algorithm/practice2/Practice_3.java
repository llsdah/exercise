package org.example.algorithm.practice2;

import java.util.Arrays;

/**
 * 무게 측정 양팔 저울
 * 한쪽은 특정할 무게
 * 반대는 가진 추들의 무게 놓는곧
 * 측정이 불가능한 최소값 출력
 *
 *
 * input {6,1,2,3,1,7,30}
 * output 21
 */
public class Practice_3 {

    // 이진 탐색 - 시간이 걸림
    private static void solution(int[] weight) {

        Arrays.sort(weight);

        int cur = 0;
        // 순간의 최선 
        for (int i = 0; i < weight.length; i++) {
            if (cur + 1 < weight[i]) {
                break;
            }

            cur += weight[i];
        }

        cur++;
        System.out.println("cur = " + cur);
    }

    public static void main(String[] args) {
        int[] weight = {6,1,2,3,1,7,30};
        solution(weight);
    }

}
