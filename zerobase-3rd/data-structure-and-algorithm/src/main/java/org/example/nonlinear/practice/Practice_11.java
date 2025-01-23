package org.example.nonlinear.practice;

/**
 * 적녹 구분 관련
 * NxN 의 사진에 [R 빨 G 녹 B 파 ] 중 하나를 색칠한 그림이 있다
 * 이때 같은 색상이 상하 좌우 연결된 경우 같은 구역으로 본다.
 * 적록 생약인 경우 적녹 색상은 인접한 경우로 본다.
 * 아래와 같은경우가 있는 겨웅 일반은은 4구역, 적녹색약은 3구역으로 본다
 * 일반인과 적녹색약의 구역 수 를 출력하라
 {
    "RRRBB",
    "GGBBB",
    "BBBRR",
    "BBRRR",
    "RRRRR"
 }

 *
 */
public class Practice_11 {

    // 2차원 배열에 대한 정보를 작성
    final static int[][] dirs = {{1,0},{-1,0},{0,1},{0,-1}};

    static int[][] notRGview;
    static int[][] RGview;

    private static void solution(char[][] pictures) {
        if (pictures == null || pictures.length == 0 || pictures[0].length == 0) {
            return;
        }

        int n = pictures.length;
        notRGview = new int[n][n];
        RGview = new int[n][n];

        int notRGcnt = 0;
        int RGcnt = 0;

        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                // not

                if (notRGview[i][j] == 0) {
                    notRGcnt++;
                    //DFS
                    notRGDFS(pictures, j, i, pictures[i][j], notRGcnt);
                }

                if (RGview[i][j] == 0) {
                    RGcnt++;
                    //DFS
                    RGDFS(pictures, j, i, pictures[i][j], RGcnt);
                }

            }
        }

        System.out.println("notRGcnt = " + notRGcnt);
        System.out.println("RGcnt = " + RGcnt);

    }

    public static void notRGDFS(char[][] picture, int x, int y, char color, int cnt) {
        notRGview[y][x] = cnt;

        for (int i = 0; i < dirs.length; i++) {
            int xNext = x + dirs[i][0];
            int yNext = y + dirs[i][1];

            if (xNext < 0 || xNext >= notRGview.length || yNext < 0 || yNext >=notRGview.length) {
                continue;
            }

            // 마킹이 된경우
            if (notRGview[yNext][xNext] != 0) {
                continue;
            }


            char nextColor = picture[yNext][xNext];
            // 같은색 한번더 이동
            if (nextColor == color) {
                notRGDFS(picture, xNext, yNext, nextColor, cnt);
            }

        }

    }

    public static void RGDFS(char[][] picture, int x, int y, char color, int cnt) {
        RGview[y][x] = cnt;

        for (int i = 0; i < dirs.length; i++) {
            int xNext = x + dirs[i][0];
            int yNext = y + dirs[i][1];

            if (xNext < 0 || xNext >= RGview.length || yNext < 0 || yNext >= RGview.length) {
                continue;
            }

            if (RGview[yNext][xNext] != 0) {
                continue;
            }

            char nextColor = picture[yNext][xNext];
            if (nextColor == color || (color == 'R' && nextColor == 'G') || (color == 'G' && nextColor == 'R')) {
                RGDFS(picture, xNext, yNext, nextColor, cnt);
            }

        }

    }




    public static void main(String[] args) {

        String[] strs =
                {
                        "RRRBB",
                        "GGBBB",
                        "BBBRR",
                        "BBRRR",
                        "RRRRR"
                };

        char[][] pictures = new char[strs.length][strs[0].length()];
        for (int i = 0; i < strs.length; i++) {
            for (int j = 0; j < strs[0].length(); j++) {
                pictures[i][j] = strs[i].charAt(j);
                System.out.print(strs[i].charAt(j)+ " ");
            }
            System.out.println();
        }

        solution(pictures);

    }


}
