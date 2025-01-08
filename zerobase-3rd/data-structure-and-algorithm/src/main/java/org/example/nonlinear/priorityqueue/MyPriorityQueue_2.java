package org.example.nonlinear.priorityqueue;

import java.util.PriorityQueue;

public class MyPriorityQueue_2 {

    public static void main(String[] args) {
        String[] name = {"a","b","c","d","e"};
        int[] age = {30,20,45,62,35};

        //문제 1
        //solution(name,age);
        
        // 문제 2
        PriorityQueue<Person> pq = new PriorityQueue<>(
                (Person p1, Person p2) -> p1.age >= p2.age ? 1 : -1
        );

        for (int i = 0; i < name.length; i++) {
            pq.offer(new Person(name[i],age[i]));
        }

        while (!pq.isEmpty()) {
            Person p = pq.poll();
            System.out.println(p.naame + " " + p.age);
        }

        
    }

    // 나이를 오름차순 혹은 내림차순으로 정렬
    private static void solution(String[] name, int[] age) {

        PriorityQueue<Person> pq = new PriorityQueue<>();

        for (int i = 0; i < name.length; i++) {
            pq.offer(new Person(name[i],age[i]));
        }

        while (!pq.isEmpty()) {
            Person p = pq.poll();
            System.out.println(p.naame + " " + p.age);
        }
    }

    static class Person{
        String naame;
        int age;

        public Person(String naame, int age) {
            this.naame = naame;
            this.age = age;
        }

        
        /**
         문제 1번 부분
         
         @Override
        public int compareTo(Person o) {
            // 1 변경 안함, -1 변경
            // 새롭게 추가하는 데이터가 더 적을 때 변경 (적은 값이 위로 올라감, 오름차순)
            // o가 새롭게 추가하는 데이터
//            return this.age >= o.age ? 1 : -1;

            // 새롭게 추가하는 데이터가 더 클때 변경 (큰값이 위로 올라감, 내림차순)
            return this.age >= o.age ? -1 : 1;

        }
        */
    }
}
