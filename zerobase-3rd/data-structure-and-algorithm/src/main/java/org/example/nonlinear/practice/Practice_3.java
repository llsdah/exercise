package org.example.nonlinear.practice;

import java.util.*;

/**
 * 이메일 정보를 병합 하고 출력 하는 프로그램
 * accounts[][]
 *
 * {
 *     {"john", "john@main.com"}
 *     ,{"john", "john@main.com","john1@main.com"}
 *     ,{"mary", "may@main.com"}
 *     ,{"john", "john@3main.com"}
 *
 * }
 * 한명이 다건의 메일 소유 가능
 * 이름이 같고 메일이 같으면 동일인
 * 이름이 같고 메일이 다르면 동명이인
 *
 */
public class Practice_3 {

    public static void main(String[] args) {
        ArrayList<ArrayList<String>> list = new ArrayList<>();

        list.add(new ArrayList<>(Arrays.asList("john", "john@main.com")));
        list.add(new ArrayList<>(Arrays.asList("john", "john@main.com","john1@main.com")));
        list.add(new ArrayList<>(Arrays.asList("mary", "may@main.com")));
        list.add(new ArrayList<>(Arrays.asList("john", "john@3main.com")));

        list = solution(list);

        for(ArrayList<String> item : list) {
            System.out.println(item);
        }

    }

    private static ArrayList<ArrayList<String>> solution(ArrayList<ArrayList<String>> list) {
        ArrayList<ArrayList<String>> result = new ArrayList<>();

        HashMap<String, HashSet<String>> graph = new HashMap<>();
        HashMap<String,String> name = new HashMap<>();

        for (ArrayList<String> account : list ){
            String user = account.get(0);

            for (int i = 1; i < account.size(); i++) {
                if (!graph.containsKey(account.get(i))) {
                    // 이메일 주소에
                    graph.put(account.get(i), new HashSet<>());
                }

                // 이메일 주소와 이름
                name.put(account.get(i), user);

                if (i == 1) {
                    continue;
                }

                // 이메일 주소에 같은 소유자의 이메일 주소들을 넣는다.
                graph.get(account.get(i)).add(account.get(i-1));
                graph.get(account.get(i -1)).add(account.get(i));
            }
        }

        HashSet<String> visit = new HashSet<>();

        for (String email : name.keySet()) {
            ArrayList<String> lists = new ArrayList<>();
            if (visit.add(email)) {
                dfs(graph,email,visit,lists);
                Collections.sort(lists);
                // 이메일 - 사람이름
                lists.add(0, name.get(email));
                result.add(lists);
            }
        }

        return result;
    }

    private static void dfs(HashMap<String, HashSet<String>> graph, String email, HashSet<String> visit, ArrayList<String> lists) {
        lists.add(email);
        for(String next : graph.get(email)) {

            if (visit.add(next)){
                dfs(graph,next,visit,lists);

            }
        }
    }


}
