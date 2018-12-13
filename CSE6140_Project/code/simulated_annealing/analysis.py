import sys
from time import sleep

def main():

    # # Parse the user input
    # num_args = len(sys.argv)
    # assert (num_args >= 6), "Error: Not enough input arguments"
    # instance = sys.argv[1]
    # method = sys.argv[2]
    # cut_off_time = int(sys.argv[3])
    # seed = int(sys.argv[4])
    # nruns = int(sys.argv[5])

    # instances = ["Atlanta","berlin52","Boston","Champaign","Cincinnati","Denver",
    #           "NYC","Philadelphia","Roanoke","SanFrancisco",
    #           "Toronto","UKansasState","UMissouri"]

    instances = ["Cincinnati","Atlanta","Champaign","Toronto","Roanoke"]

    methods = ['LS2']
    cut_off_times = [10]
    seeds = [0]
    nruns = [10]

    ## Get the comprehensive table results
    outputFile = 'comprehensiveTable_baseline.csv'
    comprehensiveTable(outputFile,instances,methods,cut_off_times,seeds,nruns)

    # Get the QRD Plot results
    # qual_percents = [1,0.8,0.6,0.4,0.2,0]

    # qrd_plot(instances,methods,cut_off_times,
    #          seeds,nruns,qual_percents)

def comprehensiveTable(outputFile,
                       instances,
                       methods,
                       cut_off_times,
                       seeds,
                       nruns):


    with open(outputFile,'wb') as f:
        columns = ['instance', 'method', 'cut_off_time', 'seed', 'nruns','qual_avg','qual_std','runtime_avg','runtime_std']
        for iter,col in enumerate(columns):
            f.write(col.encode('ascii'))

            if iter < len(columns) - 1 :
                f.write(','.encode('ascii'))

    print()
    for instance in instances:
        for method in methods:
            for cut_off_time in cut_off_times:
                for seed in seeds:
                    for nrun in nruns:
                        filename = '{}_{}_{}_{}_{}runs.avg'.format(instance,
                                                                   method,
                                                                   cut_off_time,
                                                                   seed,
                                                                   nrun)

                        with open(filename,'r') as f:
                            n, avg, std, runtime, runstd = f.readline().strip().split(',')

                        with open(outputFile, 'ab') as f:
                            f.write('\n{},{},{},{},{},{},{},{},{}'.format(instance,method,
                                                                        cut_off_time,seed,
                                                                        n,avg,std,runtime,runstd).encode('ascii'))

                        print(instance,method,cut_off_time,seed,n,avg,std,runtime,runstd)

# def qrd_plot(instances,methods,
#              cut_off_times,seeds,nruns,
#              qual_percents):
#     '''
#     :param instances:
#     :param methods:
#     :param cut_off_times:
#     :param seeds:
#     :param nruns:
#     :param qual_percent: The percent closeness to optimal that we want
#     :return:
#     '''
#     for instance in instances:
#         for method in methods:
#             for cut_off_time in cut_off_times:
#                 for seed in seeds:
#                     qrdDataTable = []
#                     for nrun in nruns:
#
#                         # Get the best known solution for this instance thus far
#                         best_known_quality = getBestKnownQuality(instance)
#
#                         # load the runtimes file
#                         filename = '{}_{}_{}_{}_{}runs.runtimes'.format(instance,
#                                                                    method,
#                                                                    cut_off_time,
#                                                                    seed,
#                                                                    nrun)
#                         with open(filename,'r') as f:
#                             for line in f:
#                                 loadedLine = f.readline().strip().split(',')
#                                 if len(loadedLine) > 1:
#                                     qrdDataTable =
#


def getBestKnownQuality(instance):
    '''
    Retrieve the best known solution for the instance thus far from our file
    :param instance:
    :return:
    '''
    filename = 'bestResults.txt'
    best_known_result = -100 # If this stays negative, we know we didn't find it
    with open(filename,'r') as f:
        for line in f:
            # print(line)
            # sleep(1)
            resultLine = list(line.strip().split(','))
            # print(resultLine[0])
            # print(instance)
            # sleep(2)
            if resultLine[0] == instance:
                best_known_result = resultLine[1]
                break

    return best_known_result


if __name__ == '__main__':
    # run the analysis
    main()