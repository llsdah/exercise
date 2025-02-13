package org.example.algorithm.backtracking;

import org.example.nonlinear.heap.MyMaxHeap_1;

public class BackTracking {
    /**
     * 백트래킹
     * - 모든 경우의 수를 탐색하며, 최적해를 구하는 과정에서 유망하지 않은 쪽은 더이상 구하지 않는 방법
     * - 유망 Promising : 해가 될 가능성이 있는 경우
     * - 가지치기 Pruning : 해가 될 가능성이 없는 경우 노드 제외
     * - 백트래킹 BackTracking : 유망하지 않은 쪽으로 가지 않고 되돌아 오는 방법
     * - 재귀함수, DFS 등의 구현이 필요함
     * 
     * 
     * 에시 
     * - N-Queen 
     * - NxN 체스판에서 퀸 N개를 서로 공격할 수 없도록 배치하는 경우의 수 
     * 
     */

    static int n = 4;
    static int[] board = new int[n]; // queen은 둔 index 값 입력
    static int cnt;
    private static int solution(int row) {

        if (row == n) {
            cnt ++;
            System.out.print("퀸을 놓는 장소 ");
            for (int i = 0; i < n; i++) {
                System.out.print(board[i]+ " ");
            }
            System.out.println();
            return cnt;
        }
        for (int i = 0; i < n; i++) {
            board[row] = i; // queen index

            // promising
            if (isPromising(row)) {
                solution(row + 1);
            }
        }
        
        return cnt;
    }

    public static boolean isPromising(int row) {
        for (int i = 0; i < row; i++) {
            // 위치 || 대각선
            if (board[row] == board[i] || row - i == Math.abs(board[row]- board[i])) {
                return false;
            }
        }
        return true;
    }


    // n queen 문제 풀기 
    public static void main(String[] args) {
        int row = 0;
        System.out.println("경우의 수 : "+solution(row)); //2
    }

}
