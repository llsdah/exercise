package org.example.programmers.one;

import org.example.programmers.ProgrammersSolutionSample;
import java.util.*;

public class 성격유형검사하기 implements ProgrammersSolutionSample {
    @Override
    public boolean solution() {
        return false;
    }

    class Solution {
        public String solution(String[] survey, int[] choices) {
            String answer = "";
            // "RT" "CF" "JM" "AN"
            int[] points = new int[8];
            // 1-비동의3 2-비동의2 3-비동의1 4-빵쩜 5-동의"-1"...
            HashMap<Character,Integer> map = new HashMap<>();
            map.put('R',0);
            map.put('T',1);
            map.put('C',2);
            map.put('F',3);
            map.put('J',4);
            map.put('M',5);
            map.put('A',6);
            map.put('N',7);

            HashMap<Integer,Integer> point = new HashMap<>();
            point.put(1,3);
            point.put(2,2);
            point.put(3,1);
            point.put(4,0);
            point.put(5,-1);
            point.put(6,-2);
            point.put(7,-3);

            for (int i = 0; i < survey.length; i++) {
                char[] name = survey[i].toCharArray();
                points[map.get(name[0])] += point.get(choices[i]);
            }
            // "RT" "CF" "JM" "AN"
            String RT = points[0] >= points[1] ? "R" : "T";
            String CF = points[2] >= points[3] ? "C" : "F";
            String JM = points[4] >= points[5] ? "J" : "M";
            String AN = points[6] >= points[7] ? "A" : "N";
            //System.out.println("point : "+Arrays.toString(points));
            answer = RT + CF + JM + AN ;

            return answer;
        }
    }
}
