package org.example.programmers.campus;

public class 자연수뒤집어배열로만들기 {
    public int[] solution(long n) {
        String str = n + "";
        System.out.println(str);
        int[] answer = new int[str.length()];

        for(int i = str.length()-1; i>=0;i--){
            answer[str.length()-1-i] = str.toCharArray()[i]-'0';
        }

        return answer;
    }
}
