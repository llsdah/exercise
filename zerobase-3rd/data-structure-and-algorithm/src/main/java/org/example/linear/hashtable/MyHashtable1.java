package org.example.linear.hashtable;

import java.util.*;

public class MyHashtable1 {
    /**
     * 해시테이블
     * -
     * 키 : 테이블 접근 입력값
     * 해시 함수 : 키를 해시값으로 매핑하는 연산
     * 해시 값 : 해시 테이블의 인덱스
     * 해시 테이블 키-갑승ㄹ 연관시켜 저장하는 데이터 구조
     * 해시 값 : 해시테이블의 인덱스
     * 해시테이블 : 키-값을 연관시켜 저장하는 데이터 구조
     *
     *
     * 해시 충돌
     * - 해시테이블의 같은 공간에 서로 다른 값을 저장하려는 경우 발생
     * => 개방주소법 분리 연결법
     *
     *
     * 개방주소법
     * - 충돌시 테이블에서 비어있는 공간의 hash를 찾아
     * -- 선형 탐사법
     * --- 빈공간을 순차 탐사, 일차군집화 발생 (데이터가 집중화 되는 현상)
     * -- 제곱탐사법
     * --- 빈공간을 n제곱만금 간격 두고 탐사, 이차군집화 발생
     * -- 이중해실
     * --- 해시함수를 이중으로 사용 최초해시를 구할때 사용 + 충돌발생시 탐사 이동 간격을 구할때 사용
     *
     *
     */


    public static void main(String[] args) {
        Hashtable<String,Integer> ht = new Hashtable<>();
        ht.put("key1",10);
        ht.put("key2",20);
        ht.put("key3",30);

        for ( Map.Entry<String,Integer> item : ht.entrySet()) {
            System.out.print("item.getKey() = " + item.getKey());
            System.out.println(", item.getValue() = " + item.getValue());
        }

        Hashtable<Integer,Integer> ht2 = new Hashtable<>();

        ht2.put(getHash(1),10);
        ht2.put(getHash(2),20);
        ht2.put(getHash(3),30);
        ht2.put(getHash(4),40);
        ht2.put(getHash(5),50);
        ht2.put(getHash(6),60);

        for ( Map.Entry<Integer,Integer> item : ht2.entrySet()) {
            System.out.print("item.getKey() = " + item.getKey());
            System.out.println(", item.getValue() = " + item.getValue());
        }
        System.out.println(" 후 ");
        ht2.put(getHash(6),70);
        for ( Map.Entry<Integer,Integer> item : ht2.entrySet()) {
            System.out.print("item.getKey() = " + item.getKey());
            System.out.println(", item.getValue() = " + item.getValue());
        }

    }

    public static int getHash(int key){
        return key % 5;
    }



}
