package com.company;

import java.io.IOException;
import java.io.PrintWriter;
import java.time.LocalTime;
import java.util.HashSet;
import java.util.Random;
import java.util.Set;

public class ILS {
    public static void solve(String graph_file, int time_cutoff, long seed) throws IOException{

        int index = graph_file.lastIndexOf('/');
        String output_file_name = graph_file.substring(index);
        index = output_file_name.indexOf('.');
        output_file_name = output_file_name.substring(0,index);

        int[][] adj = FileIO.readGraph_AdjMatrix(graph_file);


        Random generator = new Random(seed);
        String output_file = "./" + output_file_name + "_ILS_"+String.valueOf(time_cutoff)+"_"+String.valueOf(seed)+".trace";
        PrintWriter output;
        output = new PrintWriter(output_file, "UTF-8");


        Set<solution> history = new HashSet<>();
        LocalTime start = LocalTime.now();
        solution s0 = generate_s0(generator, adj);
        solution s_lc = LocalSearch.LC(s0, adj,history);
        output.printf("%f %d\n", 0.0, s_lc.cost);
        LocalTime now = LocalTime.now();
        while (start.plusSeconds(time_cutoff).isAfter(now)) {
            solution s_perturb = Perturbation.perturbation(s_lc, history, adj, generator);
            solution s_pertub_lc = LocalSearch.LC(s_perturb, adj,history);
            if (s_lc.cost > s_pertub_lc.cost) {
                s_lc = s_pertub_lc;
                output.printf("%f %d\n", (LocalTime.now().toNanoOfDay() - start.toNanoOfDay()) / (double)1000000000, s_lc.cost);
            }
            now = LocalTime.now();
        }
        s_lc.showSolution();
        output.close();

        //write the solution file
        String output_file2 = "./" + output_file_name + "_ILS_"+String.valueOf(time_cutoff)+"_"+String.valueOf(seed)+".sol";
        PrintWriter output2;
        output2 = new PrintWriter(output_file2, "UTF-8");
        output2.println(s_lc.cost);
        for(int k = 0; k < s_lc.path.length-1; k++){
            if(k == s_lc.path.length-2){
                output2.println(s_lc.path[k] + 1);
            }else{
                output2.printf("%d, ",s_lc.path[k] + 1);
            }
        }

        output2.close();
    }

    public static void solve(String graph_file, int time_cutoff) throws IOException {
        int index = graph_file.lastIndexOf('/');
        String output_file_name = graph_file.substring(index);
        index = output_file_name.indexOf('.');
        output_file_name = output_file_name.substring(0,index);

        int[][] adj = FileIO.readGraph_AdjMatrix(graph_file);


        long seed = System.currentTimeMillis();
        Random generator = new Random(seed);
        String output_file = "./" + output_file_name + "_ILS_" + String.valueOf(time_cutoff) + "_" + String.valueOf(seed) + ".trace";
        PrintWriter output;
        output = new PrintWriter(output_file, "UTF-8");


        Set<solution> history = new HashSet<>();
        LocalTime start = LocalTime.now();
        solution s0 = generate_s0(generator, adj);
        solution s_lc = LocalSearch.LC(s0, adj,history);
        output.printf("%f %d\n", 0.0, s_lc.cost);
        LocalTime now = LocalTime.now();
        while (start.plusSeconds(time_cutoff).isAfter(now)) {
            solution s_perturb = Perturbation.perturbation(s_lc, history, adj, generator);
            solution s_pertub_lc = LocalSearch.LC(s_perturb, adj,history);
            if (s_lc.cost > s_pertub_lc.cost) {
                s_lc = s_pertub_lc;
                output.printf("%f %d\n", (LocalTime.now().toNanoOfDay() - start.toNanoOfDay()) / (double)1000000000, s_lc.cost);
            }
            now = LocalTime.now();
        }
        s_lc.showSolution();
        output.close();

        //write the solution file
        String output_file2 = "./" + output_file_name + "_ILS_"+String.valueOf(time_cutoff)+"_"+String.valueOf(seed)+".sol";
        PrintWriter output2;
        output2 = new PrintWriter(output_file2, "UTF-8");
        output2.println(s_lc.cost);
        for(int k = 0; k < s_lc.path.length-1; k++){
            if(k == s_lc.path.length-2){
                output2.println(s_lc.path[k]+1);
            }else{
                output2.printf("%d, ",s_lc.path[k]+1);
            }
        }

        output2.close();
    }

    public static solution generate_s0(Random generator, int[][] adj) {
        int l = adj.length;
        int[] result = new int[l+1];
        int cost = 0;
        Set<Integer> check = new HashSet();
        check.add(0);
        result[0] = 0;
        result[l] = 0;
        for(int i = 1; i < l; i++){
            result[i] = generator.nextInt(l);
            while(check.contains(result[i])){
                result[i] = generator.nextInt(l);
            }
            check.add(result[i]);
            cost += adj[result[i-1]][result[i]];
        }
        cost += adj[result[l-1]][result[l]];


        return new solution(cost,result);
    }
}
