package com.company;
import java.util.ArrayList;
import java.util.List;

public class HSolution {
    private int distance;
    private List<Integer> paths;

    HSolution(int distance, List<Integer> paths) {
        this.distance = distance;
        this.paths = paths;
    }

    public int getDistance() {
        return distance;
    }

    public List<Integer> getPaths() {
        return new ArrayList<>(paths);
    }

    public void showSolution(){
        for(int i = 0; i < paths.size(); i++){
            if(i == paths.size()-1){
                System.out.println(paths.get(i) + 1);
            }else{
                System.out.printf("%d, ",paths.get(i) + 1);
            }
        }
        System.out.printf("The total cost of this path is %d\n", distance);
    }

    public void setDistance(int distance) {
        this.distance = distance;
    }

    public void setPaths(List<Integer> paths) {
        this.paths = paths;
    }
}
