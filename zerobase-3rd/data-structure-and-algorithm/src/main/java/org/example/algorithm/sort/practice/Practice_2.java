package org.example.algorithm.sort.practice;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

/**
 * 문자열 배열 strs 가 주어졌을 때 anagram 으로 묶어서 출력하는 프로그램 작성
 * anagram 절차 순서 바꾸면 같아지는 문자
 *
 * 입력
 * {"eat","tea","tan","ate","nat","bat"}
 *
 * 출력
 * [["eat,"tea","ate"],["bat"],["tan","nat"]]
 *
 */
public class Practice_2 {

    public static void main(String[] args) {
        String[] strs = {"eat","tea","tan","ate","nat","bat"};
        System.out.println("strs = " + Arrays.toString(strs));

        System.out.println("strs = " + solution(strs));

        strs = new String[]{"abc","bac","bca","xyz","zyx","aaa"};
        System.out.println("strs = " + Arrays.toString(strs));
        solution(strs);
        System.out.println("strs = " + solution(strs));

    }

    private static ArrayList<ArrayList<String>> solution(String[] strs) {
        if (strs == null || strs.length == 0) {
            return new ArrayList<>();
        }
        HashMap<String, ArrayList<String>> map = new HashMap<>();

        for (String s : strs) {
            char[] cArr = s.toCharArray();
            sort(cArr);
            String key = String.valueOf(cArr);

            if (!map.containsKey(key)) {
                map.put(key, new ArrayList<>());
            }
            map.get(key).add(s);

        }


        return new ArrayList<>(map.values());
    }

    // 삽입 정렬로 데이터 정렬
    private static void sort(char[] arr) {
        for (int i = 1; i < arr.length ; i++) {
            for (int j = i; j > 0; j--) {
                if (arr[j] <arr[j-1]) {
                    char temp = arr[j];
                    arr[j] = arr[j-1];
                    arr[j-1] = temp;
                }
            }
        }

    }


}
