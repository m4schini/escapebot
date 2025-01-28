package logic.board.graph;

import logic.board.Board;
import logic.board.FieldType;
import logic.util.Vector;

import java.util.*;

/**
 * Parent class of graph representation of board.
 * Use {@link #of(Board)} to generate a new Graph.
 *
 * @author orcid.org/0009-0008-3023-1228 (inf104662)
 */
class Graph implements Iterable<Node> {
    /**
     * Map of all nodes, using their position as key.
     */
    private final Map<Vector, Node> nodeMap;

    /**
     * Initiate a new Graph. Should only be used internally or for testing. Use {@link #of(Board)}
     * to initiate a new Graph from a Board.
     */
    Graph() {
        this.nodeMap = new HashMap<>();
    }

    /**
     * Add new Node. Node needs Position
     * @param node node to add
     */
    public void add(Node node) {
        nodeMap.put(node.getPosition(), node);
    }

    /**
     * Retrieves node from map at position
     * @param position position in board
     * @return node from Position
     */
    public Node get(Vector position) {
        return nodeMap.get(position);
    }

    /**
     * Calculate distances from one start position to all other possible positions.
     * @param startPos start position
     */
    public void dijkstra(Vector startPos) {
        Node source = nodeMap.get(startPos);

        source.setDistanceFromStart(0);

        Set<Node> settledNodes = new HashSet<>();
        Set<Node> unsettledNodes = new HashSet<>();
        unsettledNodes.add(source);

        while (!unsettledNodes.isEmpty()) {
            Node current = pollFromSet(unsettledNodes);
            current.setStart(source);
            for (Edge adjacentEdge : current.getAdjacent()) {
                var adjacent = adjacentEdge.getNode();
                if (!settledNodes.contains(adjacent)) {
                    current.calcDistance(adjacent); //setCalcDistance(adjacent, adjacentEdge, current);
                    unsettledNodes.add(adjacent);
                }
            }
            settledNodes.add(current);
        }
    }

    /**
     * Get all coins in this Graph.
     * @return list of coins nodes in graph.
     */
    public List<Node> getCoins() {
        return new ArrayList<>(nodeMap.values().stream().filter(node -> node.getFieldType() == FieldType.COIN).toList());
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Adjacency list:\n");

        for (Node node : nodeMap.values()) {
            boolean isFirst = true;
            sb.append(node).append(":  \t[");
            for (Edge adjacent : node.getAdjacent()) {
                if (isFirst) {
                    isFirst = false;
                } else {
                    sb.append(", ");
                }
                sb.append(adjacent.getNode());
            }
            sb.append("]\n");
        }

        return sb.toString();
    }

    @Override
    public Iterator<Node> iterator() {
        return nodeMap.values().iterator();
    }

    /**
     * Factory method. Generates Graph from {@link Board}
     * @param board board
     * @return newly generated graph
     */
    public static Graph of(Board board) {
        Graph graph = new Graph();

        // generate Node map
        Map<Vector, Node> nodeMap = new HashMap<>();
        var fields = board.toArray();
        for (int y = 0; y < fields.length; y++) {
            for (int x = 0; x < fields[y].length; x++) {
                Node node = new Node(new Vector(x, y), fields[y][x]);
                nodeMap.put(new Vector(x, y), node);
            }
        }

        //var fields = board.toArray();
        for (int y = 0; y < fields.length; y++) {
            for (int x = 0; x < fields[y].length; x++) {
                var target = nodeMap.get(new Vector(x, y));
                if (FieldType.isWalkable(target.getFieldType())) {
                    target.setAdjacent(nodeMap); //addAdjacent(target, nodeMap);
                }
                graph.add(target);
            }
        }

        return graph;
    }

    /**
     * Helper method. Retrieves and removes first element from set.
     * @param set set
     * @return first element from Set
     */
    private static Node pollFromSet(Set<Node> set) {
        Node first = set.iterator().next();
        set.remove(first);
        return first;
    }
}
