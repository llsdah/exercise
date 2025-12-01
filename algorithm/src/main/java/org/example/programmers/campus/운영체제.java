package org.example.programmers.campus;

import java.util.*;

public class 운영체제 {

    public long[] solution_my(int[][] program) {
        long[] answer = new long[11];

        int n = program.length;
        List<int[]> list = new ArrayList<>();
        for (int i = 0; i < n; i++) {
            list.add(program[i]);
        }

        list.sort((o1, o2) -> {
            if (o1[1] == o2[1]) return o1[0] - o2[0];
            return o1[1] - o2[1];
        });

        Queue<int[]> que = new PriorityQueue<>((o1, o2) -> {
            if (o1[0] == o2[0]) return o1[1] - o2[1];
            return o1[0] - o2[0];
        });

        int time = 0;
        int idx = 0;

        while (!que.isEmpty() || idx < list.size()) {
            while (que.isEmpty() && time < list.get(idx)[1]) {
                time = list.get(idx)[1];
            }
            while (idx < list.size() && time >= list.get(idx)[1]) {
                que.offer(list.get(idx++));
            }

            int[] p = que.poll();
            answer[p[0]] += (time - p[1]);
            time += p[2];
        }

        answer[0] = time;

        return answer;
    }

    // fail 1
    public long[] solution_fail(int[][] program) {
        long[] answer = new long[11];
        int r = program.length;
        int c = program[0].length;
        int[][] programs = new int[r][c + 1];
        int minTime = 10000001;
        for (int i = 0; i < r; i++) {
            for (int k = 0; k < c; k++) {
                programs[i][k] = program[i][k];
            }
            minTime = Math.min(minTime, programs[i][1]);
            programs[i][c] = programs[i][1];
        }
        Arrays.sort(programs, (o1, o2) -> {
            if (o1[1] == o2[1]) return o1[0] - o2[0];
            return o1[1] - o2[1];
        });
        Queue<int[]> que = new PriorityQueue<>((o1, o2) -> {
            if (o1[0] == o2[0]) return o1[1] - o2[1];
            return o1[0] - o2[0];
        });
        for (int i = 0; i < r; i++) {
            if (programs[i][1] <= minTime) {
                programs[i][3] = -1;
                que.offer(programs[i]);
            }
        }
        System.out.println(que.size());
        int time = 0;
        while (!que.isEmpty()) {
            int[] pro = que.poll();
            answer[pro[0]] += (time - pro[1]); //걸린 시간
            time+=pro[2]; //시간
            pro[3] = -1; // 사용함
            for (int i = 0; i< r;i++) {
                if(programs[i][3] == -1) continue;
                if(programs[i][1] <= time) {
                    programs[i][3] = -1;
                    que.offer(programs[i]);
                }
            }
        }
        answer[0] = time;
        return answer;
    }

    public long[] solution(int[][] program) {
        long[] answer = new long[11];
        Arrays.sort(program, new Comparator<int[]>() {
            @Override
            public int compare(int[] o1, int[] o2) {
                return o1[1] - o2[1];
            }
        });
        Queue<int[]> prog = new LinkedList<>();
        for (int i = 0; i < program.length; i++) {
            prog.offer(program[i]);
        }

        // 실행 대기
        PriorityQueue<int[]> pq = new PriorityQueue<>(
                (a,b) -> (a[0] == b[0] ? a[1] -b[1] : a[0]-b[0])
        );
        int[] exec = {-1,-1,-1};
        long time = 0;

        while(!prog.isEmpty() || !pq.isEmpty()) {
            if (pq.isEmpty()) {
                time = prog.peek()[1];
                while (!prog.isEmpty() && prog.peek()[1] <= time) {
                    pq.offer(prog.poll());
                }
            }
            exec = pq.poll();
            answer[exec[0]] += time - exec[1];
            time += exec[2];
            // 시간 변경으로 한번 더 호출
            while (!prog.isEmpty() && prog.peek()[1] < time) {
                pq.offer(prog.poll());
            }
        }
        answer[0] = time;
        return answer ;
    }
}
