package org.example.linear.practice2;

import java.util.LinkedList;
import java.util.Queue;

/**
 * 프린터기기 순서대로 출력
 * 단, 중요도 고려합니다.
 * 중요도 찾고 우측부터 재 순차 배열을 수행합니다.
 *
 */
public class Practice_1 {
    public static void main(String[] args) {
        int docs = 1;
        int target = 0;
        int[] priority = {5};
        solution(docs, target, priority);
        docs = 6;
        target = 0;
        priority = new int[]{1,1,9,1,1,1};
        solution(docs, target, priority);
    }


    private static void solution(int docs, int target, int[] priority) {
        Queue<Doc> que = new LinkedList<>();
        for (int i = 0; i < docs; i++) {
            que.add(new Doc(i, priority[i]));
        }
        int cnt = 1;
        while (true) {
            Doc cur = que.poll();

            // 우선순위 대로 고려한다.
            Doc[] highPriority = que.stream().filter(x -> x.priority > cur.priority).toArray(Doc[]::new);

            // 높은거 있다 사용
            if (highPriority.length > 0) {
                que.add(cur);
                // 현재 무서 출력
            } else {
                // 없으면 출력  방금 꺼낸 문서가 보냄
                if (cur.no == target) {
                    System.out.println("cnt + "+ cnt);
                    break;
                }
                cnt++;
            }
        }
    }


    static class Doc{
        int no;
        int priority;

        public Doc(int no, int priority) {
            this.no = no;
            this.priority = priority;
        }
    }

}
