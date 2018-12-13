package com.company;
import java.io.PrintWriter;
import java.util.*;

public class BNB {
    public static void solve(String graphFile, int time) throws java.io.IOException {
        int instance_start = graphFile.lastIndexOf("/") + 1;
        String instance = graphFile.substring(instance_start, graphFile.length() - 4);
        String outputFile = "./" + instance + "_BnB_" + Integer.toString(time) + ".sol";
        String traceFile = "./" + instance + "_BnB_" + Integer.toString(time) + ".trace";
        Graph graph = GraphReader.readGraph(graphFile);
        PrintWriter output = new PrintWriter(traceFile, "UTF-8");
        PrintWriter sol_output = new PrintWriter(outputFile, "UTF-8");
        time = time * 1000;

        Set<Integer> nodes= graph.getNodes();

        int size = nodes.size();
        int startPoint = 0;
        int maxDistance = Integer.MAX_VALUE;
        int matrix[][] = new int[size+1][size+1];
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                matrix[i][j] = graph.getDistance(i, j);
                if ((i != j) && matrix[i][j] < maxDistance) {
                    maxDistance = matrix[i][j];
                }
            }
            matrix[i][i] = -1;
        }
        for (int i = 0; i < size; i++)
            matrix[i][size] = 1;
        for (int j = 0; j < size; j++)
            matrix[size][j] = 1;

        int lb = 0;
        for (int i = 0; i < size; i++) {
            int min = Integer.MAX_VALUE;
            for (int j = 0; j < size; j++) {
                if (matrix[i][j] >= 0 && matrix[i][j] < min) {
                    min = matrix[i][j];
                }
            }
            lb += min;
            for (int j = 0; j < size; j++) {
                matrix[i][j] -= min;
            }
        }
        for (int j = 0; j < size; j++) {
            int min = Integer.MAX_VALUE;
            for (int i = 0; i < size; i++) {
                if (matrix[i][j] >= 0 && matrix[i][j] < min) {
                    min = matrix[i][j];
                }
            }
            lb += min;
            for (int i = 0; i < size; i++) {
                matrix[i][j] -= min;
            }
        }


        long start = System.currentTimeMillis();
        List<Integer> paths = new ArrayList<>();
        paths.add(startPoint);
        int col = minimum(matrix[startPoint], size);

        HSolution solution = reduceMatrixSolver(matrix, size, startPoint, col, Integer.MAX_VALUE, paths, lb, start, time, output);
        output.close();

        if (solution == null) {
            System.out.println("Can't find a valid solution. Try again with longer time.");
            return;
        }

        System.out.println("The solution found by BnB:");
        solution.showSolution();

        paths = solution.getPaths();
        int totalDistance = solution.getDistance();
        sol_output.println(totalDistance);
        for (int i = 0; i < paths.size() - 2; i++) {
            sol_output.print(Integer.toString(paths.get(i) + 1) + ", ");
        }
        sol_output.println(paths.get(paths.size() - 2) + 1);
        sol_output.close();


    }


    private static int minimum(int array[], int size) {
        for (int i = 0; i < size; i++)
            if (array[i] == 0)
                return i;
        return -1;
    }


    private static HSolution reduceMatrixSolver(int matrix[][], int size, int row, int minIndex, int upperbound, List<Integer> partSolution, int lowerbound, long start, long time, PrintWriter output) {
        // check all cities are included in the path
        if (partSolution.size() == size) {
            List<Integer> paths = new ArrayList<>(partSolution);
            paths.add(minIndex);
            double running_time = 1. * (System.currentTimeMillis() - start) / 1000;
            output.println(Double.toString( running_time) + ", " + Integer.toString(lowerbound));
            return new HSolution(lowerbound, paths);
        }

        // Include
        if (System.currentTimeMillis() - start > time)
            return null;

        HSolution solution = includeEdge(matrix, size, row, minIndex, upperbound, lowerbound, partSolution, start, time, output);
        if (solution != null)
            upperbound = solution.getDistance();

        // Exclude
        if (System.currentTimeMillis() - start > time)
            return solution;

        HSolution newSolution = excludeEdge(matrix, size, row, minIndex, upperbound, lowerbound, partSolution, start, time, output);
        if (newSolution != null)
            return newSolution;

        return solution;
    }

    private static HSolution excludeEdge(int matrix[][], int size, int row, int col, int upperbound, int lowerbound, List<Integer> partSolution, long start, long time, PrintWriter output) {
        matrix[row][col] = -1;
        int deleteRow = -1;
        int deleteCol = -1;
        for (int j = 0; j < size; j++) {
            if (matrix[row][j] < 0)
                continue;
            if (deleteRow > matrix[row][j] || deleteRow == -1)
                deleteRow = matrix[row][j];
        }
        if (deleteRow == -1) {
            matrix[row][col] = 0;
            return null;
        }
        for (int i = 0; i < size; i++) {
            if (matrix[i][col] < 0)
                continue;
            if (deleteCol > matrix[i][col] || deleteCol == -1)
                deleteCol = matrix[i][col];
        }
        if (deleteCol == -1) {
            matrix[row][col] = 0;
            return null;
        }
        lowerbound += deleteCol;
        lowerbound += deleteRow;
        if (lowerbound >= upperbound) {
            matrix[row][col] = 0;
            return null;
        }
        for (int j = 0; j < size; j++) {
            if (matrix[row][j] < 0)
                continue;
            matrix[row][j] -= deleteRow;
        }
        for (int i = 0; i < size; i++) {
            if (matrix[i][col] < 0)
                continue;
            matrix[i][col] -= deleteCol;
        }
        int newCol = minimum(matrix[row], size);
        HSolution solution = reduceMatrixSolver(matrix, size, row, newCol, upperbound, partSolution, lowerbound, start, time, output);
        for (int i = 0; i < size; i++) {
            if (matrix[i][col] < 0)
                continue;
            matrix[i][col] += deleteCol;
        }
        for (int j = 0; j < size; j++) {
            if (matrix[row][j] < 0)
                continue;
            matrix[row][j] += deleteRow;
        }
        matrix[row][col] = 0;

        return solution;
    }

    private static HSolution includeEdge(int matrix[][], int size, int row, int col, int upperbound, int lowerbound, List<Integer> partSolution, long start, long time, PrintWriter output) {
        int delete_item = matrix[col][row];
        matrix[col][row] = -1;
        int[] delete_iten_row = new int[size];
        if (delete_item != -1) {
            int min = Integer.MAX_VALUE;
            for (int i = 0; i < size; i++) {
                delete_iten_row[i] = matrix[col][i];
                if (matrix[col][i] < min && matrix[col][i] >= 0) {
                    min = matrix[col][i];
                }
            }
            for (int i = 0; i < size; i++) {
                if (matrix[col][i] < 0)
                    continue;
                matrix[col][i] -= min;
            }
            lowerbound += min;
        }

        int newCol = minimum(matrix[col], size);
        if (newCol == -1) {
            if (delete_item != -1) {
                for (int i = 0; i < size; i++) {
                    matrix[col][i] = delete_iten_row[i];
                }
            }
            matrix[col][row] = delete_item;
            return null;
        }

        // Delete the chosen row and column

        int currentRow[] = new int[size];
        int currentCol[] = new int[size];
        for (int j = 0; j < size; j++) {
            currentRow[j] = matrix[row][j];
            currentCol[j] = matrix[j][col];
        }
        for (int j = 0; j < size; j++) {
            matrix[row][j] = -1;
            matrix[j][col] = -1;
        }

        matrix[row][size] = 0;
        matrix[size][col] = 0;

        // update matrix and lowerbound
        int changeRow[] = new int[size];
        int changeCol[] = new int[size];

        for (int j = 0; j < size; j++) {
            changeCol[j] = -1;
            changeRow[j] = -1;
        }

        // update lowerbound
        for (int i = 0; i < size; i++) {
            if (matrix[i][size] == 0)
                continue;
            for (int j = 0; j < size; j++) {
                if (matrix[i][j] < 0)
                    continue;
                if (matrix[i][j] < changeRow[i] || changeRow[i] == -1)
                    changeRow[i] = matrix[i][j];
            }
            if (changeRow[i] != -1) {
                lowerbound += changeRow[i];
            }
        }

        if (lowerbound >= upperbound) {
            for (int j = 0; j < size; j++) {
                matrix[j][col] = currentCol[j];
                matrix[row][j] = currentRow[j];
            }
            matrix[row][size] = 1;
            matrix[size][col] = 1;

            if (delete_item != -1) {
                for (int i = 0; i < size; i++) {
                    matrix[col][i] = delete_iten_row[i];
                }
            }

            matrix[col][row] = delete_item;
            return null;
        }

        for (int i = 0; i < size; i++) {
            if (changeRow[i] != -1) {
                for (int j = 0; j < size; j++) {
                    if (matrix[i][j] < 0)
                        continue;
                    matrix[i][j] -= changeRow[i];
                }
            }
        }



        for (int j = 0; j < size; j++) {
            if (matrix[size][j] == 0)
                continue;
            for (int i = 0; i < size; i++) {
                if (matrix[i][j] < 0)
                    continue;
                if (matrix[i][j] < changeCol[j] || changeCol[j] == -1)
                    changeCol[j] = matrix[i][j];
            }
            if (changeCol[j] != -1) {
                lowerbound += changeCol[j];
            }
        }

        if (lowerbound >= upperbound) {
            for (int i = 0; i < size; i++) {
                if (changeRow[i] != -1) {
                    for (int j = 0; j < size; j++) {
                        if (matrix[i][j] < 0)
                            continue;
                        matrix[i][j] += changeRow[i];
                    }
                }
            }
            for (int j = 0; j < size; j++) {
                matrix[j][col] = currentCol[j];
                matrix[row][j] = currentRow[j];
            }
            matrix[row][size] = 1;
            matrix[size][col] = 1;

            if (delete_item != -1) {
                for (int i = 0; i < size; i++) {
                    matrix[col][i] = delete_iten_row[i];
                }
            }

            matrix[col][row] = delete_item;
            return null;
        }

        // update matrix
        for (int j = 0; j < size; j++) {
            if (changeCol[j] != -1) {
                for (int i = 0; i < size; i++) {
                    if (matrix[i][j] < 0)
                        continue;
                    matrix[i][j] -= changeCol[j];
                }
            }
        }

        partSolution.add(col);
        HSolution solution = reduceMatrixSolver(matrix, size, col, newCol, upperbound, partSolution, lowerbound, start, time, output);
        partSolution.remove(partSolution.size()-1);

        // recover matrix
        for (int i = 0; i < size; i++) {
            if (changeRow[i] != -1) {
                for (int j = 0; j < size; j++) {
                    if (matrix[i][j] < 0)
                        continue;
                    matrix[i][j] += changeRow[i];
                }
            }
        }

        for (int j = 0; j < size; j++) {
            if (changeCol[j] != -1) {
                for (int i = 0; i < size; i++) {
                    if (matrix[i][j] < 0)
                        continue;
                    matrix[i][j] += changeCol[j];
                }
            }
        }


        for (int j = 0; j < size; j++) {
            matrix[j][col] = currentCol[j];
            matrix[row][j] = currentRow[j];
        }

        if (delete_item != -1) {
            for (int i = 0; i < size; i++) {
                matrix[col][i] = delete_iten_row[i];
            }
        }

        matrix[col][row] = delete_item;
        matrix[row][size] = 1;
        matrix[size][col] = 1;
        return solution;
    }



    // helper functions for debug
    private static void printMatrix(int matrix[][], int size) {
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                System.out.print(matrix[i][j]);
                System.out.print(" ");
            }
            System.out.println();
        }
    }

    private static void printPath(List<Integer> path) {
        for (Integer p : path)
            System.out.print(Integer.toString(p) + " ");
        System.out.println();
    }
}
