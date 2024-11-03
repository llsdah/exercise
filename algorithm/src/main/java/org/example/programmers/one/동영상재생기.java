package org.example.programmers.one;

import org.example.programmers.ProgrammersSolutionSample;

public class 동영상재생기 implements ProgrammersSolutionSample {

    public boolean solution(){
        Solution test = new Solution();
        String result = test.solution("34:33","13:00","00:55","02:55",new String[]{"next", "prev"});

        String answer = "";

        return answer.equals(result);
    }
    class Solution {
        public String solution(String video_len, String pos, String op_start, String op_end, String[] commands) {

            String answer = "";

            int[] tempIntArr = new int[2];

            String[] mmss = pos.split(":");
            int mm = Integer.parseInt(mmss[0]);
            int ss = Integer.parseInt(mmss[1]);

            int[] nowTime = {mm,ss};

            mmss = video_len.split(":");
            mm = Integer.parseInt(mmss[0]);
            ss = Integer.parseInt(mmss[1]);

            int[] maxLenTime = {mm,ss};

            mmss = op_start.split(":");
            mm = Integer.parseInt(mmss[0]);
            ss = Integer.parseInt(mmss[1]);

            int[] op_start_time = {mm,ss};

            mmss = op_end.split(":");
            mm = Integer.parseInt(mmss[0]);
            ss = Integer.parseInt(mmss[1]);

            int[] op_end_time = {mm,ss};

            for(int i = 0 ; i < commands.length; i++ ){
                if( checkOverVideo(op_start_time, nowTime) && !checkOverVideo(op_end_time, nowTime)){
                    nowTime[0] = op_end_time[0];
                    nowTime[1] = op_end_time[1];
                }

                if(commands[i].equals("prev")){
                    tempIntArr = returnTimeValue(nowTime[0],nowTime[1],-10);
                }else if (commands[i].equals("next")){
                    tempIntArr = returnTimeValue(nowTime[0],nowTime[1],10);
                }

                nowTime[0] = tempIntArr[0];
                nowTime[1] = tempIntArr[1];

                if(nowTime[0] < 0){
                    nowTime[0] = 0;
                    nowTime[1] = 0;

                }else if ( checkOverVideo(maxLenTime,nowTime) ){
                    nowTime[0] = maxLenTime[0];
                    nowTime[1] = maxLenTime[1];

                }
            }

            if( checkOverVideo(op_start_time, nowTime) && !checkOverVideo(op_end_time, nowTime)){
                nowTime[0] = op_end_time[0];
                nowTime[1] = op_end_time[1];
            }

            answer = ( nowTime[0] / 10 == 0 ? ( nowTime[0] == 0 ? "00" : "0"+nowTime[0] ) : nowTime[0])
                    + ":"
                    + ( nowTime[1] / 10 == 0 ?  ( nowTime[1] == 0 ? "00" : "0"+nowTime[1] ) : nowTime[1] );

            return answer;
        }

        private boolean checkOverVideo(int[] nowMMSS, int[] newMMSS){
            String[] value = {"zero","max","op"};
            if( newMMSS[0] > nowMMSS[0] ){
                return true;
            }else if ( newMMSS[0] == nowMMSS[0] && newMMSS[1] >= nowMMSS[1] ){
                return true;
            }

            return false;
        }

        private int[] returnTimeValue(int nowMM, int nowSS, int ss){
            int[] returnTime = {nowMM,nowSS};
            if ( ss < 0 ) {
                if( nowSS+ss < 0){
                    returnTime[0] = nowMM -1 ;
                    returnTime[1] = 60 + nowSS + ss ;
                }else {
                    returnTime[1] += ss;
                }
            } else {
                if(nowSS+ss >= 60){
                    returnTime[0] = nowMM + 1 ;
                    returnTime[1] = (nowSS+ss) % 60 ;
                }else {
                    returnTime[1] += ss;
                }
            }

            return returnTime;
        }
    }
}
