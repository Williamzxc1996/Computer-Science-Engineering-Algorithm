package com.company;
import org.omg.CORBA.INTERNAL;

import java.io.PrintWriter;
import java.util.*;

public class MSTApprox {
    private static class Edge {
        int end1;
        int end2;
        int weight;

        Edge(int end1, int end2, int weight) {
            this.end1 = end1;
            this.end2 = end2;
            this.weight = weight;
        }
    }


    public static void solve(String graphFile, int time) throws java.io.IOException {
        int instance_start = graphFile.lastIndexOf("/") + 1;
        String instance = graphFile.substring(instance_start, graphFile.length() - 4);
        String outputFile = "./" + instance + "_Approx_" + Integer.toString(time) + ".sol";
        String traceFile = "./" + instance + "_Approx_" + Integer.toString(time) + ".trace";
        Graph graph = GraphReader.readGraph(graphFile);
        PrintWriter output = new PrintWriter(traceFile, "UTF-8");
        PrintWriter sol_output = new PrintWriter(outputFile, "UTF-8");
        long start = System.currentTimeMillis();
        int size = graph.getSize();
        time = time * 1000;
        Graph mst = new Graph(size);

        Comparator<Edge> comparator = new Comparator<Edge>() {
            @Override
            public int compare(Edge o1, Edge o2) {
                return o1.weight - o2.weight;
            }
        };

        // Collect info of all edges and sort them by weight
        PriorityQueue<Edge> pq = new PriorityQueue<>(size * (size - 1) / 2, comparator);
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < i; j++)
                pq.add(new Edge(i, j, graph.getDistance(i, j)));
        }

        // Pick the edge with minimum weight that won't complete a circle
        UnionF uf = new UnionF(size);
        for (int i = 0; i < (size - 1); i++) {
            Edge edge = pq.poll();
            while (uf.connected(edge.end1, edge.end2)) {
                edge = pq.poll();
            }
            mst.setDistance(edge.end1, edge.end2, edge.weight);
            mst.setDistance(edge.end2, edge.end1, edge.weight);
            uf.combine(edge.end1, edge.end2);
        }


        List<Integer> path = null;
        int distance = Integer.MAX_VALUE;
        List<Integer> newPath;
        int newDistance;
        Set<Integer> visited = new HashSet<>();


        for (int i = 0; i < size; i++) {
            newPath = new ArrayList<>();
            visited.clear();
            newPath.add(i);
            visited.add(i);
            getPath1(mst, size, i, newPath, visited);
            newPath.add(i);
            newDistance = 0;
            for (int j = 0; j < size; j++) {
                newDistance += graph.getDistance(newPath.get(j), newPath.get(j+1));
            }
            if (newDistance < distance) {
                distance = newDistance;
                path = newPath;
                double running_time = 1. * (System.currentTimeMillis() - start) / 1000;
                output.println(Double.toString( running_time) + ", " + Integer.toString(distance));
            }
            if (System.currentTimeMillis() - start > time)
                break;
            newPath = new ArrayList<>();
            visited.clear();
            newPath.add(i);
            visited.add(i);
            getPath2(mst, size, i, newPath, visited);
            newPath.add(i);
            newDistance = 0;
            for (int j = 0; j < size; j++) {
                newDistance += graph.getDistance(newPath.get(j), newPath.get(j+1));
            }
            if (newDistance < distance) {
                distance = newDistance;
                path = newPath;
                double running_time = 1. * (System.currentTimeMillis() - start) / 1000;
                output.println(Double.toString( running_time) + ", " + Integer.toString(distance));
            }
            if (System.currentTimeMillis() - start > time)
                break;
        }

        System.out.println("The solution found by Approx:");
        for(int i = 0; i < path.size(); i++){
            if(i == path.size()-1){
                System.out.println(path.get(i) + 1);
            }else{
                System.out.printf("%d, ",path.get(i) + 1);
            }
        }
        System.out.printf("The total cost of this path is %d\n", distance);

        output.close();


        sol_output.println(distance);
        for (int i = 0; i < path.size() - 2; i++) {
            sol_output.print(Integer.toString(path.get(i) + 1) + ", ");
        }
        sol_output.println(path.get(path.size() - 2) + 1);
        sol_output.close();
    }

    private static void printPath(List<Integer> path) {
        for (Integer p : path)
            System.out.print(Integer.toString(p) + " ");
        System.out.println();
    }

    private static void getPath1(Graph graph, int size, int pos, List<Integer> path, Set<Integer> visited) {
        for (int i = size - 1; i >= 0; i--) {
            if ((!visited.contains(i)) && graph.getDistance(pos, i) != -1) {
                path.add(i);
                visited.add(i);
                //graph.getDistance(pos, i);
                getPath1(graph, size, i, path, visited);
            }
        }
    }

    private static void getPath2(Graph graph, int size, int pos, List<Integer> path, Set<Integer> visited) {
        for (int i = 0; i < size; i++) {
            if ((!visited.contains(i)) && graph.getDistance(pos, i) != -1) {
                path.add(i);
                visited.add(i);
                //graph.getDistance(pos, i);
                getPath2(graph, size, i, path, visited);
            }
        }
    }
}
