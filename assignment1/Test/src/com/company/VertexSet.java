package com.company;

/**
 * VertexSet.java
 * @author GuoYuzhi
 * Georgia Institute of Technology, Fall 2018
 *
 * A VertexSet object
 */

import java.util.HashSet;
import java.util.Set;

public class VertexSet {
    private Set<Integer> vertices;

    public VertexSet() {
        this.vertices = new HashSet<>();
    }

    public boolean insertVertex(int vertexId) {
        return vertices.add(vertexId);
    }

    public boolean searchVertex(int vertexId) {
        return vertices.contains(vertexId);
    }

    public void print() {
        System.out.println("VertexSet: k = " + vertices.size());
        for (int vertex : vertices) {
            System.out.print(vertex + " ");
        }
        System.out.print("\n");
    }
}
