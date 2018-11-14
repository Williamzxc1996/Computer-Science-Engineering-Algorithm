package com.company;

import java.time.*;
import java.util.*;

import static javafx.application.Platform.exit;

public class backtracking {
    public static int current_best;
    public static List<List<Integer>> optimal = new LinkedList<>();
    //public static int current_cost = 0;
    //public static int[][] cost;

    public backtracking(int best, List<Integer> solution){
        current_best = best;
        optimal.add(new LinkedList(solution));
    }
    public backtracking(){
        current_best = Integer.MAX_VALUE;
    }

    public static void backtracking(Set<Integer> check, int[][] adjcent_matrix, int source, int lb, List<Integer> temp, int s, LocalTime limit){
        //find a solution
        if(check.size() == adjcent_matrix.length && source == s){
            //is a solution
            System.out.println("find solution!");
            if(lb < current_best) {
                current_best = lb;
                if (optimal.size() == 1) optimal.clear();
                optimal.add(new LinkedList<>(temp));
                System.out.print("current best:");
                System.out.println(current_best);
            }
            return;
        }
        LocalTime now = LocalTime.now();
        if(limit.plusMinutes(60).isBefore(now)){
            System.out.print("current best:");
            System.out.println(current_best);
            System.exit(0);
        }

        //if already checked return, if it's not a solution and source == s return
        if(check.contains(source)){
            return;
        }

        check.add(source);

        int next = findMin(adjcent_matrix,source);
        //try include next
        if(next != -1) {
            int[][] adjMatrix_include = matrixCopy(adjcent_matrix);
            int lb_include = CalculateLB.reducedMatrix_include(adjMatrix_include, source, next, lb);
            if (lb_include < current_best) {
                /*
                System.out.print("include:");
                System.out.printf("(%d,%d)",source,next);
                System.out.println();
                System.out.println(lb_include);
                for(int i = 0; i < adjMatrix_include.length; i++){
                    System.out.print("[");
                    for(int j = 0; j < adjMatrix_include[0].length; j++){
                        System.out.printf("%d,     ",adjMatrix_include[i][j]);
                    }
                    System.out.println();
                }
                */
                temp.add(next);
                backtracking(check, adjMatrix_include, next, lb_include, temp, s, limit);
                temp.remove(temp.size() - 1);
            }

            check.remove(source);

            //try exclude
            int[][] adjMatrix_exclude = matrixCopy(adjcent_matrix);
            if (CalculateLB.canExclude(adjMatrix_exclude, source, next)) {
                int lb_exclude = CalculateLB.reducedMatrix_exclude(adjMatrix_exclude, source, next, lb);
                //if already visited next or next is the last edge leaving source than lb_exclude will return Integer.MAX_VALUE
                if (lb_exclude < current_best) {
                    /*
                    System.out.print("exclude:");
                    System.out.printf("(%d,%d)", source, next);
                    System.out.println();
                    System.out.println(lb_exclude);
                    for(int i = 0; i < adjMatrix_exclude.length; i++){
                        System.out.print("[");
                        for(int j = 0; j < adjMatrix_exclude[0].length; j++){
                            System.out.printf("%d,     ",adjMatrix_exclude[i][j]);
                        }
                        System.out.println();
                    }
                    */
                    backtracking(check, adjMatrix_exclude, source, lb_exclude, temp, s, limit);
                }
            }
        }

        return;
    }

    public static int[][] matrixCopy(int[][] adjacent_matrix){
        int[][] result = new int[adjacent_matrix.length][adjacent_matrix[0].length];
        for(int i = 0; i < result.length; i++){
            for(int j = 0; j < result[0].length; j++){
                result[i][j] = adjacent_matrix[i][j];
            }
        }
        return result;
    }

    public static int findMin(int[][] adjacent_matrix, int source){
        int min_value = Integer.MAX_VALUE;
        int min_index = -1;
        for(int i = 0; i < adjacent_matrix.length; i++){
            if(adjacent_matrix[source][i] != -1 && adjacent_matrix[source][i] < min_value){
                min_value = adjacent_matrix[source][i];
                min_index = i;
            }
        }
        return min_index;       //note that min_index won't be -1 because in the code I already check if (source,next) can be excluded
    }
}
