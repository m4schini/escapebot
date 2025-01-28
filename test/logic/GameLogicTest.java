package logic;

import actor.FakeGui;
import logic.action.ActionType;
import logic.board.GameLevel;
import logic.procedure.Instruction;
import logic.procedure.Procedure;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class GameLogicTest {
    GameLogic gameLogic;
    FakeGui gui;
    Procedure emptyProcedure;

    @Before
    public void setUp() throws Exception {
        System.out.println("<===========================================");

        emptyProcedure = new Procedure();
        gui = new FakeGui();
        gameLogic = new GameLogic(gui, GameLevel.fromJson("""
                {
                  "field"      : [
                    [5, 5, 5, 5, 5, 5, 5, 5],
                    [5, 5, 5, 5, 5, 5, 5, 5],
                    [5, 5, 5, 5, 5, 5, 5, 5],
                    [5, 5, 5, 5, 5, 5, 5, 5],
                    [4, 3, 3, 1, 0, 3, 3, 2],
                    [5, 5, 5, 5, 5, 5, 5, 5],
                    [5, 5, 5, 5, 5, 5, 5, 5],
                    [5, 5, 5, 5, 5, 5, 5, 5]
                  ],
                  "botRotation": 1
                }"""));
    }

    @After
    public void tearDown() throws Exception {
        System.out.println("============================================>");
    }

    //@Deprecated
    //@Test(expected = ProcedureInvalidInstruction.class)
    //public void execute_emptyProcedures() {
    //    gameLogic.execute(emptyProcedure, emptyProcedure, emptyProcedure);
    //}

    @Test
    public void actionQueue_addAction() {
        var p = new Procedure(Instruction.FORWARD, Instruction.EXIT);
        gameLogic.execute(p, emptyProcedure, emptyProcedure);

        System.out.printf("logic.action.Actions(%s):%n", gui.getActions().size());
        gui.getActions().forEach(System.out::println);

        // Expected logic.action.Actions:
        //  - START
        //  - MOVE
        //  - EXIT_FAILED
        Assert.assertEquals(3, gui.getActions().size());
        Assert.assertEquals(gui.getActions().get(0), (ActionType.START));
        Assert.assertEquals(gui.getActions().get(1), (ActionType.MOVE));
        Assert.assertEquals(gui.getActions().get(2), (ActionType.EXIT_FAILED));
    }

    @Test
    public void execute_suc() {
        var p = new Procedure(
                Instruction.FORWARD,
                Instruction.FORWARD,
                Instruction.FORWARD,
                Instruction.JUMP,
                Instruction.FORWARD,
                Instruction.EXIT
        );
        gameLogic.execute(p, emptyProcedure, emptyProcedure);

        System.out.printf("logic.action.Actions(%s):%n", gui.getActions().size());
        gui.getActions().forEach(System.out::println);

        Assert.assertEquals(ActionType.EXIT_SUCCESSFUL, gui.getActions().get(gui.getActions().size() - 1).getType());
    }

    @Test
    public void execute_jumpIntoAbyss() {
        var p = new Procedure(
                Instruction.FORWARD,
                Instruction.FORWARD,
                Instruction.JUMP,
                Instruction.FORWARD,
                Instruction.FORWARD,
                Instruction.EXIT
        );
        gameLogic.execute(p, emptyProcedure, emptyProcedure);

        System.out.printf("logic.action.Actions(%s):%n", gui.getActions().size());
        gui.getActions().forEach(System.out::println);

        
        Assert.assertEquals(7, gui.getActions().size());
        Assert.assertEquals(gui.getActions().get(6).getType(), ActionType.FALL_INTO_ABYSS);
    }

    @Test
    public void execute_jumpIntoWall() {
        var p = new Procedure(
                Instruction.FORWARD,
                Instruction.TURN_LEFT,
                Instruction.JUMP,
                Instruction.FORWARD,
                Instruction.FORWARD,
                Instruction.EXIT
        );
        gameLogic.execute(p, emptyProcedure, emptyProcedure);

        System.out.printf("logic.action.Actions(%s):%n", gui.getActions().size());
        gui.getActions().forEach(System.out::println);

        Assert.assertEquals(5, gui.getActions().size());
        Assert.assertEquals(ActionType.MOVE, gui.getActions().get(3).getType());
        Assert.assertEquals(ActionType.RUN_INTO_WALL, gui.getActions().get(4).getType());
    }

    @Test
    public void execute_runIntoWall() {
        var p = new Procedure(
                Instruction.TURN_LEFT,
                Instruction.FORWARD,
                Instruction.EXIT
        );
        gameLogic.execute(p, emptyProcedure, emptyProcedure);

        System.out.printf("logic.action.Actions(%s):%n", gui.getActions().size());
        gui.getActions().forEach(System.out::println);

        // Expected logic.action.Actions:
        //  - START
        //  - TURN
        //  - MOVE
        //  - RUN_INTO_WALL
        Assert.assertEquals(4 ,gui.getActions().size());
        Assert.assertEquals(ActionType.START, gui.getActions().get(0).getType());
        Assert.assertEquals(ActionType.TURN_LEFT, gui.getActions().get(1).getType());
        Assert.assertEquals(ActionType.MOVE, gui.getActions().get(2).getType());
        Assert.assertEquals(ActionType.RUN_INTO_WALL, gui.getActions().get(3).getType());
    }

    @Test
    public void execute_outsideOfBoard() {
        var p = new Procedure(
                Instruction.TURN_LEFT,
                Instruction.TURN_LEFT,
                Instruction.FORWARD,
                Instruction.EXIT
        );
        gameLogic.execute(p, emptyProcedure, emptyProcedure);

        // Expected logic.action.Actions:
        //  - START
        //  - TURN
        //  - TURN
        //  - MOVE
        //  - RUN_INTO_WALL
        Assert.assertEquals(5, gui.getActions().size());
        Assert.assertEquals(ActionType.START, gui.getActions().get(0).getType());
        Assert.assertEquals(ActionType.TURN_LEFT, gui.getActions().get(1).getType());
        Assert.assertEquals(ActionType.TURN_LEFT, gui.getActions().get(2).getType());
        Assert.assertEquals(ActionType.MOVE, gui.getActions().get(3).getType());
        Assert.assertEquals(ActionType.RUN_INTO_WALL, gui.getActions().get(4).getType());
    }
}
