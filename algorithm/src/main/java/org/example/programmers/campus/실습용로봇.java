package org.example.programmers.campus;

public class 실습용로봇 {
    public int[] solution_my(String command) {
        // 오 왼 전 후
        int x = 0;
        int y = 0;
        int[][][] move = {
                { {0,1}, {0,-1} },
                { {1,0}, {-1,0} },
                { {0,-1}, {0,1} },
                { {-1,0}, {1,0} }
        };
        int dir = 0;
        for (char c : command.toCharArray()) {
            if ( c == 'R') {
                dir ++ ;
                if ( dir == 4) dir = 0;
            } else if ( c == 'L') {
                dir --;
                if (dir == -1) dir = 3;
            } else if ( c == 'G') {
                x += move[dir][0][0];
                y += move[dir][0][1];
            } else { // B
                x += move[dir][1][0];
                y += move[dir][1][1];
            }
        }

        int[] answer = {x,y};

        return answer;
    }

    public int[] solution(String command) {
        int dir = 0;
        int[] dx = {0,1,0,-1};
        int[] dy = {1,0,-1,0};
        int cur_x = 0;
        int cur_y = 0;
        for (int i = 0; i < command.length();i++) {
            char c = command.charAt(i);
            if (c == 'R') {
                dir = (dir +1) % 4;
            } else if (c == 'L') {
                dir = (dir +3) % 4;
            } else if (c == 'G') {
                cur_x += dx[dir];
                cur_y += dy[dir];
            } else if (c == 'B') {
                cur_x -= dx[dir];
                cur_y -= dy[dir];
            }

        }

        return new int[]{cur_x,cur_y};
    }

}
