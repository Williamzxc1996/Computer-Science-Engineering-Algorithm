from time import sleep

def distance_cost(tour_vertices,adj_mat,mat_type = 'low_triag'):
    '''
    Computes the cost for a given tour
    :param tour_vertices: a list of vertices comprising a tour. It is assumed
    that the final vertex is connected to the first vertex i.e. there should
    be no repeated vertices.
    : adj_mat: The adjacency matrix with the edge weight values
    : mat_type: 'full' for a full adj matrix, or 'low_triag' for lower triangular for symmetric matrices
    :return: a positive float that is the sum of all edges in the tour
    '''

    total_dist = 0
    for from_iter in range(len(tour_vertices)-1):

        from_node,to_node = tour_vertices[from_iter],tour_vertices[from_iter+1]

        if from_node > to_node:
            total_dist+= adj_mat[from_node][to_node]
        elif to_node > from_node:
            total_dist += adj_mat[to_node][from_node]
        else:
            raise Exception("Invalid self-loop edge found!")



    # Get the final edge that loops back to the first node
    from_node = tour_vertices[len(tour_vertices)-1]
    to_node = tour_vertices[0]

    if from_node > to_node:
        total_dist += adj_mat[from_node][to_node]
    elif to_node > from_node:
        total_dist += adj_mat[to_node][from_node]
    else:
        raise Exception("Invalid self-loop edge found!")


    return total_dist

