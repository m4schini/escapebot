package logic.board.graph;

import logic.board.FieldType;
import logic.util.Vector;

import java.util.*;
import java.util.function.Function;

/**
 * The type Node.
 */
class Node {
    /**
     * Adjacent Nodes
     */
    private final List<Edge> adjacent;
    /**
     * Position of Node in {@link logic.board.Board}
     */
    private final Vector position;
    /**
     * FieldType of Node in {@link logic.board.Board}
     */
    private final FieldType fieldType;

    /**
     * Distance from Start Node
     */
    private int distanceFromStart = Integer.MAX_VALUE;
    /**
     * Path of Nodes from Start Node
     */
    private List<Node> pathFromStart = new ArrayList<>();
    /**
     * Start Node
     */
    private Node start = this;
    /**
     * Predecessor (as seen from Start Node / Path) Node
     */
    private Node predecessor = null;

    /**
     * Initiate a new Node.
     *
     * @param position  position of Node
     * @param fieldType fieldType of Node
     */
    public Node(Vector position, FieldType fieldType) {
        assert position != null;
        assert fieldType != null;

        this.position = position;
        this.fieldType = fieldType;
        this.adjacent = new ArrayList<>();
    }

    /**
     * Add edge from this node to adjacent Node
     *
     * @param node toNode
     */
    public void edgeTo(Node node) {
        adjacent.add(new Edge(node));
    }

    /**
     * Get x coordinate of position
     *
     * @return x coordinate of position
     */
    public int X() {
        return position.X();
    }

    /**
     * Get y coordinate of position
     *
     * @return y coordinate of position
     */
    public int Y() {
        return position.Y();
    }

    /**
     * Get position as vector
     *
     * @return position as vector
     */
    public Vector getPosition() {
        return position;
    }

    /**
     * Get fieldType of Node
     *
     * @return fieldType field type
     */
    public FieldType getFieldType() {
        return fieldType;
    }

    /**
     * Get List of adjacent
     *
     * @return list of adjacent edges
     */
    public List<Edge> getAdjacent() {
        return new ArrayList<>(adjacent);
    }

    /**
     * Generate edges to neighboring (in the sense of reachable) Nodes
     *
     * @param neighborMap map of neighbors
     */
    public void setAdjacent(Map<Vector, Node> neighborMap) {
        /*
         * List of offsetter functions used to get the fields of certain direction with specific distances
         */
        List<Function<Integer, Vector>> offsetters = List.of(
                offset -> new Vector(X() + offset, Y()),
                offset -> new Vector(X() - offset, Y()),
                offset -> new Vector(X(), Y() + offset),
                offset -> new Vector(X(), Y() - offset)
        );

        for (Function<Integer, Vector> offsetter : offsetters) {
            var adjacentNode = neighborMap.get(offsetter.apply(1));
            if (adjacentNode != null) {
                var field = adjacentNode.getFieldType();
                if (FieldType.isWalkable(field)) {
                    this.edgeTo(adjacentNode);
                }
                if (FieldType.isJumpable(field)) {
                    var jumpNode = neighborMap.get(offsetter.apply(2));
                    if (jumpNode != null) {
                        this.edgeTo(jumpNode);
                    }
                }
            }
        }
    }

    /**
     * Calc distance from this to eval Node
     *
     * @param evaluationNode eval Node
     */
    public void calcDistance(Node evaluationNode) {
        final int edgeWeight = 1;

        if (this.distanceFromStart + edgeWeight < evaluationNode.distanceFromStart) {
            evaluationNode.distanceFromStart = this.distanceFromStart + edgeWeight;

            List<Node> shortestPath = new LinkedList<>(this.pathFromStart);
            shortestPath.add(this);

            this.predecessor = evaluationNode; // ???
            evaluationNode.pathFromStart = shortestPath;
        }
    }

    /**
     * Gets distance from start.
     *
     * @return the distance from start
     */
    public int getDistanceFromStart() {
        return distanceFromStart;
    }

    /**
     * Sets distance from start.
     *
     * @param distanceFromStart the distance from start
     */
    public void setDistanceFromStart(int distanceFromStart) {
        this.distanceFromStart = distanceFromStart;
    }

    /**
     * Gets path from start.
     *
     * @return the path from start
     */
    public List<Node> getPathFromStart() {
        return new ArrayList<>(pathFromStart);
    }

    /**
     * Sets path from start.
     *
     * @param pathFromStart the path from start
     */
    public void setPathFromStart(List<Node> pathFromStart) {
        this.pathFromStart = pathFromStart;
    }

    /**
     * Gets start.
     *
     * @return the start
     */
    public Node getStart() {
        return start;
    }

    /**
     * Sets start.
     *
     * @param start the start
     */
    public void setStart(Node start) {
        this.start = start;
    }

    /**
     * Gets predecessor.
     *
     * @return the predecessor
     */
    public Node getPredecessor() {
        return predecessor;
    }

    /**
     * Sets predecessor.
     *
     * @param predecessor the predecessor
     */
    public void setPredecessor(Node predecessor) {
        this.predecessor = predecessor;
    }

    @Override
    public int hashCode() {
        return position.hashCode();
    }

    @Override
    public String toString() {
        return String.format("%s(%d|%d)", fieldType, position.X(), position.Y());
    }
}
