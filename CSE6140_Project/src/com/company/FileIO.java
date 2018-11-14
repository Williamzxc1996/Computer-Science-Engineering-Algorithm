package com.company;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;

public class FileIO {
    public static int[][] readGraph_AdjMatrix(String graphFile)throws IOException{
        BufferedReader br = new BufferedReader(new FileReader(graphFile));
        //first line is city name
        br.readLine();
        //second line is comment
        br.readLine();
        //third line is demension
        int number_locations = Integer.parseInt(br.readLine().split(" ")[1]);
        //forth line is data type
        String data_type = br.readLine().split(" ")[1];
        //fifth line is nothing
        br.readLine();
        Location[] temp = new Location[number_locations];
        String line;
        int i = 0;
        //real data
        while(!(line = br.readLine()).equals("EOF")){
            String[] strs = line.split(" ");
            temp[i++] = new Location(Integer.parseInt(strs[0]),Double.parseDouble(strs[1]),Double.parseDouble(strs[2]));
        }
        int[][] adjacent_matrix = new int[number_locations][number_locations];
        if(data_type.equals("EUC_2D")){
            for(i = 0; i < number_locations; i++){
                for(int j = 0; j < number_locations; j++){
                    if(i == j) adjacent_matrix[i][i] = -1;
                    else{
                        adjacent_matrix[i][j] = (int)Math.round(Math.sqrt(Math.pow(temp[i].x-temp[j].x,2) + Math.pow(temp[i].y-temp[j].y,2)));
                    }
                }
            }
        }
        return adjacent_matrix;
    }
}
