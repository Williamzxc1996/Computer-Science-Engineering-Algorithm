package com.company;

public class CalculateLB {
    public static int start_calculate(int[][] adjacent_matrix){
        int lb = 0;
        //update lower bound
        //row first
        for(int k1 = 0; k1 < adjacent_matrix.length; k1++){
            int min = Integer.MAX_VALUE;
            //find the min value of this row, if this row is deleted than min == Integer.MAX_VALUE
            for(int k2 = 0; k2 < adjacent_matrix[0].length; k2++){
                if(adjacent_matrix[k1][k2] != -1){
                    min = Math.min(min,adjacent_matrix[k1][k2]);
                }
            }
            //if the row is not deleted than substract
            if(min != Integer.MAX_VALUE && min != 0){
                lb += min;
                for(int k2 = 0; k2 < adjacent_matrix[0].length; k2++){
                    if(adjacent_matrix[k1][k2] != -1){
                        adjacent_matrix[k1][k2] -= min;
                    }
                }
            }
        }
        //col
        for(int k1 = 0; k1 < adjacent_matrix[0].length; k1++){
            int min = Integer.MAX_VALUE;
            //find the min value of this col, if this row is deleted than min == Integer.MAX_VALUE
            for(int k2 = 0; k2 < adjacent_matrix.length; k2++){
                if(adjacent_matrix[k2][k1] != -1){
                    min = Math.min(min,adjacent_matrix[k2][k1]);
                }
            }
            //if the row is not deleted than substract
            if(min != Integer.MAX_VALUE && min != 0){
                lb += min;
                for(int k2 = 0; k2 < adjacent_matrix.length; k2++){
                    if(adjacent_matrix[k2][k1] != -1){
                        adjacent_matrix[k2][k1] -= min;
                    }
                }
            }
        }
        return lb;

    }

    public static int reducedMatrix_include(int[][] adjacent_matrix, int i, int j, int lb){
        //first delete row i and column j since we choose (i,j)
        for(int k = 0; k < adjacent_matrix.length; k++){
            adjacent_matrix[k][j] = -1;
        }
        for(int k = 0; k < adjacent_matrix[0].length; k++){
            adjacent_matrix[i][k] = -1;
        }
        //then set (j,i) to -1
        adjacent_matrix[j][i] = -1;

        //update lower bound
        //row first
        for(int k1 = 0; k1 < adjacent_matrix.length; k1++){
            int min = Integer.MAX_VALUE;
            //find the min value of this row, if this row is deleted than min == Integer.MAX_VALUE
            for(int k2 = 0; k2 < adjacent_matrix[0].length; k2++){
                if(adjacent_matrix[k1][k2] != -1){
                    min = Math.min(min,adjacent_matrix[k1][k2]);
                }
            }
            //if the row is not deleted than substract
            if(min != Integer.MAX_VALUE && min != 0){
                lb += min;
                for(int k2 = 0; k2 < adjacent_matrix[0].length; k2++){
                    if(adjacent_matrix[k1][k2] != -1){
                        adjacent_matrix[k1][k2] -= min;
                    }
                }
            }
        }
        //col
        for(int k1 = 0; k1 < adjacent_matrix[0].length; k1++){
            int min = Integer.MAX_VALUE;
            //find the min value of this col, if this row is deleted than min == Integer.MAX_VALUE
            for(int k2 = 0; k2 < adjacent_matrix.length; k2++) {
                if (adjacent_matrix[k2][k1] != -1) {
                    min = Math.min(min, adjacent_matrix[k2][k1]);
                }
            }
            //if the row is not deleted than substract
            if(min != Integer.MAX_VALUE && min != 0){
                lb += min;
                for(int k2 = 0; k2 < adjacent_matrix.length; k2++){
                    if(adjacent_matrix[k2][k1] != -1){
                        adjacent_matrix[k2][k1] -= min;
                    }
                }
            }
        }
        //return the new lower bound
        return lb;
    }
    public static boolean canExclude(int[][] adjacent_matrix, int i , int j){
        for(int k = 0; k < adjacent_matrix.length; k++){
            if(k != j && adjacent_matrix[i][k] != -1){
                return true;
            }
        }
        return false;
    }

    public static int reducedMatrix_exclude(int[][] adjacent_matrix, int i, int j, int lb){
        //first set (i,j) to -1
        adjacent_matrix[i][j] = -1;
        //update lower bound by traversing row i and col j
        int min = Integer.MAX_VALUE;
        for(int k = 0; k < adjacent_matrix[0].length; k++){
            if(adjacent_matrix[i][k] != -1){
                min = Math.min(min,adjacent_matrix[i][k]);
            }
        }

        if(min != Integer.MAX_VALUE && min != 0){
            lb += min;
            for(int k = 0; k < adjacent_matrix[0].length; k++){
                if(adjacent_matrix[i][k] != -1){
                    adjacent_matrix[i][k] -= min;
                }
            }
        }

        min = Integer.MAX_VALUE;
        for(int k = 0; k < adjacent_matrix.length; k++){
            if(adjacent_matrix[k][j] != -1){
                min = Math.min(min,adjacent_matrix[k][j]);
            }
        }
        if(min != Integer.MAX_VALUE && min != 0){
            lb += min;
            for(int k = 0; k < adjacent_matrix.length; k++){
                if(adjacent_matrix[k][j] != -1){
                    adjacent_matrix[k][j] -= min;
                }
            }
        }

        return lb;
    }

}
