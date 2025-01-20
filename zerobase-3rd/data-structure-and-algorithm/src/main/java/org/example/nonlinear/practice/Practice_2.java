package org.example.nonlinear.practice;

/**
 *
 * row x col OX 표시
 *
 * X로 둘어쌓여있는 O는 X로 변경하고
 * 상하좌우 0로 연결되어 있는 0는 그래도 0을 ㅇ지한 후 출력
 *
 * 입력
 * XXXX
 * XOOX
 * XXOX
 * XOXX
 *
 * 출력
 * XXXX
 * XXXX
 * XXXX
 * XOXX
 *
 */
public class Practice_2 {

    public static void main(String[] args) {

        char[][] board = {
                {'x','x','x','x'}
                ,{'x','o','o','x'}
                ,{'x','x','o','x'}
                ,{'x','o','x','x'}
        };
        System.out.println("solution 1 ===================");

        for (int i = 0; i < board.length; i++) {
            for (int j = 0; j < board[i].length; j++) {
                System.out.print(board[i][j] + " ");
            }
            System.out.println();
        }

        solution(board);
        System.out.println();

        for (int i = 0; i < board.length; i++) {
            for (int j = 0; j < board[i].length; j++) {
                System.out.print(board[i][j] + " ");
            }
            System.out.println();
        }

        System.out.println();
        System.out.println("solution 2====================");

        board = new char[][]{
                {'x','x','x','x'}
                ,{'x','o','o','x'}
                ,{'x','x','o','x'}
                ,{'x','o','o','x'}
        };
        for (int i = 0; i < board.length; i++) {
            for (int j = 0; j < board[i].length; j++) {
                System.out.print(board[i][j] + " ");
            }
            System.out.println();
        }

        System.out.println();
        solution(board);


        for (int i = 0; i < board.length; i++) {
            for (int j = 0; j < board[i].length; j++) {
                System.out.print(board[i][j] + " ");
            }
            System.out.println();
        }

    }

    private static void solution(char[][] board) {
        // 불가능한 구조 입니다.
        if (board == null || board.length < 3 || board[0].length < 3) {
            return;
        }

        int row = board.length;
        int col = board[0].length;

        for (int i = 0; i < row; i++) {
            // 좌측 끝
            if (board[i][0] == 'o') {
                dfs(board, i, 0);
            }

            // 우측 끝
            if (board[i][col - 1] == 'o') {
                dfs(board, i, col - 1);
            }
        }

        for (int i = 0; i < col - 1; i ++ ) {
            // cjtEo god
            if (board[0][i] == 'o') {
                dfs(board, 0, i);
            }

            if (board[row - 1][i] == 'o') {
                dfs(board, row - 1, i);
            }

        }

        for (int i = 0; i< row; i ++ ) {
            for (int j = 0; j < col; j++) {
                if (board[i][j] == 'o') {
                    board[i][j] = 'x';
                }

                if (board[i][j] == '*') {
                    board[i][j] = 'o';
                }

            }
        }

    }

    private static void dfs(char[][] board, int x, int y) {
        if (x < 0 || y < 0 || x > board.length -1 || y > board[0].length) {
            return;
        }

        if (board[x][y] != 'o') {
            return;
        }

        board[x][y] = '*';
        dfs(board,x + 1, y);// 오른쪽
        dfs(board,x - 1, y);// 왼쪽
        dfs(board,x, y + 1); // 아래
        dfs(board,x , y - 1); // 위쪽


    }


}
