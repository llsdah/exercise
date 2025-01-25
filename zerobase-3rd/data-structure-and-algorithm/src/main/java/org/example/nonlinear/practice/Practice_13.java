package org.example.nonlinear.practice;

import java.util.*;

/**
 * K 계산대, N명 고객이 물건고르고 계산하기위해 줄 서 있다. 
 * 먼저 온 순서로 계산 진행 
 * 
 * 안내원 
 * -> 비어 있는 계산대줄 번호가 작은 쪽 
 * -> 꽉차있다면, 먼저 끝난 쪽 안내 
 * 동시에 끝나면 번호가 작은 쪽 먼저 안내 
 * 
 * 동시에 계산마치면 번호가 높은 곳 부터 손님이 먼저 나감
 * 물건 갯수 만큼 분이 소비딤
 * 
 * 2차원 배열 고객번혼 + 물건 갯수 
 * 
 * 
 * 
 */
public class Practice_13 {

    public static void main(String[] args){
        int[][] customers = {
                {1,4},
                {2,5},
                {3,14},
                {4,1},
                {5,7},
                {6,5},
                {7,7},
                {8,5},
                {9,10},
                {10,3}
        };
        int count = 3;
        ArrayList<Integer> answer = solution(count, customers);
        System.out.println("answer = " + answer);
    }

    private static ArrayList<Integer> solution(int k, int[][] customers) {
        if (customers == null || customers.length == 0 || customers[0].length == 0) {
            return null;
        }
        ArrayList<Integer> list = new ArrayList<>();
        
        int n = customers.length;

        Queue<Customer> waitQue = new LinkedList<>();

        for (int i = 0; i < n; i++) {
            waitQue.offer(new Customer(customers[i][0], customers[i][1]));
        }

        PriorityQueue<Counter> pqCounter = new PriorityQueue<>();

        for (int i = 0; i < k; i++) {
            Customer customer = waitQue.poll();
            
            if (customer != null) {
                pqCounter.offer(new Counter(i,customer.id,customer.w));
            }
        }
        
        PriorityQueue<Integer> pqCounterDone = new PriorityQueue<>();
        
        int curTime = 0;
        ArrayList<Integer> result = new ArrayList<>();

        while (!pqCounter.isEmpty()) {
            // 먼저 끝난 카운터
            Counter counter = pqCounter.poll();
            
            result.add(counter.id);

            curTime = Math.max(curTime, counter.time);
            pqCounterDone.offer(counter.no);

            // 동시간 체크
            while (!pqCounter.isEmpty()) {
                if (pqCounter.peek().time == curTime) {
                    Counter c = pqCounter.poll();
                    result.add(c.id);
                    pqCounterDone.offer(c.no);
                } else {
                    break;
                }
            }
            
            // 계산대 배치
            while (!pqCounterDone.isEmpty()) {
                Customer customer = waitQue.poll();
                if (customer != null) {
                    pqCounter.offer(new Counter(pqCounterDone.poll(), customer.id, curTime + customer.w));
                } else {
                    break;
                }
            }    
        }
        
        return result;
    
    }
    
    
    static class Customer {
        int id;
        int w;
        
        public Customer(int id, int w) {
            this.id = id;
            this.w = w;
        }
    }
    
    static class Counter implements Comparable<Counter> {
        int no;
        int id;
        int time;
        
        public Counter(int no, int id, int time) {
            this.no = no;
            this.id = id;
            this.time = time;
        }
        
        @Override
        public int compareTo(Counter o) {
            // 먼저 끝나는게 먼저 나가고
            if (this.time < o.time) {
                return -1;
            } else if (this.time == o.time) {
                // 넘버가 큰게 먼저 나감
                if (this.no < o.no) {
                    return 1;
                } else {
                    return -1;
                }
            } else {
                return 1;
            }
        }
    }

}
