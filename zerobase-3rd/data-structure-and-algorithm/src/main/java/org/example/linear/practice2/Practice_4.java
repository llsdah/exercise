package org.example.linear.practice2;

import java.util.Hashtable;

/**
 * 카카오 문제 완주하지 못한 사람
 *  - 동명이인 가능성!
 */
public class Practice_4 {


    public static void main(String[] args) {
        String[] participant = {"leo","kiki","eden"};
        String[] completion = {"eden","kiki"};
        System.out.println(solution(participant,completion));

        participant = new String[]{"m","j","n","v","f"};
        completion = new String[]{"j","f","m","n"};
        System.out.println(solution(participant,completion));

        participant = new String[]{"m","s","m","a"};
        completion = new String[]{"s","a","m"};
        System.out.println(solution(participant,completion));


    }

    private static String solution(String[] participant, String[] completion) {
        String result = "";
        Hashtable<String,Integer> ht = new Hashtable<>();
        for (String item : participant) {
            if (ht.containsKey(item)) {
                ht.put(item, ht.get(item) + 1);
            } else {
                ht.put(item, 1);
            }
        }

        for (String item : completion) {
            ht.put(item, ht.get(item) -1);
        }

        for (String item : participant) {
            if (ht.get(item) > 0) {
                result = item;
                break;
            }
        }

        return result;
    }
}
