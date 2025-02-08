package org.example.algorithm.greedy;

import java.util.ArrayList;
import java.util.Collections;
import java.util.*;

public class Greedy {
    /**
     * 매 순간 현재 기준으로 최선의 답을 선택해 나가는 기법
     * 최적해가 아닐 수도 있으며, 빠르게 근사치까지 계산이 가능하다.
     *
     * 아래 두가지 조건이 있는 경우 적용 가능하다.
     * + 지금선택이 다음선택에 영향을 주지 않음( substructure 인지아닌지 즉, 다음선택이 지금 선택으로 구성이 가능한지 )
     * + 전체 문제의 최적해는 부분 문제의 최적해로 이루어짐
     *
     * 에시
     * N개의 활동과 각 활동의 시작/종료 시간이 주어 질 때, 한사람이 최대한 많이 할 수 있는 활동 수 구하기
     *
     */

    public static void main(String[] args) {

        ArrayList<Activity> list = new ArrayList<>();
        list.add(new Activity("a",1,5));
        list.add(new Activity("b",4,5));
        list.add(new Activity("c",2,3));
        list.add(new Activity("d",4,7));
        list.add(new Activity("e",6,10));
        System.out.println("가능한 활동 선택하기");
        selectActivity(list);

        System.out.println();
        System.out.println("거스름돈 반환");

        getChangeCoin(1000,100);
        getChangeCoin(1234,500);

    }

    private static void getChangeCoin(int receivedMoney, int price) {
        final int[] coins = {500,100, 50, 10, 5, 1};// 가능한 화폐단위

        HashMap<Integer,Integer> result = new HashMap<>();

        int change = receivedMoney - price;
        int cnt = 0; //잔돈 갯수

        for (int i = 0; i < coins.length; i++) {
            if (change< coins[i]) {
                continue;
            }
            int q = change / coins[i];
            result.put(coins[i], result.getOrDefault(coins[i],0) + q);

            change %= coins[i];
            cnt += q;
        }

        System.out.println("거스름돈 동전 갯수 : "+ cnt);
        System.out.println("거스름돈 동전 종류");
        for (Map.Entry<Integer,Integer> cur : result.entrySet()) {
            System.out.println(cur.getKey() +" : "+cur.getValue());
        }
        System.out.println("=====================================");
        

    }

    private static void selectActivity(ArrayList<Activity> list) {
        Collections.sort(list, (x1, x2) -> x1.end - x2.end);// 오름차순 정렬

        int curTime = 0;
        ArrayList<Activity> result = new ArrayList<>();

        for (Activity act : list) {
            if (curTime <= act.start) {
                curTime = act.end;
                result.add(act);
            }
        }

        for (Activity act : result) {
            System.out.print(act.name + " ");
        }
        System.out.println();

    }

    static class Activity {
        String name;
        int start;
        int end;

        public Activity(String name, int start, int end) {
            this.name = name;
            this.start = start;
            this.end = end;
        }
    }

}
