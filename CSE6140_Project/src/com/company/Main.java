package com.company;

import java.io.IOException;
import java.time.*;
import java.util.*;

public class Main {

    public static void main(String[] args)throws IOException {
	// write your code here
        String graph_file = args[0];
        int[][] adjacent_matrix = FileIO.readGraph_AdjMatrix(graph_file);
        List<Integer> solution = new LinkedList();
        int best = 0;
        for(int i = 0; i < adjacent_matrix.length; i++){
            if(i < adjacent_matrix.length - 1) {
                best += adjacent_matrix[i][i + 1];
                solution.add(i+1);
            }
            else{
                best += adjacent_matrix[i][0];
                solution.add(0);
            }
        }
        Random rd = new Random();
        int source = rd.nextInt(adjacent_matrix.length);
        int lb = CalculateLB.start_calculate(adjacent_matrix);
        backtracking bt = new backtracking(best,solution);
        List<Integer> temp = new LinkedList();
        temp.add(source);
        LocalTime start = LocalTime.now();
        bt.backtracking(new HashSet<>(),adjacent_matrix,source,lb,temp,source, start);
        System.out.println(backtracking.current_best);
        for(int i = 0; i < adjacent_matrix.length; i++){
            System.out.printf("%d, ",backtracking.optimal.get(0).get(i));
        }
    }
}
