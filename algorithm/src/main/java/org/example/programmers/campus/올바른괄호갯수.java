package org.example.programmers.campus;
import java.util.*;
public class 올바른괄호갯수 {
    public int solution_my(int n) {
        int answer = 0;

        answer = set(n,1,0,0);

        return answer;
    }

    public int set(int end, int open, int close, int cnt) {

        if(open < close) return 0;
        if (open == end) return 1;

        return set(end, open+1, close, cnt)+set(end, open, close+1, cnt);

    }

    class P {
        int open, close;
        P(int o, int c) {
            open = o;
            close = c;
        }
    }
    public int solution(int n) {
        int answer = 0;

        Stack<P> stack = new Stack<>();
        stack.push(new P(0,0));
        while(!stack.isEmpty()){
            P p = stack.pop();

            if (p.open > n) continue;
            if (p.open < p.close) continue;
            if (p.open + p.close == 2*n) {
                answer ++;
                continue;
            }
            stack.push(new P(p.open+1, p.close));
            stack.push(new P(p.open, p.close+1));
        }

        return answer;
    }
}
