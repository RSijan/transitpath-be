package model;

/**
 * A simple interface for edges in a graph.
 * 
 * An edge connects one node to another. This interface just says
 * that every edge should be able to give you the node it points to.
 */

public interface Edge {
    Stop getTarget();
}
