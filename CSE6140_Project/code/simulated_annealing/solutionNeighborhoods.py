from random import sample,uniform
from time import sleep
from math import sqrt

def two_exchange_random(node_list,tabu_list,max_tabu_length,do_tabu = False):
    '''
    Given a list of nodes, it will return another list that is a two-exchange away from it.
    Can also incorporate TABU
    :param node_list: initial node list i.e. initial solution
    :param: tabu_list - current list of tabooed nodes
    :return: 2-exchanged node list i.e. neighbor solution
    '''
    NN = len(node_list)-1 # index of the last node
    index_list = list(range(NN+1))

    new_tabu_list = tabu_list.copy()


    # Pick a random index
    i = sample(index_list,1)[0]

    if do_tabu == True:
        if len(new_tabu_list) > 0:
            while i in new_tabu_list:
                if len(index_list) > 6:
                    index_list.remove(i) # Can't use that as our i
                    # Add i to the TABU list
                    # new_tabu_list.append(i)
                    i = sample(index_list,1)[0]
                    # If TABU list is full, remove the first element
                    if len(new_tabu_list) > max_tabu_length:
                        del new_tabu_list[0]
                else:
                    break # We need at least two nodes (to select j later). So just pick this one.

        index_list.remove(i)  # we don't want to select i again
        new_tabu_list.append(i)  # add it to the tabu list
    else:
        index_list.remove(i)  # we don't want to select i again

    # We want to make sure that we don't pick edge reversals with overlapping
    # nodes i.e. we must have 4 distinct nodes
    if i == 0:
        if 1 in index_list: index_list.remove(1) # don't pick the second node
        if NN in index_list: index_list.remove(NN) #don't pick the last node
    elif i == NN:
        if 0 in index_list: index_list.remove(0) # don't pick the first node
        if NN-1 in index_list: index_list.remove(NN-1) # don't pick the second to last node
    else:
        if i+1 in index_list: index_list.remove(i+1) # don't pick next neighbor
        if i-1 in index_list: index_list.remove(i-1) # don't pick prev neighbor

    ## SELECTION WITH TABU!
    j = sample(index_list,1)[0]

    if do_tabu == True:
        if len(new_tabu_list) > 0:
            while j in new_tabu_list:
                if len(index_list) > 1:
                    index_list.remove(j) # Can't use that as our j
                    j = sample(index_list,1)[0]

                    # If TABU list is full, remove the first element
                    if len(new_tabu_list) > max_tabu_length:
                        del new_tabu_list[0]
                else:
                    break #There is only one option remaining for j, so just pick it

        new_tabu_list.append(j)  # add it to the tabu list


    assert (i != j), "Same index selected twice"
    assert (abs(i-j) > 1), "Neighboring nodes selected for exchange"

    if i > j:
        i,j = j,i # let 'i' be the smaller of the two


    # The exchanging
    ends = [0,NN]
    if (j != NN):
        # neither the start nor the end indices selected
        left = node_list[0:i+1]
        mid = node_list[i+1:j]
        right = node_list[j:NN+1]
        mid.reverse() # reverse the mid list
        exchanged = left + mid + right
        return([exchanged,new_tabu_list])

    elif (j == NN):
        left = node_list[0:i+1]
        right = node_list[i+1:NN+1]
        right.reverse()
        exchanged = left + right
        return([exchanged,new_tabu_list])
    else:
        raise Exception("Case not accounted for: {}".format((i,j)))


def two_exchange_random_pruned(node_list,adj_mat,
                               box_length,prune_prob):
    '''
    Given a list of nodes, it will return another list that is a two-exchange away from it
    :param node_list: initial node list i.e. initial solution
    :param: adj_mat - the LOWER TRIANGULAR adjacency matrix
    :param: box_length - the length of boxes that define the neighborhood
    :return: 2-exchanged node list i.e. neighbor solution
    '''
    # NO TABU FOR THIS ONE

    NN = len(node_list)-1 # index of the last node
    index_list = list(range(NN+1))

    # Pick a random index
    i = sample(index_list,1)[0]
    index_list.remove(i)  # we don't want to select i again

    # We want to make sure that we don't pick edge reversals with overlapping
    # nodes i.e. we must have 4 distinct nodes
    if i == 0:
        if 1 in index_list: index_list.remove(1) # don't pick the second node
        if NN in index_list: index_list.remove(NN) #don't pick the last node
    elif i == NN:
        if 0 in index_list: index_list.remove(0) # don't pick the first node
        if NN-1 in index_list: index_list.remove(NN-1) # don't pick the second to last node
    else:
        if i+1 in index_list: index_list.remove(i+1) # don't pick next neighbor
        if i-1 in index_list: index_list.remove(i-1) # don't pick prev neighbor

    # Neighborhood Pruning
    neighbor_found = False
    while neighbor_found == False:
        j = sample(index_list,1)[0]
        if adj_mat[max(i,j)][min(i,j)] <= 2*sqrt(2)*box_length:
            # The new neighbor is within 2 box length away from our old node
            neighbor_found = True
            # Need to check the other edge as well here
        else:
            # remove the earlier j from the sample pool with some probability
            if len(index_list) > 1: # Don't remove if there are no more options
                chance = uniform(0, 1)
                if chance < prune_prob:
                    # resample. Look for another exchange
                    index_list.remove(j)
                else:
                    # don't resample. Accept this exchange.
                    neighbor_found = True
            else:
                # don't resample. Accept this exchange.
                neighbor_found = True

    assert (i != j), "Same index selected twice"
    assert (abs(i - j) > 1), "Neighboring nodes selected for exchange"

    if i > j:
        i, j = j, i  # let 'i' be the smaller of the two

    # The exchanging
    ends = [0, NN]
    if (j != NN):
        # neither the start nor the end indices selected
        left = node_list[0:i + 1]
        mid = node_list[i + 1:j]
        right = node_list[j:NN + 1]
        mid.reverse()  # reverse the mid list
        exchanged = left + mid + right
        return (exchanged)

    elif (j == NN):
        left = node_list[0:i + 1]
        right = node_list[i + 1:NN + 1]
        right.reverse()
        exchanged = left + right
        new_tabu_list = [] #NO TABU
        return (exchanged)
    else:
        raise Exception("Case not accounted for: {}".format((i, j)))