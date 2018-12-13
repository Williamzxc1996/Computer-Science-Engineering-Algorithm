package com.company;

import java.io.IOException;

public class Main {

    public static void main(String[] args)throws IOException, java.lang.InterruptedException {
        if (args.length < 3) {
            System.err.println("Unexpected number of command line arguments");
            System.exit(1);
        }
        String graphFile = "";
        String method = "";
        int time = 0;
        long seed = 0;

        if(args.length == 3){
            graphFile = args[0];
            method = args[1];
            time = Integer.parseInt(args[2]);
        }
        if(args.length == 4){
            graphFile = args[0];
            method = args[1];
            time = Integer.parseInt(args[2]);
            seed = Long.parseLong(args[3]);
        }

        if(method.equals("LS1")){
            if(seed == 0){
                ILS.solve(graphFile,time);
            }else{
                ILS.solve(graphFile,time,seed);
            }
        }else if(method.equals("BnB")){
            BNB.solve(graphFile, time);
        }else if(method.equals("Approx")){
            MSTApprox.solve(graphFile, time);
        }else {
            System.out.println("The required method is not supported.");
            System.exit(1);
        }
    }
}
