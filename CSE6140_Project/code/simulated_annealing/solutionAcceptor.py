from random import uniform
from math import exp

def accept_new_solution(deltaE, temperature, strategy = 'exp'):
    '''
    :deltaE: change in energy due to new solution
    :iteration: current iteration
    :return: True if the solution should be accepted
    '''

    if strategy == 'exp':
        prob = exp(-deltaE/temperature) # Negative since we are considering drops in energy
    else:
        raise Exception("Unknown solution acceptance strategy")

    chance = uniform(0,1)
    if chance < prob:
        return True
    else:
        return False