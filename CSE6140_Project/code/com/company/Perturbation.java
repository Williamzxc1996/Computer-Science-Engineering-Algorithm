package com.company;
import java.util.*;


public class Perturbation {
    public static solution perturbation(solution s, Set<solution> history, int[][] adj, Random r){
        int time = 0;
        solution s_new;
        do {
            //generate index
            Set<Integer> check = new HashSet();
            int count = 0;
            int[] pivots = new int[4];
            while (count < 4) {
                int num = r.nextInt(adj.length);
                int left = num == 0 ? adj.length - 1 : num - 1;
                int right = num == adj.length - 1 ? 0 : num + 1;
                if (!check.contains(num)) {
                    check.add(num);
                    check.add(left);
                    check.add(right);
                    pivots[count++] = num;
                }
            }
            Arrays.sort(pivots);
            //perturb
            int new_cost = s.cost - adj[s.path[pivots[0]]][s.path[pivots[0] + 1]] - adj[s.path[pivots[1]]][s.path[pivots[1] + 1]] - adj[s.path[pivots[2]]][s.path[pivots[2] + 1]] - adj[s.path[pivots[3]]][s.path[pivots[3] + 1]] + adj[s.path[pivots[0]]][s.path[pivots[2] + 1]] + adj[s.path[pivots[0] + 1]][s.path[pivots[2]]] + adj[s.path[pivots[1]]][s.path[pivots[3] + 1]] + adj[s.path[pivots[1] + 1]][s.path[pivots[3]]];      //for our problem is undirected graph
            int[] new_path = new int[s.path.length];
            int k = 0;
            while (k <= pivots[0]) {
                new_path[k] = s.path[k];
                k++;
            }
            //k == pivot[0]+1
            int next = pivots[2] + 1;
            while (next <= pivots[3]) {
                new_path[k++] = s.path[next++];
            }
            //next == pivots[3]+1
            next = pivots[1] + 1;
            while (next <= pivots[2]) {
                new_path[k++] = s.path[next++];
            }
            //next == pivots[2]+1
            next = pivots[0] + 1;
            while (next <= pivots[1]) {
                new_path[k++] = s.path[next++];
            }
            //next == pivots[1]
            next = pivots[3] + 1;
            while (next < s.path.length) {
                new_path[k++] = s.path[next++];
            }
            s_new = new solution(new_cost, new_path);
            time++;
        }while(history.contains(s_new) && time <= 50);

        if(!history.contains(s_new)) history.add(s_new);
        return s_new;

    }
}
