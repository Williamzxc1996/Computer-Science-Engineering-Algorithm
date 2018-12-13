from scoringFunctions import distance_cost
from solutionNeighborhoods import two_exchange_random, two_exchange_random_pruned
from solutionAcceptor import accept_new_solution
from temperatureScheduler import temperature_schedule
from time import time,sleep
from inputOutput import writeTraceFile
import sys
from random import randint,sample
from math import sqrt,ceil

def solution_search(init_solution,
                    adj_mat,
                    paramDict,
                    start_time,
                    run):
    '''The main solution search function to peform simulated annealing.
    :param: init_solution - The initial solution
    :param: adj_mat - The LOWER TRIANGULAR adjacency matrix
    :param: paramDict - The parameter dict with all tunable parameters. Check runAlgo for full list of parameters.
    :param: start_time - Time when algorithm started running
    :param: run - Which run number is it? Relevant when doing multiple runs for averaging.
    :return: best_sol - The best solution found in the search
    :return: search stop time - time when this solution search ended
    '''
    current_sol, current_cost = init_solution, distance_cost(init_solution, adj_mat)
    best_sol, best_cost = init_solution, current_cost # Set to highest possible cost

    # Create a tabu_list
    tabu_list = []

    # Write initial result to trace file
    writeTraceFile(paramDict,
                   best_solution_quality= best_cost,
                   new_file = True,
                   run = run)


    # Retrieve the tunable parameters
    # check runAlgo for full descriptions of all paramters
    epsilon = paramDict['epsilon'] # when temperature should be considered 0
    alpha = paramDict['alpha'] # scaling factor for temperature drops. Higher means slower drops.
    best_max_iter = paramDict['best_max_iter']
    # If no changes to best sol within this many iterations, change revert current sol to last best sol
    stop_max_iter = paramDict['stop_max_iter']
    current_temp = paramDict['initial_temp'] # Initial temperature
    max_iteration = paramDict['max_iteration']
    end_time = paramDict['end_time']
    instance = paramDict['instance']
    nruns = paramDict['nruns']
    max_eq_iter = paramDict['max_eq_iter']
    max_tabu_length = paramDict['max_tabu_length']
    prune_dist = paramDict['prune_dist']
    prune_prob = paramDict['prune_prob']
    do_tabu = paramDict['do_tabu']
    do_prune = paramDict['do_prune']
    initial_temp_bonomi = paramDict['initial_temp_bonomi']
    maxL = paramDict['maxL']

    # Compute box_length which we will need if we are pruning
    box_length = prune_dist * maxL / (sqrt(2))
    # ^ This is the size of the cell within the big Bonomi box that surrounds the nodes
    # We will restrict edges to be within 2 of these boxes

    # If initial_temp_bonomi, use the bonomi formula to calculate initial temperature
    if initial_temp_bonomi == True:
        current_temp = ceil(maxL / sqrt(len(adj_mat)))


    best_counter = 0 # Counter for iterations since last best update
    iteration = 0
    while time() <= end_time:
        iteration += 1
        print("[{}] [run {}/{}] [Time: {:.2f}] [SA {}/{}] Temp: {:.1f}, Cost:{}, Best: {}, BestCounter: {} [len: {}] ".format(instance,
                                                                                                                              run,
                                                                                                                              nruns,
                                                                                                                              time() - start_time,
                                                                                                                              iteration,
                                                                                                                              max_iteration,
                                                                                                                              current_temp,
                                                                                                                              current_cost,
                                                                                                                              best_cost,
                                                                                                                              best_counter,
                                                                                                                              len(current_sol)))

        # Drop the temperature
        current_temp = temperature_schedule(time = iteration,
                                                temp = current_temp,
                                                epsilon = epsilon,
                                                alpha = alpha,
                                                strategy='geometricSeries')

        if current_temp > 0:
            # Not frozen yet
            eq_counter = 0 # counter for number of iterations per temperature
            equilibrium = False
            while equilibrium == False:
                # We consider equilibrium once we eq_counter reaches max_eq_iter
                eq_counter += 1
                if do_prune == False:
                    # No Bonomi pruning
                    new_sol,tabu_list = two_exchange_random(current_sol,tabu_list,max_tabu_length,do_tabu)
                else:
                    # Yes Bonomi pruning
                    new_sol = two_exchange_random_pruned(current_sol,adj_mat,
                                                         box_length,prune_prob)

                # Change in enegry: Difference in solution qualities
                deltaE = distance_cost(new_sol,adj_mat) - \
                         distance_cost(current_sol,adj_mat)

                if deltaE <= 0:
                    # Downhill Move: Found a solution with lower cost
                    current_sol = new_sol
                    current_cost = distance_cost(current_sol, adj_mat)
                    if current_cost < best_cost:
                        # Update the best solution
                        best_sol = current_sol
                        best_cost = current_cost
                        best_counter = 0 # Refresh the iterations since last improvement
                        #Update trace file with better solution
                        writeTraceFile(paramDict = paramDict,
                                       best_solution_quality = best_cost,
                                       run = run)

                else:
                    # Uphill Move: Maybe move to a worse solution with higher cost
                    if accept_new_solution(deltaE,current_temp,strategy = 'exp') == True:
                        current_sol = new_sol

                if eq_counter > max_eq_iter:
                    equilibrium = True

            # If there have been no improvements in best_cost for in many temperature drops,
            # move back to last best solution.
            if best_counter > best_max_iter:
                # Go back to the last best solution, and restart search from there
                current_sol = best_sol
                current_cost = best_cost
                best_counter = 0

            # Uncomment if you want to terminate after a number of iterations
            if best_counter > stop_max_iter:
                break

        elif current_temp == 0:
            # We're Frozen!
            # run naive greedy strategy for remaining iterations
            greedy_local_search(solution = current_sol,
                          adj_mat = adj_mat,
                          best_sol_input = best_sol,
                          paramDict = paramDict,
                          run = run,
                                tabu_list = tabu_list)
            break
        else:
            raise Exception("Negative temperature encountered.")


        # Update best_counter
        best_counter += 1
        best_cost = distance_cost(best_sol, adj_mat)

    return [best_sol,time()]


def greedy_local_search(solution, adj_mat,
                  best_sol_input,
                  paramDict,run,tabu_list):

    '''
    Greedy local search to be used after simulated annealing freezes (T = 0)
    :param solution: the solution prior to calling greedy local search. This is its initial solution
    :param adj_mat: The LOWER TRIANGULAR adjacency matrix of the city
    :param best_sol_input: The best solution thus far
    :param paramDict: parameter dictionary. Check readAlgo for all entires.
    :param run: Which run is this? Relevant when computing average values across multiple runs.
    :param tabu_list: The tabu_list to use. Will be empty if no tabu.
    :return: search stop time - time when this solution search ended
    '''

    # Get all parameters from parameterDict
    start_time = paramDict['start_time']
    end_time = paramDict['end_time']
    max_iteration = paramDict['max_iteration']
    stop_max_iter = paramDict['stop_max_iter']
    instance = paramDict['instance']
    nruns = paramDict['nruns']
    end_time = paramDict['end_time']
    best_max_iter = paramDict['best_max_iter']
    max_tabu_length = paramDict['max_tabu_length']

    # Set current and best solutions
    current_sol,current_cost = solution, distance_cost(solution,adj_mat)
    best_sol,best_cost = best_sol_input, distance_cost(best_sol_input,adj_mat)

    # Similar to solution_search
    best_counter = 0
    iteration = 0
    while time() <= end_time:
        iteration += 1
        print("[{}] [run {}/{}] [Time: {:.2f}] [Greedy {}/{}] Temp: 0, Cost:{}, Best: {}, BestCounter: {}, len: {}]".format(instance,
                                                                                                     run,
                                                                                                     nruns,
                                                                                                     time() - start_time,
                                                                                                     iteration,
                                                                                                     max_iteration,
                                                                                                     current_cost,
                                                                                                     best_cost,
                                                                                                     best_counter,
                                                                                                                            len(current_sol)))

        best_counter += 1

        # Perform two exchange: No neighborhood pruning here.
        new_sol,tabu_list = two_exchange_random(current_sol,tabu_list,max_tabu_length)
        new_cost = distance_cost(new_sol,adj_mat)
        current_cost = distance_cost(current_sol,adj_mat)

        # Difference in solution quality
        deltaE = new_cost \
                 - current_cost

        if deltaE <= 0:
            # Downhill (good) move!
            current_sol = new_sol # update to better solution


        if current_cost < best_cost:
            # Keep track of the best solution thus far
            best_sol = current_sol
            best_cost = distance_cost(best_sol,adj_mat)
            best_counter = 0
            writeTraceFile(paramDict=paramDict,
                           best_solution_quality=best_cost,
                           run = run) # Update trace file with better solution

        # # If there have been no improvements in best_cost for a while
        # # move back to last best solution. If it has been too long
        # # end search
        if best_counter > best_max_iter:
            # Go back to the last best solution, and restart search from there
            current_sol = best_sol
            current_cost = best_cost
            best_counter = 0

        # Uncomment if you want to terminate after a number of iterations
        # if best_counter > stop_max_iter:
        #     break

    return [best_sol,time()]

def greedy_heuristic(adj_mat):
    '''
    Performs a greedy nearest neighbor search.
    We pick a random node, and add the closest node. Keep doing this until no nodes remain.
    :param adj_mat: The LOWER TRIANGULAR adjacency matrix of the city
    :return: The final tour found through this heuristic
    '''

    # Need the full adjacency matrix for this part. So build that matrix.
    numCities = len(adj_mat)
    adj_mat_full = [[-1 for i in range(numCities)] for j in range(numCities)]
    for row in range(numCities):
        for col in range(numCities):
            if row >= col:
                adj_mat_full[row][col] = adj_mat[row][col]
            else:
                adj_mat_full[row][col] = adj_mat[col][row]


    # Start with a random node
    tour = [randint(0,numCities-1)]
    tour_cost = 0 # no paths yet

    while len(tour) < numCities:

        # Find the neighbors of last city added to tour
        last_city_added = tour[len(tour)-1]
        neighbors = adj_mat_full[last_city_added]

        # Don't consider the cities already in the tour
        for tourCity in tour:
            neighbors[tourCity] = int(sys.float_info.max) # Set to infinity

        # Get the nearest neighbor
        min_cost, min_cost_neighbor = min((val,idx) for (idx,val) in enumerate(neighbors))

        tour_cost += min_cost
        tour.append(min_cost_neighbor) # Add the city to the tour

    return tour
