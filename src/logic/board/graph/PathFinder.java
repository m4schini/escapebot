package logic.board.graph;

import logic.Direction;
import logic.board.Board;
import logic.board.FieldType;
import logic.procedure.Instruction;
import logic.util.Log;
import logic.util.Vector;

import java.util.*;

/**
 * Pathfinder generates a graph from a board and then searches in the
 * resulting graph the optimal path between start and finish. The Path
 * is converted to instructions.
 */
public class PathFinder {
    /**
     * Graph generated from Board
     */
    private Graph graph;
    /**
     * Source board
     */
    private Board board;

    /**
     * Origin postion
     */
    private Vector origin;

    /**
     * Initiates a new Pathfinder. Uses board start position as origin.
     * @param board source board
     */
    public PathFinder(Board board) {
        this(board, board.getStartPosition());
    }

    /**
     * Initiates a new Pathfinder
     * @param board source board
     * @param origin start position
     */
    public PathFinder(Board board, Vector origin) {
        this.board = board;
        this.graph = Graph.of(board);
        setOrigin(origin);
    }

    /**
     * Sets new board and recalculates all path from origin
     * @param board board
     * @param origin origin (start) position
     */
    private void recalculate(Board board, Vector origin) {
        this.board = board;
        this.graph = Graph.of(board);
        setOrigin(origin);
    }

    /**
     * Sets origin position and if necessary recalculates distances from origin.
     *
     * @param origin new origin position
     * @return true, if origin changed and graph distances were recalculated
     */
    public boolean setOrigin(Vector origin) {
        boolean changeOrigin = this.origin == null || !this.origin.equals(origin);
        if (changeOrigin) {
            this.origin = origin;
            this.graph.dijkstra(origin);

            Log.debug("Recalculated distances from origin");
        } else {
            Log.warning("Origin didn't change. Skipped recalculation.");
        }

        return changeOrigin;
    }

    /**
     * Generates list of instructions that first collect coins and then go to exit.
     *
     * @return list of instructions.
     */
    public List<Instruction> solve() {
        var path = generateCompletePath();
        return generateInstructionsFromPath(path);
    }

    /**
     * Generates path from current origin to first occurrence of fieldType
     * @param fieldType fieldtype
     * @return path of nodes to fieldtype
     */
    List<Node> pathTo(FieldType fieldType) {
        return pathTo(origin, board.positionOf(fieldType));
    }

    /**
     * Generates path to first occurrence of fieldType
     * @param fieldType fieldType
     * @return list of nodes to fieldType
     */
    List<Node> pathTo(Vector from, FieldType fieldType) {
        return pathTo(from, board.positionOf(fieldType));
    }

    /**
     * Generate path point a to b
     * @param from point a
     * @param to point b
     * @return list of nodes from a to b;
     */
    List<Node> pathTo(Vector from, Vector to) {
        Board originalBoard = this.board;
        Vector originalOrigin = this.origin;

        recalculate(board, from);

        // path from last coin (or origin if no coins exist) to exit
        var exit = graph.get(to);
        LinkedList<Node> path = new LinkedList<>(exit.getPathFromStart());
        path.addLast(exit);

        // reset pathfinder
        recalculate(originalBoard, originalOrigin);
        return path;
    }

    /**
     * Generate complete path to exit from origin.
     * @param newOrigin set a new origin
     * @return complete path to exit (door);
     */
    List<Node> generateCompletePath(Vector newOrigin) {
        assert newOrigin != null;
        Board originalBoard = this.board;
        Vector originalOrigin = this.origin;

        setOrigin(newOrigin);

        List<Node> completePath = new LinkedList<>();
        List<Node> coins = graph.getCoins();

        // generate path to all coins
        while (!coins.isEmpty()) {
            // sorting by distance to origin
            coins.sort(Comparator.comparingInt(Node::getDistanceFromStart));
            // selecting closest to origin
            Node coin = coins.get(0);
            // add nodes from origin to coin
            completePath.addAll(coin.getPathFromStart());
            Log.debug("COIN: DISTANCE=%s | PATH=%s\n", coin.getDistanceFromStart(), coin.getPathFromStart());
            // remove coin from board
            board.set(coin.getPosition(), FieldType.NORMAL);
            // recalculate distance -> use last coin location as new origin
            recalculate(board, coin.getPosition());
            // update coins list
            coins = graph.getCoins();
        }
        // path from last coin (or origin if no coins exist) to exit
        var exit = graph.get(board.positionOf(FieldType.DOOR));
        completePath.addAll(exit.getPathFromStart());
        completePath.add(exit);
        Log.debug("Complete Path Length: %d\n", completePath.size());

        // reset pathfinder
        recalculate(originalBoard, originalOrigin);
        return completePath;
    }

    /**
     * Generate complete path from origin to exit
     * @return complete path of nodes to exit
     */
    List<Node> generateCompletePath() {
        return generateCompletePath(origin);
    }

    /**
     * Generates a list of instructions from given path
     *
     * @param pathNodes list of connected nodes
     * @return list of instructions following path
     */
    private List<Instruction> generateInstructionsFromPath(List<Node> pathNodes) {
        Queue<Node> path = new LinkedList<>(pathNodes);
        Direction currentDirectionOfBot = board.getDirectionOfBot();
        List<Instruction> instructions = new ArrayList<>(path.size());

        // iterate over path
        while (!path.isEmpty()) {
            var current = path.poll();
            var next = path.peek();
            assert next != null;

            // only turn when needed
            var expectedNextPosition = current.getPosition().add(currentDirectionOfBot.vector());
            var actualNextPosition = next.getPosition();
            if (!expectedNextPosition.equals(actualNextPosition)) {
                var turn = Direction.rotateFromTo(
                        current.getPosition(),
                        next.getPosition(),
                        currentDirectionOfBot);
                instructions.addAll(turn);

                currentDirectionOfBot = Direction.fromTo(current.getPosition(), next.getPosition());
            }

            // if handle exit or movement
            if (next.getFieldType() == FieldType.DOOR) {
                // add exit and clear queue
                instructions.add(Instruction.EXIT);
                path.clear();
            } else {
                // handle jumps and movements
                var length = current.getPosition().distanceTo(next.getPosition());
                var move = switch (length) {
                    case 1 -> Instruction.FORWARD;
                    case 2 -> Instruction.JUMP;
                    default -> throw new IllegalStateException("length of difference too large");
                };
                instructions.add(move);
            }
        }

        return instructions;
    }
}
