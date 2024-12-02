package org.example.linear.list.practice.linkedlist;

import java.util.HashMap;
import java.util.HashSet;

/**
 * 연결리스트 배열 사용
 *
 * 주어진 문자열 배열을 연결리스트 배열로 관리하는 코드를 작성
 *
 * 각 문자열의 첫 글자가 같은 것끼리 같은 연결 리스트로 관리하기
 *
 */

public class Practice_7 {

    public static void dataCollect(String[] data) {
        HashSet<Character> set = new HashSet<>(); // 데이터 걸러내기

        for (String item : data) {
            set.add(item.toCharArray()[0]);
        }

        System.out.println(set);
        Character[] arr = set.toArray(new Character[0]);
        LinkedList[] listArr = new LinkedList[set.size()];

        for (int i = 0; i < listArr.length; i++) {
            listArr[i] = new LinkedList(null,arr[i]);
        }

        for (String item : data) {
            for (LinkedList list : listArr) {
                if (list.alphabet == item.toCharArray()[0]) {
                    list.addData(item); // 첫글자로 문자열 연결
                }
            }
        }

        for (LinkedList item : listArr) {
            System.out.print(item.alphabet+ " : ");
            item.showData();
        }

    }
    public static void main(String[] args) {
        LinkedList list = new LinkedList();
        String[] fruits = { "apple", "banana", "watermelon", "apricot", "kiwi","blueberry"};
        dataCollect(fruits);
        String[] animals = {"ant","kangaroo","dog","cat","alligator","duck","crab"};
        dataCollect(animals);


    }
    
    // 기존 수정
    static class Node {
        String data;
        Node next;

        Node(){}
        Node(String data,Node next){
            this.data = data;
            this.next = next;
        }
    }

    static class LinkedList{
        Node head;
        char alphabet;

        LinkedList(){}
        LinkedList(Node node, char alphabet) {
            this.head = node;
            this.alphabet = alphabet;
        }
        public boolean isEmpty () { return this.head == null; }

        public void addData(String data){
            if (this.head == null) {
                this.head = new Node(data, null);
            } else {
                Node cur = this.head;
                while (cur.next != null) {
                    cur = cur.next;
                }
                cur.next = new Node(data,null);
            }
        }

        public void removeData(String data) {
            if (this.isEmpty()) {
                System.out.println("null");
                return;
            }


            Node cur = this.head;
            Node pre = cur.next;

            while (cur != null) {
                if(cur.data.equals(data)){
                    if (cur == this.head) {
                        this.head = cur.next;
                    } else {
                        pre.next = cur.next;
                    }
                    break;
                }
                pre = cur;
                cur = cur.next;
            }
        }

        public boolean findData(int data) {
            Node cur = this.head;
            while (cur != null) {
                if (cur.data.equals(data)) {
                    return true;
                }
                cur = cur.next;
            }
            return false;
        }

        public void showData() {
            if (this.head == null) {
                System.out.println(" is null ");
                return;
            }

            Node cur = this.head;
            while (cur != null) {
                System.out.print(cur.data + " ");
                cur = cur.next;
            }
            System.out.println();
        }

    }

}
