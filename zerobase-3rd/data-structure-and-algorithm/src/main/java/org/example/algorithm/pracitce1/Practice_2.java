package org.example.algorithm.pracitce1;

/**
 * 회전 초밥집 이벤트
 * 손님이 처음선택한 접시위치로 부터연속해서 n개의 접시를 골라 식사하면 할인
 * 추가로 쿠폰이 제공되는데 해당 쿠폰은 적혀있는 번호의 초밥을 즉석에서 만들어 제공
 * 손님이 가장 많은 종류의 초밥을 먹을 수 있는 최대값 구하기
 *
 *
 * n = 4
 * plates = {7,9,7,30,2,7,9,25}
 * type = 30
 * coupon = 30
 *
 */

public class Practice_2 {

    public static void main(String[] args) {
        int n = 4; // 고르는 접시의 수
        int[] plates = {7,9,7,30,2,7,9,25}; // 초밥종류
        int type = 30; // 초밥종류이 수
        int coupon = 30; // 쿠폰으로 주는 초밥

        // 투포인터를 통해ㅓ 사용
        solution(n,plates,type,coupon);
    }

    private static void solution(int n, int[] plates, int type, int coupon) {

        int[] selected = new int[type + 1]; // 몇번 접시를 골랐는지 체크
        int cnt = 0;
        int max = 0; // 종류의 최대 몇개

        for (int i = 0; i < n; i++) {
            if (selected[plates[i]] == 0) {
                cnt ++;
            }
            selected[plates[i]] ++;
        }

        max = cnt;

        for (int i = 1; i < plates.length; i++) {

            if (max <= cnt) {
                if (selected[coupon] == 0) {
                    max = cnt + 1;
                } else {
                    max = cnt;
                }
            }

            int p1 = i - 1;
            int p2 = (i+n-1) % plates.length;

            selected[plates[p1]]--;
            if (selected[plates[p1]] == 0) {
                cnt--;
            }

            if (selected[plates[p2]] == 0) {
                cnt++;
            }
            selected[plates[p2]]++;
        }

        System.out.println("answer : "+ max);
    }

}
