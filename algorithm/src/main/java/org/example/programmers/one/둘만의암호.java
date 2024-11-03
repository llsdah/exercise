package org.example.programmers.one;

import org.example.programmers.ProgrammersSolutionSample;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class 둘만의암호 implements ProgrammersSolutionSample {

    @Override
    public boolean solution() {

        return false;
    }

    class Solution {
        public String solution(String s, String skip, int index) {
            String answer = "";

            char[] strArr = s.toCharArray();
            char[] newArr = new char[s.length()];

            for(int i = 0; i < strArr.length; i++){
                char newChar = shiftAlphabet(strArr[i],skip,index);
                //System.out.println("main"+i+"newChar "+ newChar);
                newArr[i] = newChar;

            }

            answer = new String(newArr);

            return answer;
        }

        private char shiftAlphabet(char c,String skip, int shift){
            char newChar = c;
            char[] skipArr = skip.toCharArray();

            for(int i = 1; i <= shift; i++){
                newChar = (char) ('a'+(newChar - 'a'+1) % 26);
                if( checkSkipPattern(skipArr,newChar) ){
                    i--;
                }
            }

            return newChar;
        }

        private boolean checkSkipPattern(char[] skipArr, char word){
            //char[] skipArr = skip.toCharArray();
            for(int i = 0; i < skipArr.length; i++){
                if(skipArr[i] == word) return true;
            }

            return false;
        }
    }

}
