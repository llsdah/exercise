package org.example.mathematics;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;

/**
 * 집합 1
 * 특정조건에 맞는 원소들의 모임
 * 집합 표현 방법
 * - 원소 나열법
 * - 조건 제시법
 * 벤 다이어그램
 *
 * 교집합
 * 두집합이 공통으로 포함하는 원소로 이루어진 집합
 *
 * 합집합
 * 어느 하나에라도 속하는 원소들을 모두 모은 집합
 *
 * 차집합
 * A 또는 B 에만 속하는 원소들의 집합
 *
 * 여집합
 * 전체 집합 중 특정 A의 원소가 아닌 집합
 */
class MySet1 {
    ArrayList<Integer> list;

    MySet1() {
        this.list = new ArrayList<>();
    }

    MySet1(int[] arr){
        this.list = new ArrayList<>();

        for (int item : arr) {
            this.list.add(item);
        }
    }

    // 원소 넣기 - 중복값 제거 필요
    public void add (int x) {
        for (int item : this.list) {
            if (item == x) {
                return;
            }
        }
        this.list.add(x);
    }

    // 교집합
    public MySet1 retainAll(MySet1 b){
        MySet1 result = new MySet1();

        for (int itemA : this.list) {
            for (int itemB : b.list) {
                if (itemA == itemB) {
                    result.add(itemA);
                }
            }
        }
        return result;
    }

    // 합집합
    public MySet1 addAll(MySet1 b) {
        MySet1 result = new MySet1();
        for (int itemA : this.list) {
            result.add(itemA);
        }

        for (int itemB : b.list) {
            result.add(itemB);
        }
        return result;

    }

    // 차집합
    public MySet1 removeAll(MySet1 b){
        MySet1 result = new MySet1();

        for (int itemA : this.list) {
            boolean containFlag = false;
            // 같은 값이 있다면 굳이 다시 순회 안해도됩니다. 제거하면됩니다.
            for (int itemB : b.list) {
                if (itemA == itemB) {
                    containFlag = true;
                    break;
                }
            }
            if (!containFlag) {
                result.add(itemA);
            }
        }
        return result;
    }

}

public class Set1 {

    public static void main(String[] args) {

        System.out.println("== Hash set==");

        System.out.println("집합연산 HashSet 사용");

        // 교집합 연산
        HashSet a = new HashSet(Arrays.asList(1,2,3,4,5));
        HashSet b = new HashSet(Arrays.asList(2,4,6,8,10));

        a.retainAll(b);
        System.out.println("a = " + a);

        // 합집합
        a.addAll(b);
        System.out.println("a = " + a);

        // 차집합
        a.removeAll(b);
        System.out.println("a = " + a);


        // MySet1 수행
        MySet1 a1 = new MySet1();

        a1.add(1);
        a1.add(2);
        a1.add(3);
        a1.add(4);
        System.out.println("a1 = " + a1.list);
        a1.add(1);
        a1.add(4);
        System.out.println("a1 = " + a1.list);

        MySet1 b1 = new MySet1(new int[]{2,4,6,7});
        System.out.println("b1 = " + b1.list);

        MySet1 result = a1.retainAll(b1);
        System.out.println("result = " + result.list);

        result = a1.removeAll(b1);
        System.out.println("result = " + result.list);

        result = a1.addAll(b1);
        System.out.println("result = " + result.list);








    }

}


