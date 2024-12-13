package org.example.linear.hashtable.practice;

import java.util.HashMap;
import java.util.Hashtable;

/**
 * 해시테이블과 해시맵 차이
 */
public class Practice_3 {

    public static void main(String[] args) {
        Hashtable<Integer,Integer> ht = new Hashtable<>();
        ht.put(0,10);
        System.out.println(ht.get(0));
        
        HashMap<Integer,Integer> map = new HashMap<>();
        map.put(0,10);
        System.out.println("map.get(0) = " + map.get(0));


        ht.put(null,-999); // null키가 없음
        map.put(null,-999); // null 사용가능
        /**
         *
         * Thread-safe 측면
         * Hashtable : 0 (멀티스레드 환경에서 좋다)
         * HashMap : x (싱글쓰레드 환경 우수)
         * 참고) synchronizedMap, ConCurrentHashMap
         *
         */

        
    }
}
