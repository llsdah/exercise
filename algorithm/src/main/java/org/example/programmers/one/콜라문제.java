package org.example.programmers.one;

import java.util.*;
import org.example.programmers.ProgrammersSolutionSample;

public class 콜라문제 implements ProgrammersSolutionSample {
    @Override
    public boolean solution() {
        return false;
    }

    class Solution {
        public int solution(int a, int b, int n) {
            int answer = 0;
            int bottle = n;
            int returnBottle = 0;
            while(true){

                int quotient = (bottle+returnBottle)/a*b; // 새로받은병
                int remain = (bottle+returnBottle)%a; // 빈병
                //System.out.println("quotient : "+quotient+", remain : "+remain+", bottle : "+bottle+", returnBottle : "+returnBottle+", answer : "+answer);
                answer += (quotient);

                bottle = quotient;
                returnBottle =remain;

                if (bottle+returnBottle < a){
                    break;
                }
            }

            return answer;
        }
    }

}
