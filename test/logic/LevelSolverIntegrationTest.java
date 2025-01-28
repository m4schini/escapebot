package logic;

import actor.FakeGui;
import logic.action.ActionType;
import logic.board.GameLevel;
import org.junit.Assert;
import org.junit.Test;

public class LevelSolverIntegrationTest {
    @Test
    public void test_xtra_level0() {
        var level = GameLevel.fromJson("""
                {
                  "name": "Level 0 - Einführung",
                  "field"      : [
                    [5, 5, 5, 5, 5, 5, 5, 5],
                    [5, 5, 5, 5, 5, 5, 5, 5],
                    [5, 5, 5, 5, 5, 5, 5, 5],
                    [5, 5, 5, 5, 5, 5, 5, 5],
                    [1, 4, 3, 3, 0, 3, 3, 2],
                    [5, 5, 5, 5, 5, 5, 5, 5],
                    [5, 5, 5, 5, 5, 5, 5, 5],
                    [5, 5, 5, 5, 5, 5, 5, 5]
                  ],
                  "botRotation": 1
                }""");
        var fakeGui = new FakeGui();
        var gameLogic = new GameLogic(fakeGui, level);
        var board = gameLogic.getBoard();

        // Test board analysis
        var analysis = board.analyze();
        Assert.assertTrue("Level shouldn't have problems", analysis.isEmpty());

        // Test procedure generator
        var solution = board.solve();
        Assert.assertTrue("Solution should have at least 1 Procedure", solution.size() > 0);
        Assert.assertTrue("Solution should not have more than 3 Procedures", solution.size() <= 3);

        // Test execution
        gameLogic.execute(solution.get(0), solution.get(1), solution.get(2));
        var actionsFromExecution = fakeGui.getActions();
        Assert.assertEquals("Exit should be successful",
                ActionType.EXIT_SUCCESSFUL,
                (actionsFromExecution.get(actionsFromExecution.size() - 1)).getType());
    }

    @Test
    public void test_required_level1() {
        var level = GameLevel.fromJson("""
                {
                  "field"      : [
                    [5, 5, 5, 5, 5, 5, 5, 5],
                    [5, 5, 5, 5, 5, 5, 5, 5],
                    [5, 5, 5, 5, 5, 5, 5, 5],
                    [5, 5, 5, 5, 5, 5, 5, 5],
                    [4, 3, 3, 3, 3, 3, 3, 2],
                    [5, 5, 5, 5, 5, 5, 5, 5],
                    [5, 5, 5, 5, 5, 5, 5, 5],
                    [5, 5, 5, 5, 5, 5, 5, 5]
                  ],
                  "botRotation": 1
                }""");
        var fakeGui = new FakeGui();
        var gameLogic = new GameLogic(fakeGui, level);
        var board = gameLogic.getBoard();

        // Test board analysis
        var analysis = board.analyze();
        Assert.assertTrue("Level shouldn't have problems", analysis.isEmpty());

        // Test procedure generator
        var solution = board.solve();
        Assert.assertTrue("Solution should have at least 1 Procedure", solution.size() > 0);
        Assert.assertTrue("Solution should not have more than 3 Procedures", solution.size() <= 3);

        // Test execution
        gameLogic.execute(solution.get(0), solution.get(1), solution.get(2));
        var actionsFromExecution = fakeGui.getActions();
        Assert.assertEquals("Exit should be successful",
                ActionType.EXIT_SUCCESSFUL,
                (actionsFromExecution.get(actionsFromExecution.size() - 1)).getType());
    }

    @Test
    public void test_required_level2() {
        var level = GameLevel.fromJson("""
                {
                  "field"      : [
                    [5, 5, 5, 5, 5, 5, 5, 5],
                    [5, 5, 5, 5, 5, 5, 5, 5],
                    [5, 5, 5, 5, 5, 5, 5, 5],
                    [4, 3, 3, 3, 0, 3, 3, 3],
                    [5, 5, 5, 5, 5, 5, 5, 3],
                    [2, 3, 3, 3, 0, 3, 3, 3],
                    [5, 5, 5, 5, 5, 5, 5, 5],
                    [5, 5, 5, 5, 5, 5, 5, 5]
                  ],
                  "botRotation": 1
                }""");
        var fakeGui = new FakeGui();
        var gameLogic = new GameLogic(fakeGui, level);
        var board = gameLogic.getBoard();

        // Test board analysis
        var analysis = board.analyze();
        Assert.assertTrue("Level shouldn't have problems", analysis.isEmpty());

        // Test procedure generator
        var solution = board.solve();
        Assert.assertTrue("Solution should have at least 1 Procedure", solution.size() > 0);
        Assert.assertTrue("Solution should not have more than 3 Procedures", solution.size() <= 3);

        // Test execution
        gameLogic.execute(solution.get(0), solution.get(1), solution.get(2));
        var actionsFromExecution = fakeGui.getActions();
        Assert.assertEquals("Exit should be successful",
                ActionType.EXIT_SUCCESSFUL,
                (actionsFromExecution.get(actionsFromExecution.size() - 1)).getType());
    }

    @Test
    public void test_required_level3() {
        var level = GameLevel.fromJson("""
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
                }""");
        var fakeGui = new FakeGui();
        var gameLogic = new GameLogic(fakeGui, level);
        var board = gameLogic.getBoard();

        // Test board analysis
        var analysis = board.analyze();
        Assert.assertTrue("Level shouldn't have problems", analysis.isEmpty());

        // Test procedure generator
        var solution = board.solve();
        Assert.assertTrue("Solution should have at least 1 Procedure", solution.size() > 0);
        Assert.assertTrue("Solution should not have more than 3 Procedures", solution.size() <= 3);

        // Test execution
        gameLogic.execute(solution.get(0), solution.get(1), solution.get(2));
        var actionsFromExecution = fakeGui.getActions();
        Assert.assertEquals("Exit should be successful",
                ActionType.EXIT_SUCCESSFUL,
                (actionsFromExecution.get(actionsFromExecution.size() - 1)).getType());
    }

    @Test
    public void test_required_level4() {
        var level = GameLevel.fromJson("""
                {
                  "field"      : [
                    [4, 5, 3, 3, 3, 5, 5, 5],
                    [3, 5, 3, 5, 3, 5, 5, 5],
                    [3, 5, 3, 5, 3, 5, 5, 5],
                    [3, 5, 3, 5, 3, 5, 5, 5],
                    [3, 5, 3, 5, 3, 5, 5, 5],
                    [3, 5, 3, 5, 3, 5, 5, 5],
                    [3, 5, 3, 5, 3, 5, 5, 5],
                    [3, 3, 3, 5, 3, 3, 3, 2]
                  ],
                  "botRotation": 2
                }
                """);
        var fakeGui = new FakeGui();
        var gameLogic = new GameLogic(fakeGui, level);
        var board = gameLogic.getBoard();

        // Test board analysis
        var analysis = board.analyze();
        Assert.assertTrue("Level shouldn't have problems", analysis.isEmpty());

        // Test procedure generator
        var solution = board.solve();
        Assert.assertTrue("Solution should have at least 1 Procedure", solution.size() > 0);
        Assert.assertTrue("Solution should not have more than 3 Procedures", solution.size() <= 3);

        // Test execution
        gameLogic.execute(solution.get(0), solution.get(1), solution.get(2));
        var actionsFromExecution = fakeGui.getActions();
        Assert.assertEquals("Exit should be successful",
                ActionType.EXIT_SUCCESSFUL,
                (actionsFromExecution.get(actionsFromExecution.size() - 1)).getType());
    }

    @Test
    public void test_xtra_level5() {
        var level = GameLevel.fromJson("{\"field\":[[4,3,3,3,3,3,3,3],[5,3,0,0,3,0,0,3],[2,3,0,0,3,0,0,3],[3,0,0,0,3,0,0,3],[3,3,3,3,1,3,3,3],[3,0,0,0,3,0,0,3],[3,0,0,0,3,0,0,3],[1,3,3,3,3,3,3,3]],\"botRotation\":1}");
        var fakeGui = new FakeGui();
        var gameLogic = new GameLogic(fakeGui, level);
        var board = gameLogic.getBoard();

        // Test board analysis
        var analysis = board.analyze();
        Assert.assertTrue("Level shouldn't have problems", analysis.isEmpty());

        // Test procedure generator
        var solution = board.solve();
        Assert.assertTrue("Solution should have at least 1 Procedure", solution.size() > 0);
        Assert.assertTrue("Solution should not have more than 3 Procedures", solution.size() <= 3);

        // Test execution
        gameLogic.execute(solution.get(0), solution.get(1), solution.get(2));
        var actionsFromExecution = fakeGui.getActions();
        Assert.assertEquals("Exit should be successful",
                ActionType.EXIT_SUCCESSFUL,
                (actionsFromExecution.get(actionsFromExecution.size() - 1)).getType());
    }
}
