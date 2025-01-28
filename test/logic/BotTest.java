package logic;

import logic.action.ActionType;
import logic.action.Actions;
import logic.board.Board;
import logic.procedure.Procedure;
import logic.util.Vector;
import org.junit.Assert;
import org.junit.Test;

import java.util.List;

import static logic.board.FieldType.*;
import static logic.procedure.Instruction.EXIT;
import static logic.procedure.Instruction.FORWARD;

public class BotTest {

    @Test
    public void stopExecutionAfterFailedAction() {
        var bot = new Bot(Board.from(Direction.NORTH, List.of(START, NORMAL, NORMAL)));
        var procedure = new Procedure(FORWARD, FORWARD, FORWARD, EXIT);
        var expected = List.of(ActionType.MOVE, ActionType.RUN_INTO_WALL);

        var actual = bot.execute(procedure, new Procedure(), new Procedure());

        Assert.assertTrue("actions failed:", actual.failed());
        Assert.assertEquals("stopped execution:", expected.get(1), actual.last().getType());
    }


    /**
     * @implSpec Bot geht mehrere Schritte
     * @see <a href="https://lms.fh-wedel.de/mod/assign/view.php?id=13561">Aufgabenstellung</a>
     */
    @Test
    public void move_multipleSteps() {
        var bot = new Bot(Board.from(Direction.EAST, List.of(START, NORMAL, NORMAL)));
        var expected = new Vector(2,0);

        bot.moveForward();
        bot.moveForward();

        Assert.assertEquals("Position of bot after move", expected, bot.getPosition());
    }

    /**
     * @implSpec Bot dreht sich [nach links] und geht dann mehrere Schritte
     * @see <a href="https://lms.fh-wedel.de/mod/assign/view.php?id=13561">Aufgabenstellung</a>
     */
    @Test
    public void turnLeft_moveMultipleSteps() {
        var bot = new Bot(Board.from(Direction.SOUTH, List.of(START, NORMAL, NORMAL)));

        var expectedPos = new Vector(2,0);
        var expectedDir = Direction.EAST;

        bot.turnLeft();
        Assert.assertEquals("Direction of bot after turn", expectedDir, bot.getDirection());

        bot.moveForward();
        bot.moveForward();
        Assert.assertEquals("Position of bot after move", expectedPos, bot.getPosition());
    }

    /**
     * @implSpec Bot dreht sich [nach rechts] und geht dann mehrere Schritte
     * @see <a href="https://lms.fh-wedel.de/mod/assign/view.php?id=13561">Aufgabenstellung</a>
     */
    @Test
    public void turnRight_moveMultipleSteps() {
        var bot = new Bot(Board.from(Direction.NORTH, List.of(START, NORMAL, NORMAL)));

        var expectedDir = Direction.EAST;
        var expectedPos = new Vector(2,0);

        bot.turnRight();
        Assert.assertEquals("Direction of bot after turn", expectedDir, bot.getDirection());

        bot.moveForward();
        bot.moveForward();
        Assert.assertEquals("Position of bot after move", expectedPos, bot.getPosition());
    }

    /**
     * @implSpec Bot geht gegen eine Mauer
     * @see <a href="https://lms.fh-wedel.de/mod/assign/view.php?id=13561">Aufgabenstellung</a>
     */
    @Test
    public void moveAgainstWall() {
        var bot = new Bot(Board.from(Direction.EAST, List.of(START, WALL)));
        var expected = ActionType.RUN_INTO_WALL;

        var actual = bot.moveForward();

        Assert.assertEquals("ActionType after running into wall", expected, actual.last().getType());
    }

    /**
     * @implSpec Bot geht in einen Abgrund
     * @see <a href="https://lms.fh-wedel.de/mod/assign/view.php?id=13561">Aufgabenstellung</a>
     */
    @Test
    public void moveIntoAbyss() {
        var bot = new Bot(Board.from(Direction.EAST, List.of(START, ABYSS)));
        var expected = ActionType.FALL_INTO_ABYSS;

        var actual = bot.moveForward();

        Assert.assertEquals("ActionType after falling into abyss", expected, actual.last().getType());
    }

    /**
     * @implSpec Bot springt über einen Abgrund
     * @see <a href="https://lms.fh-wedel.de/mod/assign/view.php?id=13561">Aufgabenstellung</a>
     */
    @Test
    public void jumpAboveAbyss() {
        var bot = new Bot(Board.from(Direction.EAST,
                List.of(NORMAL, WALL, WALL),
                List.of(ABYSS, WALL, WALL),
                List.of(START, ABYSS, NORMAL)));
        var expected = ActionType.JUMP;

        var actual = bot.jump();

        Assert.assertEquals("Jump above abyss", expected, actual.last().getType());
        // no exception expected
    }

    /**
     * @implSpec Bot springt auf einem normalen Feld
     * @see <a href="https://lms.fh-wedel.de/mod/assign/view.php?id=13561">Aufgabenstellung</a>
     */
    //@Test(expected = JumpNotAllowedException.class)
    public void jumpAboveNormal() {
        var bot = new Bot(Board.from(Direction.EAST, List.of(START, ABYSS, NORMAL)));

        bot.jump();

        assert false; // if test reaches this line, the expected exception was not thrown and the test failed
    }

    // -> wird in ProcedureTest getestet
//    /**
//     * @implSpec Bot sammelt Münze ein
//     * @see <a href="https://lms.fh-wedel.de/mod/assign/view.php?id=13561">Aufgabenstellung</a>
//     */
//    @Test
//     public void collectCoin() {
//         throw new UnsupportedOperationException("not implemented");
//     }

    /**
     * @implSpec Bot erhält Anweisung "Exit" neben einer Tür [-> auf einem Tür feld]
     * @see <a href="https://lms.fh-wedel.de/mod/assign/view.php?id=13561">Aufgabenstellung</a>
     */
    @Test
    public void exit_inFront_Door() {
        var bot = new Bot(Board.from(Direction.EAST, List.of(START, DOOR)));
        var expected = ActionType.EXIT_SUCCESSFUL;

        var actual = bot.exit();

        Assert.assertEquals("exit suc", expected, actual.last().getType());
    }

    /**
     * @implSpec Bot erhält Anweisung "Exit" entfernt von einer Tür [-> NICHT auf einem Tür feld]
     * @see <a href="https://lms.fh-wedel.de/mod/assign/view.php?id=13561">Aufgabenstellung</a>
     */
    @Test
    public void exit_NOT_inFront_Door() {
        var bot = new Bot(Board.from(Direction.EAST, List.of(START, NORMAL, DOOR)));
        var expected = ActionType.EXIT_FAILED;

        var actual = bot.exit();

        Assert.assertEquals("exit fail", expected, actual.last().getType());
    }


    /**
     * @implSpec Bot geht auf Exit, ohne alle Münzen eingesammelt zu haben
     * @see <a href="https://lms.fh-wedel.de/mod/assign/view.php?id=13561">Aufgabenstellung</a>
     */
    public void exit_withoutAllCoins() {
        var bot = new Bot(Board.from(Direction.EAST, List.of(START, DOOR, COIN)));
        var expected = ActionType.EXIT_FAILED;

        var actual = bot.exit();

        Assert.assertEquals("exit fail", expected, actual.last());
    }

    /**
     * @implSpec Bot sammelt Münzen und geht auf Exit
     * @see <a href="https://lms.fh-wedel.de/mod/assign/view.php?id=13561">Aufgabenstellung</a>
     */
    @Test
    public void collectCoin_moveToExit() {
        var bot = new Bot(Board.from(Direction.EAST, List.of(START, COIN, DOOR)));
        var expected = ActionType.EXIT_SUCCESSFUL;
        var actions = new Actions();

        actions.addAll(bot.moveForward());
        actions.addAll(bot.exit());

        Assert.assertEquals("exit coin suc", expected, actions.last().getType());
    }
}
