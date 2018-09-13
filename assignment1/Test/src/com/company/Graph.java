package com.company;

/**
 * Graph.java
 * @author GuoYuzhi
 * Georgia Institute of Technology, Fall 2018
 *
 * A simple CSR based graph object
 */

public class Graph {
    // Keep track of the graph size
    private int numVertices;
    private int numEdges;

    // Store the graph as edges and offsets into edges
    private int[] edges;
    private int[] offsets;

    // Include an array for vertices data
    private double[] verticesData;

    public Graph(int numVertices, int numEdges) {
        this.numVertices = numVertices;
        this.numEdges = numEdges;
        this.edges = new int[numEdges];
        this.offsets = new int[numVertices];
        this.verticesData = new double[numVertices];
    }

    public int getNumVertices() {
        return this.numVertices;
    }

    public int getNumEdges() {
        return this.numEdges;
    }

    public int[] getRawEdges() {
        return this.edges;
    }

    public int[] getRawOffsets() {
        return this.offsets;
    }

    public double[] getVerticesData() {
        return this.verticesData;
    }

    public void setRawEdge(int index, int edge) {
        this.edges[index] = edge;
    }

    public void setRawOffset(int index, int offset) {
        this.offsets[index] = offset;
    }

    public void setVertexData(int index, double data) {
        this.verticesData[index] = data;
    }

    public int degree(int vertexId) {
        // Degree is either: the difference between the next element's pointer, or the difference between the last
        // element and the total number of edges.
        if (vertexId == numVertices - 1) {
            return numEdges - offsets[vertexId];
        }
        else {
            return offsets[vertexId + 1] - offsets[vertexId];
        }
    }

    public void print() {
        System.out.println("Graph: number of vertices = " + numVertices + ", number of edges = " + numEdges);

        System.out.print("Offsets: ");
        for (int offset : offsets) {
            System.out.print(offset + " ");
        }
        System.out.print("\n");

        System.out.print("Edges: ");
        for (int edge : edges) {
            System.out.print(edge + " ");
        }
        System.out.print("\n");

        System.out.println("Graph Structure: ");
        for (int v = 0; v < numVertices; v++) {
            System.out.print(v + "->");
            for (int offset = offsets[v]; offset < offsets[v] + degree(v); offset++) {
                System.out.print(edges[offset] + " ");
            }
            System.out.print("\n");
        }
    }
}