package com.company;
/**
 * UnionF.java
 * @author Jiaying He
 * Georgia Institute of Technology, Fall 2018
 *
 * This class is to represent connections of vertices in a graph. If two vertices are connected, they
 * will point to the same head.
 *
 */

public class UnionF {
    private int[] elements;
    private int[] depth;
    
    
    /**
     * Construct the UnionF. Connections are stored in elements and depth are stored in depth.
     */
    public UnionF(int size) {
        elements = new int[size];
        depth = new int[size];
        for(int i = 0; i < size; i++) {
            elements[i] = i;
            depth[i] = 0;
        }
    }
    
    /**
     * combine two elements by setting the head of the heap with smaller depth to the head of the
     * larger one
     */
    public void combine(int m, int n) {
        int mh = findhead(m);
        int nh = findhead(n);
        if (depth[mh] > depth[nh])
            sethead(nh, mh);
        else {
            sethead(mh, nh);
            if (depth[mh] == depth[nh])
                depth[nh]++;
        }
    }
    
    
    /**
     * If two elements are connected, they will eventually point the same head.
     *
     * Return true if two elements are connected.
     */
    public boolean connected(int m, int n) {
        return (findhead(m) == findhead(n));
    }
    
    
    /**
     * Return the head of element n
     */
    private int findhead(int n) {
        while (n != elements[n]) {
            n = elements[n];
        }
        return n;
    }
    
    
    /**
     * Combine two sets of elements by setting one of their head to the other
     */
    private void sethead(int m, int n) {
        elements[m] = n;
    }
}
