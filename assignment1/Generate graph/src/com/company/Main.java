package com.company;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;

public class Main {

    public static void main(String[] args){
        String graphFile = args[0];
        try {
            BufferedReader br = new BufferedReader(new FileReader(graphFile));
            String firstLine = br.readLine();
            int numEdges = Integer.parseInt(firstLine.split(" ")[1]);
            int curr = 0;
            while (numEdges > 0) {
                String line = br.readLine();
                int src = Integer.parseInt(line.split(" ")[0]);
                if (curr == src) {
                    numEdges--;
                    continue;
                }
                else if(curr == src - 1){
                    curr++;
                    numEdges--;
                    continue;
                }
                else {
                    curr++;
                    while (curr != src) {
                        System.out.print(curr);
                        System.out.print('\n');
                        curr++;
                    }
                    numEdges--;
                }
            }
        } catch(IOException ie){
            ie.printStackTrace();
        }
    }
}
