package org.example.linear.practice.array;

/**
 * 배열에서 타켓에 해당하는 값의 인덱스를 출력하는것
 * 값이 여러개면 가장 큰인덱스 출력
 * 입력 1,1,100,1,1,1,100
 * 타켓 100
 * 결과 6
 */
public class Practice_2 {
    public static void main(String[] args) {
        int[] arr = {1,1,100,1,1,1,100};
        int target = 100;
        int index = -1;

        for (int i = 0; i < arr.length; i++) {
            if (arr[i] == target) {
                if (i > index) {
                    index = i;
                }
            }
        }

        if (index >= 0) {
            System.out.println("index : " + index);
        }

    }
}
