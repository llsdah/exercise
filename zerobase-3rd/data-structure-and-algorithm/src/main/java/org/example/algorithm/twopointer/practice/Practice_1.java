package org.example.algorithm.twopointer.practice;

/**
 * 규칙으로 문자열 정리 후 남은 문자열 반환
 * 양끝쪽의 문자 비교 후 같으면 삭제 + 연속해서 같은 문자 등장시 함께 삭제
 *
 * 최종으로 남은 문자 반환
 *
 * input : ab
 * output : ab
 *
 * input : aaabbaa
 * output : (없음)
 *
 */
public class Practice_1 {

    public static void main(String[] args) {
        System.out.println(solution("ab"));
        System.out.println(solution("aaabbaa"));
        System.out.println(solution("aabcddba")); // cdd
    }

    private static String solution(String s) {
        if (s == null || s.length() == 0) {
            return null;
        }

        int p1 = 0;
        int p2 = s.length()-1;

        while (p1 < p2 && s.charAt(p1) == s.charAt(p2)) {
            char c = s.charAt(p2);

            while (p1 <= p2 && s.charAt(p1) == c) {
                p1++;//우측이동
            }

            while (p1 <= p2 && s.charAt(p2) == c) {
                p2--;
            }


        }

        return s.substring(p1,p2 + 1);
    }

}
