package logic.board.graph;

import logic.board.Board;
import logic.board.GameLevel;
import logic.util.Vector;
import org.junit.Test;

public class TestPathing {

    @Test
    public void test_graph_building() {
        Board board = GameLevel.fromJson("""
                {
                  "field"      : [
                    [4, 3, 3, 3, 3, 3, 3, 1],
                    [5, 3, 0, 0, 0, 0, 0, 3],
                    [2, 3, 0, 0, 0, 0, 0, 3],
                    [3, 0, 0, 0, 0, 0, 0, 3],
                    [3, 0, 0, 0, 0, 0, 0, 3],
                    [3, 0, 0, 0, 0, 0, 0, 3],
                    [3, 0, 0, 0, 0, 0, 0, 3],
                    [1, 3, 3, 3, 3, 3, 3, 1]
                  ],
                  "botRotation": 1
                }""").getBoard();

        Graph graph = Graph.of(board);
        System.out.println(graph);
    }

    @Test
    public void test_dijkstra() {
        Board board = GameLevel.fromJson("""
                {
                  "field"      : [
                    [4, 3, 3, 3, 3, 3, 3, 1],
                    [5, 3, 0, 0, 0, 0, 0, 3],
                    [2, 3, 0, 0, 0, 0, 0, 3],
                    [3, 0, 0, 0, 0, 0, 0, 3],
                    [3, 0, 0, 0, 0, 0, 0, 3],
                    [3, 0, 0, 0, 0, 0, 0, 3],
                    [3, 0, 0, 0, 0, 0, 0, 3],
                    [1, 3, 3, 3, 3, 3, 3, 1]
                  ],
                  "botRotation": 1
                }""").getBoard();

        Graph graph = Graph.of(board);
        graph.dijkstra(new Vector(0,0));
        System.out.println("====");
        graph.getCoins().forEach(node -> System.out.printf("%s distance: %d\n", node, node.getDistanceFromStart()));
    }

    @Test
    public void test_pathing() {
        Board board = GameLevel.fromJson("""
                {
                  "field"      : [
                    [4, 3, 3, 3, 3, 3, 3, 1],
                    [5, 3, 0, 0, 0, 0, 0, 3],
                    [2, 3, 0, 0, 0, 0, 0, 3],
                    [3, 0, 0, 0, 0, 0, 0, 3],
                    [3, 0, 0, 0, 0, 0, 0, 3],
                    [3, 0, 0, 0, 0, 0, 0, 3],
                    [3, 0, 0, 0, 0, 0, 0, 3],
                    [1, 3, 3, 3, 3, 3, 3, 1]
                  ],
                  "botRotation": 1
                }
                """).getBoard();

        PathFinder pathFinder = new PathFinder(board);
        var p = pathFinder.solve();
        System.out.println(p);
    }
}
