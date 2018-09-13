package com.company;

/**
 * FileIo.java
 * @author GuoYuzhi
 * Georgia Institute of Technology, Fall 2018
 *
 * Helper functions for reading graphs and writing data
 */

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;

public class FileIo {
    // Create a new graph object from a simple dimacs-like file
    public static Graph readGraph(String graphFile) throws IOException {
        // Open the file and read the number of vertices/edges
        BufferedReader br = new BufferedReader(new FileReader(graphFile));
        String firstLine = br.readLine();
        int numVertices = Integer.parseInt(firstLine.split(" ")[0]);
        int numEdges = Integer.parseInt(firstLine.split(" ")[1]);

        // Then, create the new graph with the appropriate size
        Graph g = new Graph(numVertices, numEdges);

        // Read through the file parsing edges, assuming that they are in order
        int cur = numVertices;
        for (int offset = 0; offset < numEdges; offset++) {
            String line = br.readLine();
            int src = Integer.parseInt(line.split(" ")[0]);
            int dst = Integer.parseInt(line.split(" ")[1]);
            // We need to set the offset appropriately
            while (cur != src) {
                if (cur == numVertices) {
                    cur = 0;
                } else {
                    cur++;
                }
                if (cur > src) {
                    System.out.print(src);
                    System.err.println("invalid edge error");
                    System.exit(1);
                }
                g.setRawOffset(cur, offset);
            }

            // Adds the edge in
            g.setRawEdge(offset, dst);
        }

        // Finish the final edge
        for (cur++; cur < numVertices; cur++) {
            g.setRawOffset(cur, numEdges);
        }

        br.close();
        return g;
    }

    // Create a vertex set object from a simple file with vids
    public static VertexSet readVertices(String verticesFile) throws IOException {
        // Open the file and read the number of elements in the set
        BufferedReader br = new BufferedReader(new FileReader(verticesFile));
        String firstLine = br.readLine();
        int numVertices = Integer.parseInt(firstLine);

        // Then, loop through and read/insert each into a new VertexSet
        VertexSet vertices = new VertexSet();
        for (int i = 0; i < numVertices; i++) {
            String line = br.readLine();
            int vertexId = Integer.parseInt(line);
            vertices.insertVertex(vertexId);
        }

        br.close();
        return vertices;
    }

    // Save vertex data in a graph to the given file
    public static void writeVerticesData(Graph g, String outputFile) throws IOException {
        PrintWriter output = new PrintWriter(outputFile, "UTF-8");
        output.println(g.getNumVertices());
        for (double vertexData : g.getVerticesData()) {
            output.println(vertexData);
        }
        output.close();
    }
}
