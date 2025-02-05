package org.example.algorithm.twopointer.practice;

/**
 * 문자열 s를 거꾸로 출력
 * 단 각 단어의 알파벳 순서는 그대로 출력
 * 문자 공백이 여러개 일 경우 하나의 공백을 제외한 나머지 공백 제거
 *
 *
 * input : the sky is blue
 * output : blue is sky the
 *
 *
 * input : hello java
 * output : java hello
 *
 */
public class Practice_3 {

    public static void main(String[] args) {
        String s = "the sky is blue";
        System.out.println(solution(s));
        s = "   hello           java    ";

        System.out.println(solution(s));

    }

    private static String solution(String s) {
        if (s == null) {
            return null;
        }
        if (s.length() < 2) {
            return s;
        }

        s = removeSpace(s);

        char[] cArr = s.toCharArray();

        reverseString(cArr,0,s.length()-1);
        reverseWords(cArr,s.length());

        return new String(cArr);
    }

    private static void reverseWords(char[] cArr, int length) {
        int p1 = 0;
        int p2 = 0;

        while ( p1 <length) {
            while (p1 < p2 || p1 < length && cArr[p1] == ' ') {
                p1++;
            }

            while (p2 < p1 || p2 < length && cArr[p2] != ' ') {
                p2 ++;
            }

            reverseString(cArr,p1,p2-1);
        }
    }

    private static void reverseString(char[] cArr, int i, int j) {

        while (i < j) {
            // 양 끝을 바꾸는 구조
            char temp = cArr[i];
            cArr[i++] = cArr[j];
            cArr[j--] = temp;
        }

    }

    // 공백 제거
    private static String removeSpace(String s) {
        int p1 = 0;
        int p2 = 0;

        char[] cArr = s.toCharArray();
        int length = s.length();

        while (p2 < length) {
            // 한 단어의 앞뒤로 공백 제거
            while (p2 < length && cArr[p2] == ' ') {
                p2 ++;
            }
            while (p2 < length && cArr[p2] != ' ') {
                cArr[p1++] = cArr[p2++];
            }
            while (p2 < length && cArr[p2] == ' ') {
                p2++;
            }

            //
            if (p2 < length) {
                cArr[p1++] = ' '; // 공백이 필요하기에 추가
            }

        }

        System.out.println("reverse space p1 = " + p1 + ", p2 = "+p2 );
        return new String(cArr).substring(0,p1);
    }


}
