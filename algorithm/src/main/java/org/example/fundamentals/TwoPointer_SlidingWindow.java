package org.example.fundamentals;

import java.util.*;

/**
 * PCCP Level 3 - Two Pointer / Sliding Window 문제
 *
 * "연속 부분합 최소 길이"
 *
 * 요구사항:
 *  - 부분 배열의 합이 S 이상이 되는 가장 짧은 길이 구하기
 *  - 없다면 0 출력
 *
 * 알고리즘:
 *  - 배열이 모두 양수이므로 Two Pointer(=Sliding Window) 가능
 *  - 현재 합 < S → right 증가시켜 구간 확장
 *  - 현재 합 >= S → 최소 길이 갱신 후 left 증가하여 구간 축소
 *  - 전체적으로 O(N) 시간복잡도 보장
 *
 * 핵심 요약
 * - 모든 원소가 양수 → Two Pointer 완벽 적용 가능
 * - 구간을 확장/축소하며 최적 길이 찾기
 * - O(N)으로 대규모 입력에서도 빠르게 동작
 */


public class TwoPointer_SlidingWindow {

    public int solution(long[] arr, int S) {
        // Two pointer 를 위한 변수
        int left = 0;
        int sum = 0;
        int minLen = Integer.MAX_VALUE;

        for (int right = 0; right < arr.length; right++) {
            // 현재 구간 합에 arr[right] 추가 (윈도우 확장)
            sum += arr[right];

            // 합이 S 이상인 동안 left를 밀어서 최소 길이 갱신
            while(sum >= S) {
                int len = right - left + 1;
                if (len < minLen) {
                    minLen = len;
                }
                // left를 한칸 오른쪽으로 이동하며 윈도우 축소
                sum -= arr[left];
                left++;
            }
        }

        // 조건을 만족하는 부붑 배열이 없는 경우
        if (minLen == Integer.MAX_VALUE) {
            return -1;
        } else {
            return minLen;
        }

    }
}









