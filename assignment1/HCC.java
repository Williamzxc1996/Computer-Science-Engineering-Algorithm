package com.company;
import java.util.LinkedList;

/**
 * HCC.java
 * @ZHOU XUNCHENG
 * Georgia Institute of Technology, Fall 2018
 *
 * Heist-Closeness Centrality computation implementation
 *
 * NOTE: You should change this file to add in your implementation.
 * Feel free to create as many local functions as you want.
 */

public class HCC {
    public static void compute(Graph g, VertexSet h) {
        // <Implement HCC here>
        int vNum = g.getNumVertices(), eNum = g.getNumEdges();
        int[] offsets = g.getRawOffsets(), edges = g.getRawEdges();
        for(int v = 0; v < vNum; v++){
            //use BFS to find the distance from v to every other vertex in the graph
            double[] dis = new double[vNum];
            boolean[] visited = new boolean[vNum];
            LinkedList<Integer> q = new LinkedList<>();
            q.add(v);
            visited[v] = true;
            while(!q.isEmpty()){
                int temp = q.poll(), start = offsets[temp], count = g.degree(temp);
                if(count != 0) {
                    for (int i = start; i < start + count; i++) {
                        if (!visited[edges[i]]) {
                            visited[edges[i]] = true;
                            q.add(edges[i]);
                            dis[edges[i]] = dis[temp] + 1.0;
                        }
                    }
                }
            }
            double res = 0;
            for(int j = 0; j < vNum; j++){
                if(j != v && h.searchVertex(j)){
                    res += dis[j];
                }
            }
            g.setVertexData(v,1/res);
        }
    }
}