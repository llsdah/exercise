package org.example.mathematics.practice;

import java.lang.reflect.Array;
import java.util.*;
import java.util.zip.ZipFile;

/**
 * a와 b가 주어 질때, a가 b의 부분문자열인지 여부
 * 단, a는 permutation 가능
 */

public class Practice_3 {

    public static void permutation(char[] arr, int depth, int n, int r, boolean[] visited, char[] out, ArrayList<String> list) {
        if (depth == n) {
            list.add(new String(out));
        }

        for (int i = 0; i < n; i++) {
            if (visited[i] != true) {
                visited[i] = true;
                out[depth] = arr[i];
                permutation(arr, depth + 1, n, r, visited, out, list);
                visited[i] = false;
            }
        }
    }

    // permutation 방법으로 해결
    public static boolean solution1(String a, String b) {
        boolean[] visited = new boolean[a.length()];
        char[] out = new char[a.length()];
        ArrayList<String> list = new ArrayList<>();
        permutation(a.toCharArray(), 0, a.length(), a.length(), visited,out, list);

        for (String s : list) {
            if (b.contains(s)) {
                return true;
            }
        }

        return false;
    }
    // 문제 규칙을 찾아서 해결
    // 영문자 배열 생성 후 b를 돌면서 갯수 확인 단, a 길이 대로 잘라서 인접한 부분을 생각
    public static boolean solution2(String a, String b) {
        final int ALPHABET = 26;

        if (a.length() > b.length()) {
            return false;
        }
        int[] cnt = new int[ALPHABET];
        // a의 갯수만큼 증가
        for (int i = 0; i < a.length(); i++) {
            cnt[a.charAt(i)-'a']++;
        }

        for (int i = 0; i < b.length(); i++) {
            cnt[b.charAt(i)-'a']--;
            if (i - a.length() >= 0 ) {
                cnt[b.charAt(i-a.length())-'a' ]++;
            }

            boolean zero = true;
            // 전체가 0 이면 끝 아니면 종료대기
            for (int j = 0; j < ALPHABET; j++) {
                if (cnt[j] != 0) {
                    zero = false;
                    break;
                }
            }

            if (zero) {
                return true;
            }
        }

        return false;
    }


    public static void main(String[] args) {
        String a = "ab";
        String b = "adbak";
        System.out.println(solution1(a,b));
        System.out.println(solution2(a,b));

        a = "ac";
        b = "car";
        System.out.println(solution1(a,b));
        System.out.println(solution2(a,b));
        a = "ak";
        b = "aabbkk";
        System.out.println(solution1(a,b));
        System.out.println(solution2(a,b));




    }
}
