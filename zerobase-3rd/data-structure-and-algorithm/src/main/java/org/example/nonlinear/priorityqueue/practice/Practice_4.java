package org.example.nonlinear.priorityqueue.practice;

import java.util.*;
import java.util.PriorityQueue;

/**
 * 문자열 이 주어졌을때, 문자열 내에 동일 한 알파벳이 연속적으로 배치되지 않도록 재배치 하십시오
 * 재배치가 가능한 경우 재정렬한 문자열을 반환하고 불가능한 경우 null을 반환하세요
 *
 * 입력 "aabb"
 * 출력 "abab"
 *
 * 입력 "aaca"
 * 출력 null
 *
 */

public class Practice_4 {

    public static void main(String[] args) {
        System.out.println(solution("aaa"));
        System.out.println(solution("aaaaabccd"));
        System.out.println(solution("aaca"));
    }

    private static String solution(String s) {
        HashMap<String,Integer> map = new HashMap<>();
        for ( String item : s.split("") ) {
            map.put(item, map.getOrDefault(item, 0) + 1);
        }

        PriorityQueue<Map.Entry<String,Integer>> pq =
                new PriorityQueue<Map.Entry<String, Integer>>((x,y) -> y.getValue() - x.getValue());

        for (Map.Entry<String,Integer> item : map.entrySet()) {
            pq.offer(item);
        }

        StringBuffer sb = new StringBuffer();
        Map.Entry<String,Integer> prev = null;
        while(!pq.isEmpty()) {
            Map.Entry<String,Integer> cur = pq.poll();

            if (prev != null && prev.getValue() > 0) {
                pq.offer(prev);
            }

            sb.append(cur.getKey());
            cur.setValue(cur.getValue()-1);

            prev = cur;
            if (pq.isEmpty() && prev.getValue() > 0) {
                return null;
            }

        }

        return sb.toString();
    }
}
