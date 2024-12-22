package org.example.linear.practice2;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.*;

/**
 * 카카오 문제
 * 스트리밍문제 입니다.
 *
 * 노래 재생이 많이 재생된 장르 먼저 수록
 * 장르 내에 많이 재생된 노래 먼저 수록
 * 장르 내 재생 횟수가 같은 노래중에서 고유번호가 낮은 노래를 먼저 수록
 * 장르 별로는 2개만 수록합니다.
 */

public class Practice_3 {
    public static void main(String[] args) {
        String[] genres = {"classic","pop","classic","classic","pop"};
        int[] plays = {500,600,150,800,2500};
        System.out.println("answer : " +solution(genres,plays));;
        
        int[] answer = {4,1,3,0};

        System.out.println("Arrays.toString(answer) = " + Arrays.toString(answer));
    }

    private static String solution(String[] genres, int[] plays) {

        Hashtable<String, ArrayList<Song>> ht = new Hashtable<>();

        for (int i = 0; i < genres.length; i++) {
            if (ht.containsKey(genres[i])) {
                ArrayList<Song> cur = ht.get(genres[i]);

                int idx = -1;
                for (int j = 0; j < cur.size(); j++) {
                    if (plays[i] > cur.get(j).play
                            || plays[i] == cur.get(j).play
                            && i < cur.get(j).no) {
                        idx = j;
                        break;
                    }
                }

                // 가장 뒷쪽에
                if (idx == -1) {
                    cur.add(new Song(i,plays[i]));
                } else {
                    // 해당 순번에 붙는다.
                    cur.add(idx, new Song(i,plays[i]));
                }

                ht.put(genres[i],cur);

            } else {
                Song song = new Song(i, plays[i]);
                ht.put(genres[i], new ArrayList<>(Arrays.asList(song)));
            }
        }

        // 어떤 장르가 많이 재생 되었는지 확인
        Hashtable<String,Integer> htPlay = new Hashtable<>();
        for (String item : ht.keySet()) {
            int sum = 0;

            ArrayList<Song> cur = ht.get(item);

            for (int i = 0; i < cur.size(); i++) {
                sum += cur.get(i).play;
            }
            htPlay.put(item,sum); // 장르별 재생 획수 구현
        }

        ArrayList<Map.Entry<String,Integer>> htPlaySort = new ArrayList<>(htPlay.entrySet());
        htPlaySort.sort((x1,x2) -> x2.getValue() - x1.getValue()); // 오름차순 구현
        
        StringBuilder answer = new StringBuilder();

        for (Map.Entry<String,Integer> item : htPlaySort) {
            ArrayList<Song> songs = ht.get(item.getKey());
            for (int i = 0; i < songs.size(); i++) {
                answer.append(songs.get(i).no).append(" ");
                if (i == 1){
                    break;
                }
            }
        }

        return answer.toString();
    }

    static class Song {
        int no;
        int play;

        public Song(int no, int play) {
            this.no = no;
            this.play = play;
        }
    }
}
