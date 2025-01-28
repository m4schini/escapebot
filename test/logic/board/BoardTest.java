package logic.board;

import logic.Direction;
import org.junit.Assert;
import org.junit.Test;
import logic.util.Vector;

import java.util.List;

import static logic.board.FieldType.*;

public class BoardTest {

    @Test
    public void varArgs_constructor() {
        var board = Board.from(
                Direction.NORTH,
                List.of(NORMAL, NORMAL, NORMAL),
                List.of(START,  NORMAL, DOOR),
                List.of(ABYSS,  ABYSS,  ABYSS)

        );
        System.out.println(board);
    }

    @Test
    public void test_solvable_solvableField() {
        var board = Board.from(
                Direction.NORTH,
                List.of(NORMAL, NORMAL, NORMAL),
                List.of(START,  NORMAL, DOOR),
                List.of(ABYSS,  ABYSS,  ABYSS)
        );

        Assert.assertTrue("board is isSolvable", board.hasProblems());
    }

    @Test
    public void test_solvable_unsolvableField() {
        var board = Board.from(
                Direction.NORTH,
                List.of(ABYSS, ABYSS, ABYSS),
                List.of(START,  ABYSS, ABYSS),
                List.of(ABYSS,  ABYSS,  DOOR)
        );

        Assert.assertFalse("board is unsolvable", board.hasProblems());
    }

    @Test
    public void test_solvable_solvableWithJump() {
        var board = Board.from(
                Direction.NORTH,
                List.of(START, ABYSS, NORMAL),
                List.of(NORMAL,  ABYSS, DOOR),
                List.of(ABYSS,  ABYSS,  ABYSS)
        );

        Assert.assertTrue("board is isSolvable", board.hasProblems());
    }

    @Test
    public void test_solvable_realLevel() {
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
                }
                """);

        Assert.assertTrue("board is isSolvable", level.getBoard().hasProblems());
    }

    @Test
    public void getField() {
        var expected = START;
        var board = Board.from(
                Direction.NORTH,
                List.of(NORMAL,   NORMAL, NORMAL, ABYSS),
                List.of(expected, NORMAL, DOOR,   ABYSS),
                List.of(NORMAL,   NORMAL, NORMAL, ABYSS),
                List.of(NORMAL,   NORMAL, NORMAL, ABYSS)
        );

        assert board.get(0, 1).equals(expected);
    }

    @Test
    public void getField_vector() {
        var expected = START;
        var board = Board.from(
                Direction.NORTH,
                List.of(NORMAL,   NORMAL, NORMAL, ABYSS),
                List.of(expected, NORMAL, DOOR,   ABYSS),
                List.of(NORMAL,   NORMAL, NORMAL, ABYSS),
                List.of(NORMAL,   NORMAL, NORMAL, ABYSS)
        );

        assert board.get(new Vector(0, 1)).equals(expected);
    }

    @Test
    public void setField() {
        var expected = START;
        var board = Board.from(
                Direction.NORTH,
                List.of(NORMAL, NORMAL, NORMAL, ABYSS),
                List.of(NORMAL, NORMAL, DOOR,   ABYSS),
                List.of(NORMAL, START, NORMAL, ABYSS),
                List.of(NORMAL, NORMAL, NORMAL, ABYSS)
        );
        board.set(0, 1, expected);

        assert board.get(0, 1).equals(expected);
    }

    @Test
    public void setField_vector() {
        var expected = START;
        var location = new Vector(0, 1);
        var board = Board.from(
                Direction.NORTH,
                List.of(NORMAL, NORMAL, NORMAL, ABYSS),
                List.of(NORMAL, NORMAL, DOOR,   ABYSS),
                List.of(NORMAL, START, NORMAL, ABYSS),
                List.of(NORMAL, NORMAL, NORMAL, ABYSS)
        );
        board.set(location, expected);

        assert board.get(location).equals(expected);
    }
}
