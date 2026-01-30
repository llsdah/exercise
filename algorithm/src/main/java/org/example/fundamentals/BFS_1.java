package org.example.fundamentals;

import java.util.*;

/**
 * PCCP Level 2 - BFS_1 기본 문제
 * "미로 최단 거리 찾기"
 *
 * 요구사항:
 *  - 0은 이동 불가, 1은 이동 가능
 *  - (0,0)에서 (N-1, M-1)까지의 최소 이동 횟수 출력
 *  - BFS를 활용하여 최단 거리 탐색
 *
 * 핵심 포인트:
 *  - BFS는 모든 노드를 "거리 순"으로 확장하므로 최단 거리 보장
 *  - 방문 체크 배열 필수 (중복 방문 방지)
 *  - 큐에는 (행, 열, 현재까지 거리)를 저장
 *  - 지도 크기 최대를 고려하여 O(N*M)로 동작
 *
 *  핵심 아이디어 요약
 *   - BFS는 최단 경로 보장
 *   - 각 상태는 (행, 열, 현재 거리 )
 *   - 방문배열로 중복 확장 차단 -> 시간 절약
 *   - O(N*M) 매우 빠름
 *
 */

public class BFS {
    static final int[] dr = {-1,1,0,0}; // row 방향 이동
    static final int[] dc = {0,0,-1,1}; // col 방향 이동

    /**
     * N 행, M 열
     * BFS를 이용하여 (0,0) → (N-1, M-1) 최소 거리 반환
     * 도달 불가 시 -1 반환
     */
    static int bfs(int[][] map, int N, int M) {
        int answer = 0;
        boolean[][] visit = new boolean[N][M]; // 방문 여부 체크 배열
        ArrayDeque<int[]> q = new ArrayDeque<>(); // r, c 거리 지정

        // 시작점이 벽(0) 이면 바로 종료
        if(map[0][0] == 0) return -1;

        // 시작점 큐에 삽입, 거리 = 1;
        q.offer(new int[]{0,0,1});
        visit[0][0] = true;
        while(!q.isEmpty()) {
            int[] cur = q.poll();
            int r = cur[0];
            int c = cur[1];
            int dist = cur[2];

            // 목표 도착하면 거리 반환
            if (r == N-1 && c == M-1) return dist;

            // 4방향 탐색
            for (int d = 0; d< 4; d++) {
                int nr = r + dr[d];
                int nc = c + dc[d];

                // 유효범위인지 확인
                if (nr < 0 || nc < 0 || nr >= N || nc >= M) continue;

                // 벽이거나 이미 방문 했는지 확인
                if(map[nr][nc] == 0 || visit[nr][nc]) continue;

                // 방문 처리 후 큐에 추가
                q.offer(new int[]{nr,nc,dist +1});
            }
        }

        return -1;
    }


}


















