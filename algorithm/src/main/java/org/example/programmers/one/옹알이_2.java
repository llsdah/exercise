package org.example.programmers.one;

import org.example.programmers.ProgrammersSolutionSample;

public class 옹알이_2 implements ProgrammersSolutionSample {

    @Override
    public boolean solution() {
        return false;
    }

    class Solution {

        public int solution_chatGPT(String[] babbling) {
            int answer = 0;
            /**
             * ^ 문자열의 시작을 의미
             * 해당 단어와 일치하는 패턴인지 확인
             * + 1회의상 반복될 수 있음을 의미
             * $ 문자여르이 끝을 의미
             *
             * 아래 단어 중 하나 이상의 단어로만 구성된 문자열 전체가 해당패턴과 일치하는지 확인하는 역할
             */
            String pattern = "^(aya|ye|woo|ma)+$";

            for (String item : babbling) {
                // 가능한 단어로 구성된지 확인하고, 연속되지 않는지 검사
                if (item.matches(pattern) && !hasConsecutiveWords(item)) {
                    answer++;
                }
            }

            return answer;
        }

        // 연속되는 같은 단어가 있는지 확인하는 메서드
        private boolean hasConsecutiveWords(String item) {
            return item.contains("ayaaya") || item.contains("yeye") ||
                    item.contains("woowoo") || item.contains("mama");
        }


        public int solution_my(String[] babbling) {
            int answer = 0;
            String[] possibleWord = {"aya", "ye", "woo", "ma"};

            for(String item : babbling){

                char[] itemArr = item.toCharArray();
                String beforeWord = "";
                boolean flag = true;
                for(int i = 0; i<itemArr.length;i++){
                    if(!beforeWord.equals(possibleWord[0]) && itemArr[i] == 'a'){
                        if(i+2 <= itemArr.length-1 && itemArr[i+1] == 'y' && itemArr[i+2] == 'a'){
                            beforeWord = possibleWord[0];
                            i = i+2;
                        }else {
                            flag = false;
                        }

                    }else if(!beforeWord.equals(possibleWord[1]) && itemArr[i] == 'y'){
                        if(i+1 <= itemArr.length-1 && itemArr[i+1] == 'e'){
                            beforeWord = possibleWord[1];
                            i = i+1;
                        }else{
                            flag = false;
                        }
                    }else if(!beforeWord.equals(possibleWord[2]) && itemArr[i] == 'w'){
                        if(i+2 <= itemArr.length-1 && itemArr[i+1] == 'o'&& itemArr[i+2] == 'o'){
                            beforeWord = possibleWord[2];
                            i = i+2;
                        }else {
                            flag = false;
                        }
                    }else if(!beforeWord.equals(possibleWord[3]) && itemArr[i] == 'm'){
                        if(i+1 <= itemArr.length-1 && itemArr[i+1] == 'a'){
                            beforeWord = possibleWord[3];
                            i = i+1;
                        }else{
                            flag = false;
                        }
                    }else {
                        flag = false;
                    }
                }
                answer = flag ? answer+1 : answer + 0;

            }

            return answer;
        }
    }

}
