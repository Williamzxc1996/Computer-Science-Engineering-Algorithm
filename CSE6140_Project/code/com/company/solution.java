package com.company;

import java.util.*;

public class solution {
    public int cost;
    public int[] path;

    public solution(int cost, int[] path){
        this.cost = cost;
        this.path = path;
    }

    public void showSolution(){
        System.out.println("The solution found by ILS:");
        for(int i = 0; i < path.length; i++){
            if(i == path.length-1){
                System.out.println(path[i] + 1);
            }else{
                System.out.printf("%d, ",path[i] + 1);
            }
        }
        System.out.printf("The total cost of this path is %d\n", cost);
    }

    @Override
    public boolean equals(Object obj) {
        solution other = (solution) obj;
        return (this.cost == other.cost) && (Arrays.equals(this.path,other.path));
    }

    @Override
    public int hashCode() {
        return Arrays.hashCode(this.path);
    }
}
