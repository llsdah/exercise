package org.example.linear.practice;

import java.util.HashMap;
import java.util.Stack;

/**
 * 괄호 짝을 검사해 봅시다
 * {} () []
 *
 * {([})] => fail , stack이 한개여야 한다.
 */
public class Practice_4 {

    public static void main(String[] args) {

        System.out.println(solution("[yyyy]-[mm]-[dd]"));
        System.out.println(solution("{([]})"));
        System.out.println(solution("[test (1.3)]"));
    }

    private static boolean solution(String s) {
        Stack<String> stack = new Stack<>();
        boolean checkValue = true;
        HashMap<String,String> map = new HashMap<>();
        map.put("(",")");
        map.put("{","}");
        map.put("[","]");

        for (String item : s.split("")) {
            if (map.containsKey(item)) {
                stack.push(item);
            } else if (map.containsValue(item)) {
                if (stack.isEmpty()) {
                    checkValue = false;
                    break;
                } else {
                    String cur = stack.pop();
                    if (!item.equals(map.get(cur))){
                        checkValue = false;
                        break;
                    }
                }
            }
        }

        if (checkValue && stack.isEmpty()) {
            return true;
        } else {
            return false;
        }

    }
}
