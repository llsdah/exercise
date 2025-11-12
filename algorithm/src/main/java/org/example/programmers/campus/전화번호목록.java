package org.example.programmers.campus;

import java.util.*;
public class 전화번호목록 {
    public boolean solution(String[] phone_book) {
        boolean answer = true;
        int n = phone_book.length;
        Arrays.sort(phone_book);
        for(int i = 0; i < n-1; i++) {
            if(phone_book[i+1].startsWith(phone_book[i])) return false;
            //for (int k = i+1; k< n;k++) {
            //   if(phone_book[k].startsWith(phone_book[i])) return false;
            //}
        }
        return answer;
    }
}
