package org.example.nonlinear.priorityqueue;

import javax.xml.transform.Source;
import java.util.PriorityQueue;

public class MyPriorityQueue_3 {
    // 문자열 오름차순 내림차순

    public static void main(String[] args) {
        String[] name = {"a","e","c","b","d"};
        int[] age = {30,20,45,62,35};

        solution(name,age);
        
    }

    private static void solution(String[] name, int[] age) {
        PriorityQueue<Person> pq = new PriorityQueue<>(
                ( Person p1, Person p2 ) -> p1.naame.compareTo(p2.naame)
        );

        for (int i = 0; i < name.length; i++) {
            pq.offer(new Person(name[i],age[i]));
        }

        while (!pq.isEmpty()) {
            Person p = pq.poll();
            System.out.println(p.naame +" : "+p.age);
        }



    }

    static class Person {
        String naame;
        int age;

        public Person(String naame, int age) {
            this.naame = naame;
            this.age = age;
        }
    }
    
    
}
