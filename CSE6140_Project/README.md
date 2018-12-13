# Group 7
## Sandesh Adhikary, Jiaying He, Xiao Yang, Xunchen Zhou



### The Code Files

We have implemented  four methods to solve the TSP. The branch and bound method, approximation method and the first local search method(Iterative local search) are implemented in Java and all located in the com.company package in the code folder. 

1. **_Main.java_**: The main file for the whole program. It calls method functions based on the algorithm required in input.
2. **_BNB.java_**: The branch and bound method.
3. **_MSTApprox.java_**: The MST approximation method.
4. **_ILS.java_**: The iterative local search method.
5. **_Graph.java_**: A class representing a graph.
6. **_FileIn.java_**: A helper class to read graph info from file, used in the local search method.
7. **_GraphReader.java_**: A helper class to read graph info from file, used in the branch and bound method and the approximation method.
8. **_solution.java_**: A helper class representing a solution, used in the local search method.
9. **_HSolution.java_**: A helper class representing a solution, used in the branch and bound method.
10. **_UnionF.java_**: A helper class representing unions of cities, used in the approximation method.
11. **_LocalSearch.java_**: A helper class do the hill climbing local search, used in the iterative local search method
12. **_Location.java_**: A class representing a location, used in reading the graph.
13. **_Perturbation.java_**: A helper class help the to escape the local optimal, used in the iterative local search

The second local search method(simulated annealing) is implemented in python and located in the /code/simulated_annealing folder. 

14. **_runAlgo.py_**: [Simulated Annealing] The main python file used that runs experiments.
15. **_inputOutput.py_**: [Simulated Annealing] Helper functions to load and write files.
16. **_simulatedAnnealer.py_**: [Simulated Annealing] Functions to perform annealing. Mostly contains search strategies including nearest neighbor.
17. **_solutionNeighborhoods.py_**: [Simulated Annealing] Functions that define neighborhoods and pick out new neighbors when called.
18. **_temperature_schedule.py_**: [Simulatd Annealing] Functions that define thetemperature schedule to use for annealing
19. **_scoring_functions.py_**: [Simualted Annealing] Functions to compute score (cost) of solutions.
20. **_solutionAcceptor.py_**: [Simulated Annealing] Functions to define criteria (Metropolis) to select "bad" neighbors



There's a bash file called 'tsp.sh' in the code folder which is the executable file.

21. **_tsp.sh_**: A bash file to analyze input and run the whole program.




### Execution

Directly run the following line in the terminal:

    ./tsp.sh -inst <filename> 
                -alg [BnB | Approx | LS1 | LS2]
                -time <cutoff_in_seconds>
                [-seed <random_seed>]
                
If this doesn't work, run the following line and then try to run the previous command again:
    
    chmod u+x tsp.sh
    
Make sure that the file path is included in <filename>.




### Output

After execute the program, all the output files can be found under the src file. For each instance there should be two output files, one '.sol' file containing the cost and path of the best solution and one '.trace' file containing the time whenever a new solution is found and its cost.

Our output files are in the output folder. We have the '.sol' and '.trace' file for each instance and each algorithm. For the local search algorithms, we only provide trace files for a single run (out of many). So the values may not exactly match those in the report.
