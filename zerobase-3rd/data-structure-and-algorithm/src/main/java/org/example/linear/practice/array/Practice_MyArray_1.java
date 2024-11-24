package org.example.linear.practice.array;

import java.util.*;

public class Practice_MyArray_1 {

    int[] arr;
    Practice_MyArray_1(int size) {
        this.arr = new int[size];
    }

    public void insertData(int index, int data) {
        if (index < 0 || index > this.arr.length) {
            System.out.println("Index Error");
            return;
        }

        int[] arrDup = this.arr.clone();

        this.arr = new int[this.arr.length+1];

        for (int i = 0; i < index; i++) {
            this.arr[i] = arrDup[i];
        }

        for (int i = index + 1; i < this.arr.length; i++) {
            this.arr[i] = arrDup[i-1];
        }

        this.arr[index] = data;

    }

    public void removeData(int data) {
        int targetIndex = -1;

        for (int i = 0; i < this.arr.length; i++) {
            if (this.arr[i] == data) {
                targetIndex = i;
                break;
            }
        }

        if (targetIndex == -1) {
            System.out.println("해당 데이터가 없습니다.");
        } else {
            int[] arrDup = this.arr.clone();
            this.arr = new int[this.arr.length -1];

            for (int i = 0; i < targetIndex; i++) {
                this.arr[i] = arrDup[i];
            }

            for (int i = targetIndex; i < this.arr.length; i++) {
                this.arr[i] = arrDup[i+1];
            }

        }


    }

}


class Practice_MyArray_1_sub {
    public static void main(String[] args) {
        int size = 5;
        Practice_MyArray_1 practiceMyArray1 = new Practice_MyArray_1(size);

        for (int i = 0; i < size; i++) {
            practiceMyArray1.arr[i] = i+1;
        }

        practiceMyArray1.insertData(2,6);
        System.out.println(Arrays.toString(practiceMyArray1.arr));

        practiceMyArray1.insertData(5,7);
        System.out.println(Arrays.toString(practiceMyArray1.arr));

        practiceMyArray1.removeData(7);
        System.out.println(Arrays.toString(practiceMyArray1.arr));

        practiceMyArray1.removeData(99);
        System.out.println(Arrays.toString(practiceMyArray1.arr));

    }
}












