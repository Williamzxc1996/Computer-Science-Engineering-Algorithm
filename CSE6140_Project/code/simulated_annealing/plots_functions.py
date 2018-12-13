import pandas as pd
import numpy as np
import matplotlib.pyplot as plt
plt.rcParams.update({'font.size': 20})
from time import sleep

def getQrdData(instance, method, cut_off_time,
               seed, nrun):
    # Get the best known solution
    best_sol = getBestKnownQuality(instance)

    qrdData = pd.DataFrame(columns=['run', 't', 'q'])

    for run in range(1, nrun + 1):
        # Load the trace file
        filename = '{}_{}_{}_{}_run{}of{}.trace'.format(instance,
                                                        method,
                                                        cut_off_time,
                                                        seed,
                                                        run,
                                                        nrun)

        data = pd.read_csv(filename, index_col=None,
                           header=None,
                           names=['t', 'q'])

        data['run'] = run
        data = data[['run', 't', 'q']]
        qrdData = qrdData.append(data)

    # Drop data with t = 0
    qrdData = qrdData.loc[qrdData['t'] > 0, :]

    return qrdData

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
                best_known_result = float(resultLine[1])
                break

    return best_known_result


def qPlot(qrdData, best_known_result,
          numx,
          qThresholds,instance,
          plotType='qrtd'):
    '''
    numx = the number of samples to take in the x-axis
    '''

    # Drop duplicates runtimes for same t
    qrdData.sort_values(['run', 't', 'q'],
                        ascending=[True, True, False],
                        inplace=True)  # sort just to be safe

    qrdData.drop_duplicates(keep='last', inplace=True)  # If multiple qualities at same runtime

    numTrials = qrdData['run'].nunique()

    qrdPlotData = pd.DataFrame(columns=['qThresh', 'tThresh', 'successRatio'])

    if plotType == 'qrtd':

        for qThreshold in qThresholds:
            data = qrdData.copy()

            # Only keep rows with quality lower than qThreshold
            data = data.loc[data['q'] <= best_known_result * (1 + (qThreshold / 100)), :]

            if len(data) > 1:
                # create a list of time thresholds
                # print(data)
                tlist = np.linspace(min(data['t']), max(data['t']), numx)

                for tThreshold in tlist:
                    successCount = data.loc[data['t'] <= tThreshold, 'run'].nunique()
                    successRatio = successCount / numTrials
                    qrdPlotData = qrdPlotData.append({'qThresh': qThreshold,
                                                      'tThresh': tThreshold,
                                                      'successRatio': successRatio},
                                                     ignore_index=True)

        # Plot it
        fig = plt.figure(figsize=(20, 10))
        # print(qrdPlotData)
        for qThresh in np.unique(qrdPlotData['qThresh']):
            qThreshPlotData = qrdPlotData.loc[qrdPlotData['qThresh'] == qThresh, :]
            plt.plot(qThreshPlotData['tThresh'], qThreshPlotData['successRatio'],
                     '-o',
                     label='q = {:.1f}%'.format(qThresh)
                     )

        plt.title("Qualified Runtime Distribution: {}".format(instance))
        plt.xlabel('Run-time [CPU sec]')
        plt.ylabel('P(solve)')
        plt.ylim((0,1.1))
        plt.legend(loc='center left', bbox_to_anchor=(1, 0.5))
        plt.show()

    elif plotType == 'sqd':

        # Use the quartiles as the time thresholds
        tThresholds = qrdData['t'].quantile([0, 0.25, 0.5, 0.75, 1])

        for tThreshold in tThresholds:

            data = qrdData.copy()

            # Remove all solutions which took more than threshold time
            data = data.loc[data['t'] <= tThreshold, :]

            for qThreshold in qThresholds:
                successCount = data.loc[data['q'] <= best_known_result * (1 + (qThreshold / 100)), 'run'].nunique()
                # print(successCount)
                successRatio = successCount / numTrials
                qrdPlotData = qrdPlotData.append({'qThresh': qThreshold,
                                                  'tThresh': tThreshold,
                                                  'successRatio': successRatio},
                                                 ignore_index=True)
        #             print(qrdPlotData)

        # Plot it
        fig = plt.figure(figsize=(20, 10))
        for idx,tThresh in enumerate(tThresholds):
            tThreshPlotData = qrdPlotData.loc[qrdPlotData['tThresh'] == tThresh, :]
            plt.plot(tThreshPlotData['qThresh'], tThreshPlotData['successRatio'],
                     '-o',
                     label='t = {:.3f}s (Q{})'.format(tThresh,idx)
                     )

        plt.title("Solution Quality Distribution: {}".format(instance))
        plt.xlabel('Relative Solution Quality [%]')
        plt.ylabel('P(solve)')
        plt.legend(loc='center left', bbox_to_anchor=(1, 0.5))
        plt.show()

#     return qrdPlotData

def plotBoxPlot(qrdData,plt):
    '''

    :param qrdData:
    :return:
    '''

    # Drop duplicates runtimes for same t
    qrdData.sort_values(['run', 't', 'q'],
                        ascending=[True, True, False],
                        inplace=True)  # sort just to be safe

    # only last entry of for a run
    qrdData.drop_duplicates(subset = ['run'],keep='last', inplace=True)
    qrdData.reset_index(drop = True,inplace = True)

    # Get the time values and make a box plot
    plt.boxplot(qrdData['t'])
    # plt.show()