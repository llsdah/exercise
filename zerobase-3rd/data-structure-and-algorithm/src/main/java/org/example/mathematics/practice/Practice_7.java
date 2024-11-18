package org.example.mathematics.practice;
import java.util.*;

/**
 * 회문 펠린드롬
 * 앞뒤가 같은 것
 *
 * 유사 회문
 * 특별한 단어 하나 제거하면 회문이 되는것
 */
public class Practice_7 {

    // 양 끝방향제거
    private static int solution (String str) {

        return isPalindrome(0,str.length()-1,str.toCharArray(),0);
    }

    // 하나만 지워서 가능한지 여부 확인
    private static int isPalindrome(int left, int right, char[] arr, int delCnt) {

        while (left < right) {
            if (arr[left] != arr[right]) {
                if (delCnt == 0) {
                    if (isPalindrome(left +1, right,arr,1)==0
                            || isPalindrome(left, right -1 , arr, 1) ==0) {
                        return 1;
                    } else {
                        return 2;
                    }
                } else {
                    return 2;
                }
            } else {
                left++;
                right--;
            }
        }

        return 0;
    }

    public static void main(String[] args) {
        System.out.println(solution("abba"));
        System.out.println(solution("summuus"));
        System.out.println(solution("xabba"));
        System.out.println(solution("xabbay"));
    }
}
