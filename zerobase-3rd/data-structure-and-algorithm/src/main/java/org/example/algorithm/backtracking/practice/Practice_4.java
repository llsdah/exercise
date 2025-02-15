package org.example.algorithm.backtracking.practice;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

/**
 * 2차원 배열
 * 해당 배열에는 'o 동전', '# 벽', '. 빈칸' 이 담겨 있다.
 *
 * 동전은 항상 두개
 * 두 동전이 함꼐 이동하다 하나가 보도에서 떨어지는 경우의 최소이동 횟수를 출력하는 플그램 작성
 * 단, 규칙
 *  동전은 좌우 위 아래 이동 같은 방향 함꼐 이동
 *  빈칸이나 동전이 있는 칸으로 이동 가능
 *  벽일떄 이동 불가
 *  이동 횟수가 10번 넘으로 중지 -1 반환
 *
 * input {{'.','#'},{'.','#'},{'.','#'},{'o','#'},{'o','#'},{'#','#'}}
 * output 4
 *
 *
 */
public class Practice_4 {

    final static int[][] dirs = {
            {1,0},
            {-1,0},
            {0,1},
            {0,-1}
    };
    static int cnt;
    // 동전 위치
    static class Coin {
        int x;
        int y;

        public Coin(int x, int y) {
            this.x = x;
            this.y = y;
        }
    }

    private static int solution(char[][] board) {
        if (board == null || board.length == 0){
            return -1;
        }

        int n = board.length; // 행
        int m = board[0].length; //열
        
        cnt = Integer.MAX_VALUE;
        
        ArrayList<Coin> coins = new ArrayList<>();

        for (int i = 0; i < n; i++) {
            for (int j = 0; j < m; j++) {
                if (board[i][j] == 'o') {
                    coins.add(new Coin(j,i));
                }
            }
        }
        
        // coin 은 항상 2개
        Coin coin1 = coins.get(0);
        Coin coin2 = coins.get(1);
        
        // backTracking
        backTracking(board, m, n, coin1.x, coin1.y, coin2.x, coin2.y,0);

        cnt = cnt == Integer.MAX_VALUE ? -1 : cnt;

        return cnt;
    }

    public static void backTracking(char[][] board, int m, int n, int x1, int y1, int x2, int y2, int moveCnt) {
        if (moveCnt >= 10) {
            return;
        }

        for (int[] dir : dirs) {
            int x1Next = x1 + dir[0];
            int y1Next = y1 + dir[1];

            int x2Next = x2 + dir[0];
            int y2Next = y2 + dir[1];

            int dropCnt = 0;

            if (x1Next < 0 || x1Next >= m || y1Next < 0 || y1Next >= n) {
                dropCnt ++;
            }

            if (x2Next < 0 || x2Next >= m || y2Next < 0 || y2Next >= n) {
                dropCnt ++;
            }

            if (dropCnt == 2) {
                continue;
            }

            if (dropCnt == 1) {
                cnt = Math.min(cnt, moveCnt + 1);
                return;
            }

            if (board[y1Next][x1Next] == '#') {
                x1Next = x1;
                y1Next = y1;
            }

            if (board[y2Next][x2Next] == '#') {
                x2Next = x2;
                y2Next = y2;
            }

            backTracking(board, m, n, x1Next, y1Next, x2Next, y2Next, moveCnt + 1);

        }

    }


    public static void main(String[] args) {
        char[][] board = {
                {'.','#'},
                {'.','#'},
                {'.','#'},
                {'o','#'},
                {'o','#'},
                {'#','#'}
        };

        for (char[] b : board){
            System.out.println(Arrays.toString(b));
        }
        System.out.println(solution(board));
        System.out.println();

        board = new char[][]{
                {'#','#','#'},
                {'.','o','.'},
                {'#','.','#'},
                {'.','o','.'},
                {'#','#','#'}
        };

        for (char[] b : board){
            System.out.println(Arrays.toString(b));
        }
        System.out.println(solution(board));
        System.out.println();

        board = new char[][]{
                {'#','#','#'},
                {'.','o','.'},
                {'#','#','#'},
                {'.','o','.'},
                {'#','#','#'}
        };

        for (char[] b : board){
            System.out.println(Arrays.toString(b));
        }
        System.out.println(solution(board));
        System.out.println();





    }
}
