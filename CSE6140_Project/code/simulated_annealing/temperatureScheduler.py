from math import exp

def temperature_schedule(time,temp,
                             epsilon,
                             alpha,
                             strategy='geometricSeries'):
    '''
    Function that returns the next value of the temperature
    : param time: current time
    : param temp: current temp
    : param epsilon: temperatures below this will be considered zero
    :return: new temprature value
    '''

    # NOTE: We only use 'geometricSeries' in our experiments.

    if strategy == 'geometricSeries':
        new_temp = alpha*temp
    elif strategy == 'exp':
        new_temp = exp(-time / alpha)
    else:
        raise Exception("Unknown temperature schedule strategy")

    if new_temp < epsilon:
        new_temp= 0

    return new_temp