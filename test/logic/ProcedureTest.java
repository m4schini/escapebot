package logic;

import logic.board.GameLevel;
import logic.procedure.Instruction;
import logic.procedure.Procedure;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class ProcedureTest {
    Bot bot;

    @Before
    public void setUp() throws Exception {
        bot = new Bot(GameLevel.fromJson("""
                {
                  "field"      : [
                    [5, 5, 5, 5, 5, 5, 5, 5],
                    [5, 5, 5, 5, 5, 5, 5, 5],
                    [5, 5, 5, 5, 5, 5, 5, 5],
                    [5, 3, 5, 5, 5, 5, 5, 5],
                    [1, 4, 3, 3, 0, 3, 3, 2],
                    [5, 3, 5, 5, 5, 5, 5, 5],
                    [5, 5, 5, 5, 5, 5, 5, 5],
                    [5, 5, 5, 5, 5, 5, 5, 5]
                  ],
                  "botRotation": 1
                }
                """).getBoard());
    }

    /**
     * @implSpec in P wird P1 1 Mal aufgerufen, in P1 wird P2 aufgerufen
     * @see <a href="https://lms.fh-wedel.de/mod/assign/view.php?id=13561">Aufgabenstellung</a>
     */
    @Test(expected = IllegalStateException.class)
    public void ProcedureRecursion_P1callsP2() {
        var root = new Procedure(Instruction.EXECUTE_P1);
        var p1 = new Procedure(Instruction.EXECUTE_P2);
        var p2 = new Procedure(Instruction.FORWARD);
        bot.execute(root, p1, p2);

        Assert.assertFalse("If test reached this line, something is wrong", false);
    }

    /**
     * @implSpec mindestens 3 verschiedene nicht erlaubte Rekursionen testen (1.)
     * @see <a href="https://lms.fh-wedel.de/mod/assign/view.php?id=13561">Aufgabenstellung</a>
     */
    @Test(expected = IllegalStateException.class)
    public void ProcedureRecursion_P2callsP1() {
        var root = new Procedure(Instruction.EXECUTE_P2);
        var p1 = new Procedure(Instruction.EXECUTE_P2);
        var p2 = new Procedure(Instruction.EXECUTE_P1);
        bot.execute(root, p1, p2);

        Assert.assertFalse("If test reached this line, something is wrong", false);
    }

    /**
     * @implSpec mindestens 3 verschiedene nicht erlaubte Rekursionen testen (2.)
     * @see <a href="https://lms.fh-wedel.de/mod/assign/view.php?id=13561">Aufgabenstellung</a>
     */
    @Test(expected = IllegalStateException.class)
    public void ProcedureRecursion_P1callsP1() {
        var root = new Procedure(Instruction.EXECUTE_P1);
        var p1 = new Procedure(Instruction.EXECUTE_P1);
        var p2 = new Procedure(Instruction.FORWARD);
        bot.execute(root, p1, p2);

        Assert.assertFalse("If test reached this line, something is wrong", false);
    }

    /**
     * @implSpec mindestens 3 verschiedene nicht erlaubte Rekursionen testen (3.)
     * @see <a href="https://lms.fh-wedel.de/mod/assign/view.php?id=13561">Aufgabenstellung</a>
     */
    @Test(expected = IllegalStateException.class)
    public void ProcedureRecursion_P2callsP2() {
        var root = new Procedure(Instruction.EXECUTE_P2);
        var p1 = new Procedure(Instruction.EXECUTE_P2);
        var p2 = new Procedure(Instruction.FORWARD);
        bot.execute(root, p1, p2);

        Assert.assertFalse("If test reached this line, something is wrong", false);
    }

    @Test
    public void contains_emptyProc() {
        var p = new Procedure();
        assert !p.contains(Instruction.FORWARD);
    }

    @Test
    public void contains() {
        var p = new Procedure(
                Instruction.FORWARD,
                Instruction.TURN_LEFT
        );
        assert p.contains(Instruction.FORWARD);
    }

    @Test
    public void contains_not() {
        var p = new Procedure(
                Instruction.FORWARD,
                Instruction.TURN_LEFT
        );
        assert !p.contains(Instruction.TURN_RIGHT);
    }
}
