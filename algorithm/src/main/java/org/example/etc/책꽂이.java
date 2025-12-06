package org.example.etc;


import java.io.*;
import java.util.*;

/**
중요 !!!

N : 책의 총 개수
K : 선반의 층 수 (1층부터 K층까지, 보통 1층이 제일 아래)
W : 각 층마다 최대로 꽂을 수 있는 책의 개수 (가로 길이 제한, 책 한 권 두께 = 1)
배열 h[0 … N−1] : 책의 높이들이 왼쪽 → 오른쪽 순서로 고정되어 주어짐 (이 순서를 절대 바꿀 수 없음)

제약 조건 (모두 동시에 만족해야 함)
1. 순서 유지 - 책은 주어진 순서대로만 꽂을 수 있다. → 각 층에 들어가는 책들은 원래 배열에서 연속된 구간이어야 한다.
2. 층당 가로 길이 제한 - 한 층에 꽂히는 책의 개수는 최대 W권을 넘을 수 없다.
3. 층 높이의 단조성 (안정성 제약)아래층일수록 높이 합이 더 크거나 같아야 한다. 즉, 1층(제일 아래) ≥ 2층 ≥ 3층 ≥ … ≥ K층 (위로 갈수록 높이가 작거나 같아야 안정적이라는 물리적 의미)
4. 책을 한번에 빼낼 수 있어야 함 : 책의 양 옆쪽 혹은 윗쪽에 1 인 빈공간이 있어야 뺄수있다. 각 층의 양 끝은 항상 빈 공간이 없음, 만약 [5,5,4,5] 인 책높이 배열이 입력되고 가로 길이가 4, 해당 칸의 높이가 5인경우 첫번쨰책은 양옆쪽 공간이 없어 한번에 뺴내지 못한다. 다만 두번째 네번쨰 책은 세번째 책의 높이가 4이므로 1 공간이 남아서 한번에 뺴내기가 가능하다. 결론적으로 해당칸의 높이는 5가 되지 못하고 최소 높이는 6이 된다.

목표
위 모든 제약을 만족하면서, K개 각 층의 높이 합을 최소화하라.

 */

public class 책꽂이 {
    static int N, K, W;
    static int[] h;
    static int[][] floorHeight; // floorHeight[i][j]: i~j의 유효 높이
    static final int INF = 1_000_000_000;

    public static void main(String[] args) throws Exception {
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        PrintWriter out = new PrintWriter(System.out);

        StringTokenizer st = new StringTokenizer(br.readLine());
        N = Integer.parseInt(st.nextToken());
        K = Integer.parseInt(st.nextToken());
        W = Integer.parseInt(st.nextToken());

        h = new int[N];
        st = new StringTokenizer(br.readLine());
        for (int i = 0; i < N; i++) h[i] = Integer.parseInt(st.nextToken());

        // 1️⃣ 각 구간의 유효 높이 미리 계산
        floorHeight = new int[N][N];
        for (int i = 0; i < N; i++) {
            int max = 0;
            for (int j = i; j < Math.min(N, i + W); j++) {
                max = Math.max(max, calcHeight(i, j));
                floorHeight[i][j] = max;
            }
        }

        // 2️⃣ DP 수행
        int[][] dp = new int[K + 1][N + 1];
        for (int[] row : dp) Arrays.fill(row, INF);
        dp[0][0] = 0;

        for (int k = 1; k <= K; k++) {
            for (int i = 1; i <= N; i++) {
                for (int j = Math.max(0, i - W); j < i; j++) {
                    if (dp[k - 1][j] == INF) continue;
                    int curH = floorHeight[j][i - 1];
                    if (k == 1 || curH <= floorHeightPrev(j, k - 1)) {
                        dp[k][i] = Math.min(dp[k][i], dp[k - 1][j] + curH);
                    }
                }
            }
        }

        out.println(dp[K][N]);
        out.flush();
    }

    // 주어진 구간 [l..r]의 유효 높이 계산
    static int calcHeight(int l, int r) {
        int res = 0;
        for (int i = l; i <= r; i++) {
            int val = h[i];
            if (i == l || i == r) {
                val = h[i] + 1;   // 양끝은 무조건 +1
            } else if (h[i] >= h[i - 1] && h[i] >= h[i + 1]) {
                val = h[i] + 1;   // 양옆을 모두 막고 있을 때만 +1
            } else {
                val = h[i];       // 한쪽이라도 낮으면 그대로 가능
            }
            res = Math.max(res, val);
        }
        return res;
    }

    // (선택적으로) 이전 층의 높이 기록 관리
    static int floorHeightPrev(int idx, int layer) {
        return floorHeight[0][idx - 1 < 0 ? 0 : idx - 1];
    }

}
