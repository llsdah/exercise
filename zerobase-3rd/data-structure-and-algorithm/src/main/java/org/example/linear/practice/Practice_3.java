package org.example.linear.practice;

/**
 *
 * 1~N 풍선이 원형으로 있다 즉, 1번풍선 왼쪽은 N번 오른쪽이 2번이다
 * 풍선에는 종이가 있다 N 번 이하 -N 이상의 숫자 적혀 있다.
 * 숫자대로 음수가 있으면 왼쪽, 양수는 오른쪽
 *
 */

public class Practice_3 {
    public static void main(String[] args) {
        int[] ballon = {3,2,1,-3,-1};
        solution(ballon);
        ballon = new int[]{3,2,-1,-2};
        solution(ballon);
    }

    // 리스트로 풀어 보기
    private static void solution(int[] ballon) {
        LinkedList linkedList = new LinkedList();
        for (int i = 0; i < ballon.length; i++) {
            linkedList.add(i+1,ballon[i]);
        }

        Node cur = linkedList.head;

        int visitCnt = 0;
        int cmd = 0;
        while (visitCnt != ballon.length) {
            int cnt = 0;
            while (cnt != Math.abs(cmd)) { // 이동횟수 만큼 세면 이동이 된다.
                if (cmd > 0) {
                    cur = cur.next;
                } else {
                    cur = cur.pre;
                }
                if (cur.visited == false) {
                    cnt ++;
                }
            }
            System.out.print(cur.data + " ");
            cur.visited = true;
            visitCnt ++;
            cmd = cur.cmd;
        }
        System.out.println();

    }

    static class Node{
        int data; // 풍선번호
        int cmd; // 풍선종이 내용

        boolean visited; // 터진지 여부
        Node next;
        Node pre;

        public Node(int data, int cmd, boolean visited, Node next, Node pre) {
            this.data = data;
            this.cmd = cmd;
            this.visited = visited;
            this.next = next;
            this.pre = pre;
        }
    }

    static class LinkedList {
        Node head;

        public void add(int data, int cmd) {
            if (this.head == null) {
                this.head = new Node(data, cmd, false, null, null);
                this.head.next = this.head;
                this.head.pre = this.head;
            } else {
                Node cur = this.head;
                while (cur.next != this.head) {
                    cur = cur.next;
                }
                cur.next = new Node(data, cmd, false, cur.next, cur);
                this.head.pre = cur.next;
            }
        }
    }

}
