package org.example.algorithm.pracitce1;

import java.util.ArrayList;

/**
 * N구 멀티탭 하나의 K개의 전지 제품이 있을떄,
 * K개 전기제품 사용순서가 items(2,3,2,3,1,2,7) 제품 순으로 사용할때
 * 전지제품사용을 위해 멀티탭에 교체하는 수가 최소가 되는 값을 구하는 프로그램
 *
 * n = 2
 * items = {2,3,2,3,1,2,7}
 *
 *
 */
public class Practice_3 {
    public static void main(String[] args) {
        int n = 2;
        int[] items = {2,3,2,3,1,2,7};
        solution(n,items);
    }

    private static void solution(int n, int[] items) {
        boolean[] used = new boolean[items.length+1];

        int index = 0;
        int cnt = 0;

        // 초기 입력 셋팅
        while (cnt < n) {
            if (!used[items[index]]) {
                used[items[index]] = true;
                cnt ++;
            }
            index ++;
        }


        int result = 0;

        while(index < items.length) {
            if (!used[items[index]]) {
                ArrayList<Integer> list = new ArrayList<>();
                // 사용하게 될 것 담기
                for (int i = index; i < items.length; i++) {
                    if (used[items[i]] && !list.contains(items[i])) {
                        list.add(items[i]);
                    }
                }

                // 모두 사용하는 경우
                if (list.size() == n) {
                    // 나중에 사용하는 것을 뺴야합니다.
                    used[list.get(n-1)] = false;
                } else {
                    // 둘중하나 혹은 둘다 안쓰는 경우
                    for (int i = 1; i < items.length; i++) {
                        if (used[i] && !list.contains(i)) {
                            // 사용중인것 빼기 
                            used[i] = false;
                            break;
                        }
                    }
                }

                // 현재 사용할 위치에 것 true
                used[items[index]] = true;
                // 교체 수 +1
                result ++;
            }
            // 이동
            index++;

        }

        System.out.println("result = " + result);
    }
}




