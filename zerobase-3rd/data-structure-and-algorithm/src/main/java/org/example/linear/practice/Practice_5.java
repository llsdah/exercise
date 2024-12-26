package org.example.linear.practice;

import java.awt.image.AreaAveragingScaleFilter;
import java.util.*;

/**
 * Dummy
 * 뱀이 나와서 기어다니는데 사과를 먹으면 뱀 길이가 늘어 난다.
 * NxN 정사각명, 뱀길이 1, 오른쪽 향한다
 * 벽을 만난면 게임이 종료 된다.
 * 자기 자신의 몸이랑 부딪혀도 종료된다
 */

public class Practice_5 {

    public static void main(String[] args) {
        int N = 6;
        int K = 3; // 사과수
        int L = 3; // 뱀의 방향 변환 횟수
        int[][] applesArr = { // 사과 좌표
                {3, 4},
                {2, 5},
                {5, 3}
        };
        List<List<Integer>> apples = new ArrayList<>();
        for (int[] item : applesArr) {
            apples.add( Arrays.stream(item).boxed().toList());
        }

        Object[][] movesArr = { // 뱀방향 변환 정호, {X,C} X(초) 시간이 지난 후 L(left) or D(right) 방향이동
                {3,'D'},
                {15,'L'},
                {17,'D'},
        };
        Queue<ArrayList> moves = new LinkedList<>();
        moves.add(new ArrayList(Arrays.asList(3,'D')));
        moves.add(new ArrayList(Arrays.asList(15,'L')));
        moves.add(new ArrayList(Arrays.asList(7,'D')));

        System.out.println(solution(N,K,L,apples,moves));

        N = 10;
        K = 4;
        L = 4;

        apples.clear();
        apples.add(new ArrayList<>(Arrays.asList(1,2)));
        apples.add(new ArrayList<>(Arrays.asList(1,3)));
        apples.add(new ArrayList<>(Arrays.asList(1,4)));
        apples.add(new ArrayList<>(Arrays.asList(1,5)));

        moves.clear();
        moves.add(new ArrayList(Arrays.asList(8,'D')));
        moves.add(new ArrayList(Arrays.asList(10,'D')));
        moves.add(new ArrayList(Arrays.asList(11,'D')));
        moves.add(new ArrayList(Arrays.asList(13,'L')));

        System.out.println(solution(N,K,L,apples,moves));

    }

    private static Integer solution(int n, int k, int l, List<List<Integer>> apples, Queue<ArrayList> moves) {
        int[][] board = new int[n+1][n+1];
        for (List item : apples) {
            board[(int) item.get(0)][(int)item.get(1)] = 1;
        }

        Queue<ArrayList> snake = new LinkedList<>();
        snake.add(new ArrayList(Arrays.asList(1,1)));

        ArrayList<ArrayList> direction = new ArrayList<>();
        direction.add(new ArrayList(Arrays.asList(0,1))); // 오른쪽 이동
        direction.add(new ArrayList(Arrays.asList(1,0))); // 왼쪽이동
        direction.add(new ArrayList(Arrays.asList(0,-1)));
        direction.add(new ArrayList(Arrays.asList(-1,0)));

        int dIdx = 0;
        int score = 0; // 1초
        int x = 1;
        int y = 1;

        while (true) {
            score += 1;
            x += (int)direction.get(dIdx).get(0);
            y += (int)direction.get(dIdx).get(1);

            if (1 <= x && x <= n && 1 <= y && y <= n) {
                ArrayList cur = new ArrayList(Arrays.asList(x,y));
                if (snake.contains(cur)) { // 몸과 부딪힌 케이스
                    return score;
                }
                snake.add(cur);

                if (board[x][y] == 0) { // 사과가 없으니 뺸다
                    snake.poll();
                } else if (board[x][y] == 1) { // 사과가 있으니 사과 경로를 1개 없엔다.
                    board[x][y] = 0;
                }
            } else { // 부딪힌다 종료
                return score;
            }

            if (moves.size() > 0 && score == (int) moves.peek().get(0)) {
                if ((char)moves.peek().get(1) == 'D') {
                    dIdx = (dIdx + 1) % 4;
                    moves.poll();
                } else if ((char)moves.poll().get(1) == 'L') {
                    dIdx = (dIdx - 1) % 4;
                    moves.poll();
                }
            }

        }


    }

    private static boolean solution1(int n, int k, int l, int[][] apples, int[][] moves) {

        return false;
    }
}
