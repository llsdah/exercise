package org.example.linear.practice2;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.stream.Stream;

/**
 * 진열된 모든 종류의 보석을 적어도 1개 이상 포함하는 가장 짧은 구간을 찾아서 구매
 * 모든보석을 포함하는 가장 짧은 구간을 반환
 * 가장짧은 구간이 여러개변 시작숫자가 제일 짧은 것
 */
public class Practice_5 {

    public static void main(String[] args) {
        String[] gems = {"D","B","B","D","D","E","S","D"};
        System.out.println(Arrays.toString(solution(gems)));


        gems = new String[]{"AA","AB","AC","AA","AC"};
        System.out.println(Arrays.toString(solution(gems)));

        gems = new String[]{"XYZ","XYZ","XYZ"};
        System.out.println(Arrays.toString(solution(gems)));

        gems = new String[]{"ZZZ","YYY","NNNN","YYY","BBB"};
        System.out.println(Arrays.toString(solution(gems)));

    }

    private static int[] solution(String[] gems) {
        int[] answer = {1,1};
        ArrayList<ArrayList<Integer>> result = new ArrayList<>();

        HashSet<String> set = new HashSet<>();
        Stream.of(gems).forEach(x -> set.add(x));

        // 전체 보석 갯수
        int n = set.size();

        if (n == 1) {
            result.add(new ArrayList<>(Arrays.asList(1,1)));
            return answer;
        }

        Hashtable<String,Integer> ht = new Hashtable<>();
        boolean isCandidate = false;

        int left = 0;
        int right = 0;

        ht.put(gems[0],1);

        while(true) {
            // 갯수 만족이 안됨 이동
            if (isCandidate == false) {
                right += 1;
                // 보석 없음
                if (right >= gems.length) {
                    break;
                }

                if (ht.containsKey(gems[right]) == false) {
                    ht.put(gems[right],1);
                } else {
                    ht.put(gems[right],ht.get(gems[right])+1);// 보석 하나씩 추가
                }

                if (ht.size() == n) {
                    isCandidate = true;

                    // 0부터 시작해서 1씩 증가
                    result.add(new ArrayList<>(Arrays.asList(left+1,right+1)));
                }
            } else {
                left += 1 ;
                int cnt = ht.get(gems[left-1])-1; // 보석갯수 한개씩 뺴기

                if (cnt == 0) {
                    ht.remove(gems[left-1]);
                    isCandidate = false;
                } else {
                    ht.put(gems[left-1],cnt);
                    result.add(new ArrayList<>(Arrays.asList(left+1,right+1)));
                }
            }
        }

        int minIdx = 0;
        int minNum = Integer.MAX_VALUE;
        for (int i = 0; i < result.size(); i++) {
            ArrayList<Integer> cur = result.get(i);
            left = cur.get(0);
            right = cur.get(1);

            if (right - left < minNum) {
                minNum = right - left;
                minIdx = i;
            }

        }

        ArrayList<Integer> list = result.get(minIdx);
        answer[0] = list.get(0);
        answer[1] = list.get(1);

        return answer;
    }


}















