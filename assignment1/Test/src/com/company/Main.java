package com.company;

/**
 * Main.java
 * @author GuoYuzhi
 * Georgia Institute of Technology, Fall 2018
 *
 * The main entry point for computing HC centrality
 */

import java.io.IOException;

public class Main {
    public static void main(String[] args) throws IOException {
        if (args.length != 3) {
            System.err.println("Usage: <input-graph-file> <heist-nodes-file> <output-file>");
            System.exit(1);
        }

        String graphFilePath = args[0];
        String heistNodesFilePath = args[1];
        String outputFilePath = args[2];

        // Open the input and use it to create the graph
        Graph g = FileIo.readGraph(graphFilePath);

        // Load the vertex set h
        VertexSet h = FileIo.readVertices(heistNodesFilePath);


        long start=System.currentTimeMillis();   //获取开始时间

        // Compute the HC centrality for each vertex
        HCC.compute(g, h);

        long end=System.currentTimeMillis(); //获取结束时间
        System.out.println("time： "+(end-start)+"ms");

        // Write out the HC centrality to a file
        FileIo.writeVerticesData(g, outputFilePath);
    }
}