package org.example.mathematics.practice;
import java.util.*;

/**
 * 영토 구하기
 * 지도가 주어 질때 땅의 두레를 구하는 프로그램
 */
public class Practice_5 {

    private static int solution1(int[][] grid) {
        int[][] directions = { // 방향 탐색을 위한 것 (x,y)
                {0,1},
                {1,0},
                {0,-1},
                {-1,0}
        };

        int cnt = 0;
        int row = grid.length;
        int col = grid[0].length;

        for (int i = 0; i < row; i++) {
            for (int j = 0; j < col; j++) {
                if (grid[i][j] == 1) { // 땅인경우
                    for (int[] d : directions) { // 전체 방향 탐색
                        int x = i + d[0];
                        int y = j + d[1];
                        // 밖으로 넘어가거나 물인경우
                        if (x < 0 || y < 0 || x >= row || y >= col || grid[x][y] == 0){
                            cnt ++;
                        }
                    }
                }
            }
        }

        return cnt;
    }

    // 재귀로 푼다 땅인 경우에 재귀호출
    private static int solution2(int[][] grid) {
        int[][] directions = { // 방향 탐색을 위한 것 (x,y)
                {0,1},
                {1,0},
                {0,-1},
                {-1,0}
        };

        int cnt = 0;
        int row = grid.length;
        int col = grid[0].length;

        for (int i = 0; i < row; i++) {
            for (int j = 0; j < col; j++) {
                if (grid[i][j]==1){
                    return recursive(grid, directions, i, j);
                }
            }
        }

        return cnt;
    }

    // 각 면을 조사합니다.
    private static int recursive(int[][] grid, int[][] directions, int i, int j) {
        int cnt = 0;
        int row = grid.length;
        int col = grid[0].length;

        grid[i][j] = -1; // 현재 타고 들어온 것이기에 -1
        for (int[] d : directions) {
            int x = i + d[0];
            int y = j + d[1];

            if (x < 0 || y < 0 || x >= row || y >= col || grid[x][y]==0) {
                cnt ++;
            } else {
                if (grid[x][y]==1){
                    cnt += recursive(grid,directions,x,y);
                }
            }

        }

        return cnt;
    }

    public static void main(String[] args) {
        int[][] grid = {{1}};
        System.out.println(solution1(grid));
        System.out.println(solution2(grid));
        System.out.println();

        grid = new int[][] {
                {0,1,0,0},
                {1,1,1,0},
                {0,1,0,0},
                {1,1,0,0}
        };

        System.out.println(solution1(grid));
        System.out.println(solution2(grid));
        System.out.println();

    }

}
