package org.example.programmers.campus;

public class 타켓넘버 {
    private int answer = 0;
    public int solution(int[] numbers, int target) {
        target(numbers, 0, 0, target);
        return answer;
    }

    public void target(int[] numbers, int index, int sum, int target) {
        if ( index == numbers.length) {
            if (target == sum) answer++;
            return;
        }

        target(numbers, index+1, sum-numbers[index], target);
        target(numbers, index+1, sum+numbers[index], target);
    }
}
