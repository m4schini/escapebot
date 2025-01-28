package logic.board.graph;

/**
 * This class represents a directional edge between one Node to toNode
 *
 * @author orcid.org/0009-0008-3023-1228 (inf104662)
 */
class Edge {
    /**
     * weight of edge, always one.
     */
    private final int weight;
    /**
     * Node this edge directs to
     */
    private final Node toNode;

    /**
     * Initiates a new Edge
     * @param nodeTo node this edge points to
     */
    public Edge(Node nodeTo) {
        this.weight = 1;
        this.toNode = nodeTo;
    }

    /**
     * Get weight of edge
     * @return weight of edge
     */
    public int getWeight() {
        return weight;
    }

    /**
     * Get node this edge points to
     * @return node this edge points to
     */
    public Node getNode() {
        return toNode;
    }
}
