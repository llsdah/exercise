package org.example.programmers.one;

import org.example.programmers.ProgrammersSolutionSample;

import java.util.*;
import java.util.stream.Collectors;

public class 햄버거만들기 implements ProgrammersSolutionSample {
    @Override
    public boolean solution() {
        return false;
    }


    class Solution {

        public int solution_chatGPT(int[] ingredient) {
            int answer = 0;
            Stack<Integer> stack = new Stack<>();

            for (int i : ingredient) {
                stack.push(i);

                // 스택 상위 4개의 요소가 [1, 2, 3, 1]인지 확인
                if (stack.size() >= 4 &&
                        stack.get(stack.size() - 4) == 1 &&
                        stack.get(stack.size() - 3) == 2 &&
                        stack.get(stack.size() - 2) == 3 &&
                        stack.get(stack.size() - 1) == 1) {

                    // 패턴이 맞으면 요소 4개를 제거
                    stack.pop();
                    stack.pop();
                    stack.pop();
                    stack.pop();

                    answer++;  // 찾은 패턴 수 증가
                }
            }

            return answer;
        }

        public int solution_inefficient(int[] ingredient) {
            int answer = 0;
            List<Integer> ingredientList = Arrays.stream(ingredient)
                    .boxed()
                    .collect(Collectors.toList());

            boolean flag = true;

            while(flag){
                int cnt = 0;
                //System.out.println("==================");
                for(int i = 0; i<ingredientList.size(); i++){
                    if(ingredientList.get(i) == 1 && i <= ingredientList.size()-4
                            && ingredientList.get(i+1) == 2 && ingredientList.get(i+2) == 3
                            && ingredientList.get(i+3) == 1
                    ){
                        cnt = 1;
                        // 4개 삭제 list index 삭제 주의 !!!
                        ingredientList.remove(i);
                        ingredientList.remove(i);
                        ingredientList.remove(i);
                        ingredientList.remove(i);
                        break;
                    }

                }

                if(cnt >= 1 ){
                    answer ++;
                }else{
                    flag = false;
                }
                //System.out.println("flag : "+flag);
            }


            return answer;
        }
    }
}
