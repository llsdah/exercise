package org.example.nonlinear.practice;

/**
 * 중요 색칠하는 문제
 * row x col 행렬 board 에서
 *
 * 같은 행렬에서 word 문자여링 인접하게 ㅇ녀결되어 있는지를 확인하는 프로그램 작성
 *
 * DFS 방식으로 구현
 *
 */

public class Practice_1 {

    public static void main(String[] args) {
        char[][] board = {
                {'A','B','C','F'},
                {'S','F','C','S'},
                {'A','D','E','E'}
        };
        String word = "ABCCED";
        System.out.println("result : "+ solution(board,word));
        word = "SEE";
        System.out.println("result : "+ solution(board,word));
        word = "ABCD";
        System.out.println("result : "+ solution(board,word));

    }

    final static int[][] dirs= {{1,0},{-1,0},{0,1},{0,-1}};

    private static boolean solution(char[][] board, String word) {

        if (board == null || board.length == 0 || board[0].length == 0 || word == null || word.length() == 0) {
            return false;
        }

        int row = board.length;
        int col = board[0].length;

        boolean[][] visited = new boolean[row][col];

        for (int i = 0; i < row; i++) {
            for (int j = 0; j < col; j++) {
                if (dfs(board,visited, i, j, 0, word)) {
                    return true;
                }
            }
        }



        return false;
    }

    private static boolean dfs(char[][] board, boolean[][] visited, int x, int y, int i, String word) {
        int row = board.length;
        int col = board[0].length;

        if (i == word.length()) {
            return true;
        }

        // 범위 밖이면
        if (x < 0 || x >= row || y < 0 || y >= col) {
            return false;
        }

        // 이미 방문한 경우
        if (visited[x][y]) {
            return false;
        }

        // 다르면
        if (board[x][y] != word.charAt(i)) {
            return false;
        }


        visited[x][y] = true;
        for (int[] dir : dirs) {
            int xNext = x + dir[0];
            int yNext = y + dir[1];

            if (dfs(board,visited,xNext,yNext,i+1,word)) {
                return true;
            }
        }
        visited[x][y] = false;
        return false;
    }
}





