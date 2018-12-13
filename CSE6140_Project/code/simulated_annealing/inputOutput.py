import math
from time import time,sleep
import os
import sys

def read_input_coordinates(filename):
    """
    :param: file-path of .tsp data file
    :return: coord_array - The array of co-ordinates to use
    :return: coord_type - EUC_2D or GEO?
    :return: city_name - The instance name picked up from reading the file
    """

    with open(filename, 'r') as f:

        city_name = None
        coord_type = None

        # read initial descriptive text
        for line in f:
            if line.startswith("NAME"):
                city_name = line.strip().split(' ')[1]
                print('Reading File: {}'.format(city_name))
            elif line.startswith("EDGE_WEIGHT_TYPE"):
                coord_type = line.strip().split(': ')[1]
                print("Edge Weight Type: {}".format(coord_type))
            elif line.startswith("NODE_COORD_SECTION"):
                # reached the end of descriptive text
                print("Reading Co-ordinates...")
                break

        # check that we got all descriptions needed
        assert (city_name != None), "Error: No city name found"
        assert (coord_type != None), "Error: No coordinate type found"

        # read coordinates
        coord_array = []
        for line in f:
            if line.strip() != 'EOF':
                coord_array.append([float(x) for x in line.strip().split(' ')[1:]])
            else:
                break # end of the file
    return [coord_array, coord_type, city_name]


def adjmat_from_coordinates(coord_array, coord_type, mat_type = 'low_triag'):
    '''
    Returns an adjacency matrix.
    :param coord_array: array with coordinates for each city
    :param coord_type: 'EUC_2D' or 'GEO' format specifier
    : mat_type : 'full' for a full adjacency mat; 'low_triag' for low triangular
    matrix when distances are symmetric
    NOTE: THIS ONLY SUPPORTS low_triag right now
    :return: adjmat - The LOWER TRIANGULAR adjacency matrix
    :return: maxL - The Bonomi max length. The length of the square that covers all nodes
    '''

    if mat_type == 'full':
        # I had this function to allow for full adjacency matrix. Ended up not needing it so commenting it out.
        raise Exception("Can only do lower triangular adj matrices right now, not full matrices.")
        # Compute the full adjacency matrix
        # adjmat = []
        # for from_node in coord_array:
        #     from_array = []
        #     for to_node in coord_array:
        #         from_array.append(pair_dist(to_node, from_node, coord_type))
        #     adjmat.append(from_array)
        # return 1) adjmat 2) maxL value: the length of a box that the full city can fit in

    elif mat_type == 'low_triag':
        # Only compute lower triangle of adj matrix
        adjmat = []
        minx,miny =  [sys.float_info.max,sys.float_info.max]
        maxx,maxy = [0,0]

        for from_node_index,from_node in enumerate(coord_array):

            # Update min and mix values
            if from_node[0] < minx:
                minx = from_node[0]
            if from_node[0] > maxx:
                maxx = from_node[0]
            if from_node[1] < miny:
                miny = from_node[1]
            if from_node[1] > maxy:
                maxy = from_node[1]

            from_array = [-1.0]*(from_node_index+1)
            for to_node_index,to_node in enumerate(coord_array[0:from_node_index+1]):

                if coord_type == 'EUC_2D':

                    xd = from_node[0] - to_node[0]
                    yd = from_node[1] - to_node[1]
                    dist = round(math.sqrt( xd*xd + yd*yd ))
                    if dist < 0:
                        raise Exception("Negative distance found! Distance value not computed correctly.")
                    else:
                        from_array[to_node_index] = dist
                elif coord_type == 'GEO':

                    if from_node_index == to_node_index:
                        from_array[to_node_index] = 0
                    else:
                        from_lat = convertToLatLong(from_node[0])
                        from_long = convertToLatLong(from_node[1])
                        to_lat = convertToLatLong(to_node[0])
                        to_long = convertToLatLong(to_node[1])

                        RRR = 6378.388 # The radius of the earth
                        q1 = math.cos(from_long - to_long)
                        q2 = math.cos(from_lat - to_lat)
                        q3 = math.cos(from_lat + to_lat)
                        dist = int( RRR * math.acos( 0.5*((1.0+q1)*q2 - (1.0-q1)*q3) ) + 1.0)
                        if dist < 0:
                            raise Exception("Negative distance found! Distance value not computed correctly.")
                        else:
                            from_array[to_node_index] = dist

                else:
                    raise Exception("Unknown coordinate type. Could not compute adj matrix.")

            adjmat.append(from_array)

    else:
        raise Exception('Unrecognized adjacency matrix type!')

    # Compute the maxL value
    maxL = max(maxx - minx, maxy - miny)

    return [adjmat,maxL]



def writeSolutionFile(paramDict,
                      solution_quality,solution):
    '''
    :param: paramDict - dictionary with all the parameters. Check runAlgo for full details.
    :param: The solution quality to write
    :param: The solution to write
    '''

    instance = paramDict['instance']
    method = paramDict['method']
    cut_off_time = paramDict['cut_off_time']
    random_seed = paramDict['seed']

    filename = '{}_{}_{}_{}.sol'.format(instance,
                                    method,
                                    int(cut_off_time),
                                    int(random_seed))
    with open(filename,'wb') as f:
        # Write solution quality
        f.write(str(solution_quality).encode('ascii'))
        f.write('\n'.encode('ascii'))

        # list of vertices
        for idx,vertex in enumerate(solution):
            f.write(str(vertex).encode('ascii'))
            if idx < len(solution)-1:
                f.write(','.encode('ascii'))


def writeTraceFile(paramDict,best_solution_quality, run, new_file = False):
    '''
    :param paramDict: The dictionary with all parameters. Check runAlgo for full details.
    :param best_solution_quality: The best solution quality to write
    :param run: Which run is this? relevant when doing multipel runs for averaging
    :param new_file: # If True, we create a new file
    :return: no output, just writes to the trace file
    '''

    instance = paramDict['instance']
    method = paramDict['method']
    cut_off_time = paramDict['cut_off_time']
    random_seed = paramDict['seed']
    start_time = paramDict['start_time']
    nruns = paramDict['nruns']

    filename = '{}_{}_{}_{}_run{}of{}.trace'.format(instance,
                                                    method,
                                                    int(cut_off_time),
                                                    int(random_seed),
                                                    int(run),
                                                    int(nruns))

    if new_file == True:
        # if file does not exist, create a new one and write to it
        with open(filename,'wb') as f:
            f.write("{:.3f}, {}\n".format(time() - start_time, best_solution_quality).encode('ascii'))
    else:
        if os.path.isfile(filename):
            # if file exists, append to it
            with open(filename,'ab') as f:
                f.write("{:.3f}, {}\n".format(time() - start_time,best_solution_quality).encode('ascii'))
        else:
            raise Exception("Trace file does not exist. Check initial file creation.")
            # Creating exception instead of starting a new file to keep track of issues


def writeAverageSolution(paramDict, final_sols, final_costs,final_run_times):
    '''
    Write a file with solutions averaged over trials
    :param paramDict: The dictionary with all parameters. Check runAlgo for full details
    :param final_sols: The list of final solutions from all runs
    :param final_costs: The list of final costs from all runs
    :return: No return, just write to file
    Writes a file in the following format:
    line 1: <nruns>,<avg sol quality>, <std dev of sol qualities>
    '''

    instance = paramDict['instance']
    method = paramDict['method']
    cut_off_time = paramDict['cut_off_time']
    random_seed = paramDict['seed']
    nruns = paramDict['nruns']

    filename = '{}_{}_{}_{}_{}runs.avg'.format(instance,
                                               method,
                                               int(cut_off_time),
                                               int(random_seed),
                                               int(nruns))

    quality_mean, quality_std = mean_std(final_costs)
    run_time_mean,run_time_std = mean_std(final_run_times)

    with open(filename,'wb') as f:
        #record average qualities
        f.write("{},{:.2f},{:.2f},{:.2f},{:.2f}".format(nruns,
                                                        quality_mean,
                                                        quality_std,
                                                        run_time_mean,
                                                        run_time_std).encode('ascii'))

def writeRunTimes(paramDict,run_times,costs):
    '''
    Helped function I used to write run times. Not relevant for project.
    :param paramDict:
    :param run_times:
    :param costs:
    :return:
    '''

    instance = paramDict['instance']
    method = paramDict['method']
    cut_off_time = paramDict['cut_off_time']
    random_seed = paramDict['seed']
    nruns = paramDict['nruns']

    filename = '{}_{}_{}_{}_{}runs.runtimes'.format(instance,
                                               method,
                                               int(cut_off_time),
                                               int(random_seed),
                                               int(nruns))

    with open(filename,'wb') as f:
        for iter,runCost in enumerate(zip(run_times,costs)):
            f.write('{},{},{}'.format(iter,runCost[0],runCost[1]).encode("ascii"))

            if iter < len(run_times) - 1:
                f.write('\n'.encode('ascii'))


def mean_std(list):
    '''
    Compute means and standard deviations when needed
    :param list: a list of floats
    :return: [mean, std]
    '''
    mean = sum(list)/len(list)

    sd = 0
    for x in list:
        sd += abs(x - mean)**2
    sd = (sd/len(list))**(0.5)

    return(mean,sd)

def convertToLatLong(x):
    '''
    Function to convert GEO file coordinates to lat/long in radians
    :param coordinate: the coordinate value to convert
    :return: the corresponding lat/long in radians
    '''

    pi = 3.141592

    if x < 0:
        deg = math.floor(-x)
        deg = -deg
    else:
        deg = math.floor(x)

    min = x - deg
    rad = pi * (deg + 5.0 * min /3.0) / 180.0

    return rad