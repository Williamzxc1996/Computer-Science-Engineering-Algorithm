package com.company;

import java.util.*;

public class LocalSearch{
    public static solution LC(solution s, int[][] adj, Set<solution> history){
        //for given solution s construct the neighbors
        history.add(s);
        List<solution> neighbors = new LinkedList();
        for(int i = 0; i+1 < s.path.length-2; i++){
            for(int j = i+2; j+1 < s.path.length && s.path[j+1] != s.path[i]; j++){
                //2-exchange
                int new_cost = s.cost - adj[s.path[i]][s.path[i+1]] - adj[s.path[j]][s.path[j+1]] + adj[s.path[i]][s.path[j]] + adj[s.path[i+1]][s.path[j+1]];
                int[] new_path = new int[s.path.length];
                //flip the element between i+1 and j
                for(int k = 0, m = j; k < s.path.length; k++){
                    if(k < i+1){
                        new_path[k] = s.path[k];
                    }else if(k > j){
                        new_path[k] = s.path[k];
                    }else{
                        //between i+1 and j
                        new_path[k] = s.path[m--];
                    }
                }
                neighbors.add(new solution(new_cost,new_path));
            }
        }
        //find best next neighbor
        solution best_neighbor = new solution(s.cost,s.path);
        for(solution s_n : neighbors){
            if(s_n.cost < best_neighbor.cost){
                best_neighbor.cost = s_n.cost;
                best_neighbor.path = s_n.path;
            }
        }

        //if exist a neighbor with better cost than s, continue; else return current solution as local optimal
        if(best_neighbor.equals(s)){
            return s;
        }else{
            return LC(best_neighbor,adj,history);
        }
    }
}
