package com.company;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;


public class Graph {
    private final int nodeNumber;
    private int distances[][];

    public Graph(int nodeNumber) {
        this.nodeNumber = nodeNumber;
        this.distances = new int[nodeNumber][nodeNumber];
        for (int i = 0; i < nodeNumber; i++)
            for (int j = 0; j < nodeNumber; j++)
                this.distances[i][j] = -1;
    }

    public Graph(int nodeNumber, int distances[][]) {
        this.nodeNumber = nodeNumber;
        this.distances = new int[nodeNumber][nodeNumber];
        for (int i = 0; i < nodeNumber; i++) {
            for (int j = 0; j < nodeNumber; j++)
                this.distances[i][j] = distances[i][j];
        }
        //printGraph();
    }

    public Set<Integer> getNodes() {
        Set<Integer> nodes = new HashSet<>();
        for (int i = 0; i < nodeNumber; i++)
            nodes.add(i);
        return nodes;
    }

    public int getDistance(Integer node1, Integer node2) {
        return this.distances[node1][node2];
    }

    public void setDistance(int end1, int end2, int distance) {
        this.distances[end1][end2] = distance;
    }


    public void printGraph() {
        System.out.println("print graph:");
        for (int i = 0; i < nodeNumber; i++) {
            for (int j = 0; j < nodeNumber; j++) {
                System.out.format("%7d", distances[i][j]);
                System.out.print(" ");
            }
            System.out.println();
            //break;
        }
        System.out.println("end of graph");
    }

    public int getSize() {
        return nodeNumber;
    }
}
