import sys
from random import seed, shuffle, sample
from scoringFunctions import distance_cost
from inputOutput import read_input_coordinates, adjmat_from_coordinates
from inputOutput import writeSolutionFile, writeAverageSolution,writeRunTimes

from simulatedAnnealer import solution_search,greedy_heuristic
from time import time, sleep

def main():
    '''
    The main execution. This function will parse input, read in graphs, perform simulated annealing
    and save relevant output files.
    '''

    # Parse the user input
    num_args = len(sys.argv)
    assert (num_args >= 2), "Error: Not enough input arguments"
    graph_filename = sys.argv[1]
    cut_off_time = float(sys.argv[2])

    if (num_args > 3):
        random_seed = float(sys.argv[3])
    else:
        # If no random seed provided, use 0.
        random_seed = 0

    if (num_args > 4):
        # Since we need average over runs, we might want to perform multiple runs.
        nruns = int(sys.argv[4])
        if nruns > 1:
            # NOTE: Not relevant for main project
            # I use this flag to compute average values that I used in some of my analysis.
            average = 'Y'
        else:
            average = 'N'
    else:
        # If argument missing, only run once
        nruns = 1
        average = 'N'

    # Read in the input files
    fileReadOut = read_input_coordinates(graph_filename)
    input_coords = fileReadOut[0:2]
    city_name = fileReadOut[2]

    # Get the adjacency matrix
    # This will also return maxL used for the Bonomi initial temp and neighborhood pruning
    adj_mat,maxL = adjmat_from_coordinates(coord_array= input_coords[0],
                                     coord_type = input_coords[1],
                                     mat_type = 'low_triag')


    # Create a dictionary of parameters
    # Instead of creating multiple parameters for all functions, we pass a single dictionary around that has
    # most of the parameters. Most of these are tunable parameters that can be optimized through experiments.
    paramDict = dict({'max_iteration': 10000, # NOTE: Not relevant to the project. Useful when you want to stop execution early
                      # WE DON NOT USE MAX ITERATION FOR STOPPING CRITERIA BY DEFAULT
                      'epsilon': 10e-10, # Temperatures below this will be considered zero.
                      'alpha': 0.95, #scaling factor for temperature drops. Higher means slower drops.
                      'initial_temp': 100, # Default initial temperature
                      'initial_temp_bonomi': True, #if true, use the Bonomi formula to compute initial temperature
                      'maxL': maxL, #the length of square that will fit the full city. Used for Bonomi parameters.
                      'best_max_iter': int(len(adj_mat)*0.5), #if no improvement in best sol over this many turns, revert back to best sol
                      'stop_max_iter': int(sys.float_info.max), # if no improvement in best sol over this many turns, stop searching
                      # NOTE: ^ Set to maximum possible value so it doesn't affect termination.
                      'nruns': nruns, # number of runs to do SA search
                      'seed': random_seed, # seed for random number generator
                      'instance': city_name, # for output file naming
                      'method':'LS2', # never changes
                      'cut_off_time': cut_off_time, # Algorithm will be allowed to run this many seconds
                      'max_eq_iter':100, # This many iterations per temperature i.e. iterations for equilibrium
                      'prune_dist': 5, # Scaling factor for pruning. must be > 1. Bigger means less pruning.
                       # ^ How big should pruned neighbood be ? w.r.t smallest edge
                      'prune_prob':0.1, #probability with which pruned neighbors are accepted
                      'do_prune': False, # Use neighborhood pruning?
                      'do_tabu': True,  # Implement a tabu list?
                      'max_tabu_length': int(0.05*len(adj_mat)), # Max length of tabu list
                      'greedy_start': True # Use greedy nearest neighbor algorithm to find initial solution?
                 })

    # Set random seed for reproducibility
    seed(paramDict['seed'])

    # Run simulated annealing multiple times and get averaged scores:
    costs = []
    sols = []
    run_times = []

    for run in range(1,paramDict['nruns']+1):

        print("\t\t\t[{}] - Round {}".format(paramDict['instance'],
                                       run))



        # Record start time
        start_time = time()
        end_time = start_time + cut_off_time
        paramDict['start_time'] = start_time
        paramDict['end_time'] = end_time

        # Initialize the solution
        if paramDict['greedy_start'] == True:
            init_sol = greedy_heuristic(adj_mat)  # greedy initial solution
        else:
            init_sol = sample(range(0,len(adj_mat)),len(adj_mat))  # random initial solution

        sol,run_time = solution_search(init_solution=init_sol,
                                       adj_mat=adj_mat,
                                       paramDict=paramDict,
                                       start_time = start_time,
                                       run = run)

        run_time = run_time - start_time

        sols.append(sol)
        costs.append(distance_cost(sol,adj_mat))
        run_times.append(run_time)


    # Find the best solution found over nruns
    min_cost, min_cost_id = min((val,idx) for (idx,val) in enumerate(costs))
    min_cost_sol = sols[min_cost_id] # pick solution with lowest cost

    solution_quality,solution = min_cost,min_cost_sol

    print("\nComplete!")
    print("Best Solution Quality: {}".format(solution_quality))
    print("Best Solution: {}".format(solution))

    ### Write the solution file
    # Set up the output file name
    writeSolutionFile(paramDict = paramDict,
                      solution_quality = solution_quality,
                      solution = solution)

    if average == 'Y':
        # If specified, write an average results file if computing average results
        writeAverageSolution(paramDict = paramDict,
                             final_sols = sols,
                             final_costs = costs,
                             final_run_times = run_times)

        writeRunTimes(paramDict,run_times,costs)


if __name__ == '__main__':
    # run the simulated annealer
    main()
