package com.company;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.*;


public class FileIO {
    public static int[][] readGraph_AdjMatrix(String graphFile)throws IOException{
        // Open the file and read the number of vertices/edges
        BufferedReader br = new BufferedReader(new FileReader(graphFile));

        // Read graph info
        Map<String, String> graphInfo = new HashMap<>();
        String nextLine = br.readLine();
        String contents[];
        while (nextLine.contains(":")) {
            contents = nextLine.split(":");
            contents[0] = contents[0].replaceAll(" ", "");
            if(contents[0].equals("Name")) System.out.print(1);
            graphInfo.put(contents[0], contents[1]);
            nextLine = br.readLine();
        }
        int size = Integer.parseInt(graphInfo.get("DIMENSION").replaceAll(" ", ""));
        String type = graphInfo.get("EDGE_WEIGHT_TYPE");
        type = type.replaceAll(" ", "");
        double x[] = new double[size];
        double y[] = new double[size];


        if (nextLine.equals("NODE_COORD_SECTION")) {
            for (int i = 0; i < size; i++) {
                nextLine = br.readLine();
                contents = nextLine.split(" ");
                int j = 0;
                while (!contents[j].equals(Integer.toString(i + 1)))
                    j++;
                x[i] = Double.parseDouble(contents[j+1]);
                y[i] = Double.parseDouble(contents[j+2]);
                //System.out.println(i);
                //System.out.println(contents[j+1] + " " + contents[j+2]);
                //System.out.println(Double.toString(x[i]) + " " + Double.toString(y[i]));
            }
        }

        int distances[][] = new int[size][size];

        if (type.equals("EUC_2D"))
            computeEUCDistances(x, y, size, distances);
        else
            computeGEODistances(x, y, size, distances);

        br.close();


        return distances;
    }


    private static void computeEUCDistances(double x[], double y[], int size, int distances[][]) {
        for (int i = 0; i < size; i++) {
            distances[i][i] = 0;
            for (int j= 0; j < i; j++) {
                //System.out.println("x: " + Double.toString(x[i]) + " " + Double.toString(x[j]));
                //System.out.println("y: " + Double.toString(y[i]) + " " + Double.toString(y[j]));
                distances[i][j] = (int)Math.round(Math.sqrt(Math.pow((x[j]-x[i]),2) + Math.pow((y[i]-y[j]),2)));
                distances[j][i] = distances[i][j];
                //System.out.println(distances[i][j]);
            }
        }
    }

    private static void computeGEODistances(double latitude[], double longitude[], int size, int distances[][]) {
        for (int i = 0; i < size; i++) {
            int deg = (int)(latitude[i]);
            double min = latitude[i] - deg;
            latitude[i] = 3.1415926 * (deg + 5.0 * min / 3.0) / 180.0;
            deg = (int)(longitude[i]);
            min = longitude[i] - deg;
            longitude[i] = 3.1415926 * (deg + 5.0 * min / 3.0) / 180.0;
        }

        for (int i = 0; i < size; i++) {
            distances[i][i] = 0;
            for (int j= 0; j < i; j++) {
                double q1 = Math.cos(longitude[i] - longitude[j]);
                double q2 = Math.cos(latitude[i] - latitude[j]);
                double q3 = Math.cos(latitude[i] + latitude[j]);
                //distances[i][j] = (int)Math.round(6378.388 * Math.acos(0.5*((1.0+q1)*q2 - (1.0-q1)*q3)) + 1.0);
                distances[i][j] = (int)(6378.388 * Math.acos(0.5*((1.0+q1)*q2 - (1.0-q1)*q3)) + 1.0);
                distances[j][i] = distances[i][j];
                //System.out.println(distances[i][j]);
            }
        }
    }
}