package org.example.algorithm.divideandconquer.practice;

import java.sql.ClientInfoStatus;

/**
 * 2차원 정수형 배열 lists가 주어졌다
 * lists[i] 링크드 리스트의 원소 정보가 들어 있다 오름차순 정렬
 * 모든 링크드 리스트를 하나의 저열ㄹ된 링크드 리스트로 합병하세요
 *
 * input : {{2,3,9},{1,5,7},{3,6,7,11}}
 * output : 1 ~ 9, 11
 *
 */
public class Practice_2 {

    private static Node solution(Node[] node) {

        return diviedList(node, 0, node.length -1);
    }

    public static Node diviedList(Node[] lists, int left, int right) {
        if (left == right) {
            return lists[left];
        }

        int mid = left + (right - left) / 2;
        Node l1 = diviedList(lists, left, mid);
        Node l2 = diviedList(lists, mid + 1, right);

        return mergeList(l1,l2);
    }

    private static Node mergeList(Node l1, Node l2) {
        if (l1 == null) {
            return l2;
        } else if (l2 == null) {
            return l1;
        }

        Node merge = new Node(0);
        Node cur = merge;

        while (l1 != null && l2 != null) {
            if (l1.val < l2.val) {
                cur.next = l1;
                l1 = l1.next;
            } else {
                cur.next = l2;
                l2 = l2.next;
            }
            cur = cur.next;
        }

        if (l1 != null) {
            cur.next = l1;
        }

        if (l2 != null) {
            cur.next = l2;
        }

        return merge.next;
    }

    private static void setUpLinkedList(Node[] node, int[][] arr) {
        for (int i = 0; i < arr.length; i++) {
            node[i] = new Node(arr[i][0]);
        }

        for (int i = 0; i < arr.length; i++) {
            Node cur = node[i];
            for (int j = 1; j < arr[i].length; j++) {
                cur.next = new Node(arr[i][j]);
                cur = cur.next;
            }
        }
    }

    public static void main(String[] args) {
        int[][] arr = {{2,3,9},{1,5,7},{3,6,7,11}};

        Node[] node = new Node[arr.length];
        setUpLinkedList(node, arr);
        Node combinedNode = solution(node);

        while (combinedNode != null) {
            System.out.print(combinedNode.val+ " ");
            combinedNode = combinedNode.next;
        }

        System.out.println();


    }

    static class Node {
        int val;
        Node next;
        Node(int val) {
            this.val = val;
            this.next = null;
        }
    }


}









