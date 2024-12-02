package org.example.linear.list.practice.array;

/**
 * 배열의 모든데이터에 대해 짝수 홀수 데이터 평균을 출력한다
 * 입력 : 1,2,3,4,5,6,7,8,9
 * 결과 : 5.0 , 5.0
 * 
 */

public class Practice_1 {

    public static void main(String[] args) {
        int[] arr = {1,2,3,4,5,6,7,8,9};
        float sumEven = 0;
        float sumOdd = 0;
        int evenCnt = 0;
        int oddCnt = 0;
        
        for (int item : arr) {
            if (item % 2 == 0) {
                sumEven += item;
                evenCnt ++;
            } else {
                sumOdd += item;
                oddCnt ++;
            }
        }

        System.out.println("짝수 평균 "+ (sumEven/evenCnt));
        System.out.println("훌수 평균 "+ (sumOdd/oddCnt));
    }
    
}
