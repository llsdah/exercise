package org.example.algorithm.binarysearch.practice;

/**
 * 정수형 배열 weight에 정수 days 주어짐
 * 무게와 운송납기일
 * 상품은 무게가 적혀있는 순서대로 실어서 운송해야됨
 * 납기일 이내 운송하기 위한 차량의 최소한의 적재량 계산 필요
 *
 * weight : 1,2,3,4,5,6,7,8,10
 * days : 5
 * output : 15
 *
 *
 * weight : 3,2,2,4,1,4
 * days : 3
 * output : 6
 *
 */

public class Practice_4 {
    // 최소 및 최대의 적재량을 바탕으로 이진 탐색을 수행합니다.
    // 10~55 중간인 32만큼의 무게 차량이 가능하면 이틀내 배송 가능 => 좌측으로
    public static void main(String[] args) {
        int[] weights =  {1,2,3,4,5,6,7,8,9,10};
        int days = 5;

        System.out.println("solution : "+solution(weights,days));
        weights = new int[]{3,2,2,4,1,4};
        days = 3;
        System.out.println("solution : "+solution(weights,days));

    }

    private static int solution(int[] weights, int days) {
        int left = 0;
        int right = 0;

        for (int w : weights) {
            left = Math.max(left, w);
            right +=w;
        }

        while (left <= right) {
            int mid = (left + right) / 2;
            int cnt = 1;
            int cur = 0;

            // mid 값으로 얼마일수가 소요되나
            for (int w : weights) {
                if (cur + w > mid) {
                    cnt +=1; // 필요일수 추가
                    cur = 0; // 적재량 초기화
                }
                cur += w;
            }

            // 몇일이 걸리는 지
            if (cnt > days) {
                // 기준보다 부족
                left = mid + 1;
            } else {
                // 충분
                right = mid - 1;
            }

        }


        return left;
    }

}
