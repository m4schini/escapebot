package logic;

import logic.procedure.Instruction;
import logic.util.Vector;
import org.junit.Assert;
import org.junit.Test;

import java.util.List;

public class DirectionTest {
    @Test
    public void test_rotate_positiveRotation() {
        assert Direction.NORTH.rotate(1).equals(Direction.EAST);
        assert Direction.EAST.rotate(1).equals(Direction.SOUTH);
        assert Direction.SOUTH.rotate(1).equals(Direction.WEST);
        assert Direction.WEST.rotate(1).equals(Direction.NORTH);
    }

    @Test
    public void test_rotate_negativeRotation() {
        assert Direction.NORTH.rotate(-1).equals(Direction.WEST);
        assert Direction.WEST.rotate(-1).equals(Direction.SOUTH);
        assert Direction.SOUTH.rotate(-1).equals(Direction.EAST);
        assert Direction.EAST.rotate(-1).equals(Direction.NORTH);
    }

    @Test
    public void test_rotateFromTo() {
        var from = new Vector(-2,0);
        var to = new Vector(2,3);
        var direction = Direction.NORTH;

        Direction.rotateFromTo(from, to, direction);

    }

    @Test
    public void test_rotateFromTo_NORTH_EAST() {
        var from = new Vector(-2,0);
        var to = new Vector(2,3);
        var direction = Direction.NORTH;

        var actual = Direction.rotateFromTo(from, to, direction);
        Assert.assertEquals(List.of(Instruction.TURN_RIGHT), actual);
    }

    @Test
    public void test_rotateFromTo_NORTH_WEST() {
        var from = new Vector(2,0);
        var to = new Vector(-2,3);
        var direction = Direction.NORTH;

        var actual = Direction.rotateFromTo(from, to, direction);
        Assert.assertEquals(List.of(Instruction.TURN_LEFT), actual);
    }

    @Test
    public void test_rotateFromTo_NORTH_SOUTH() {
        var from = new Vector(0,0);
        var to = new Vector(2,3);
        var direction = Direction.NORTH;

        var actual = Direction.rotateFromTo(from, to, direction);
        Assert.assertEquals(2, actual.size());
    }

    @Test
    public void test_rotateFromTo_SOUTH_NORTH() {
        var from = new Vector(0,3);
        var to = new Vector(2,0);
        var direction = Direction.SOUTH;

        var actual = Direction.rotateFromTo(from, to, direction);
        Assert.assertEquals(2, actual.size());
    }

    @Test
    public void test_rotateFromTo_SOUTH_EAST() {
        var from = new Vector(-2,0);
        var to = new Vector(2,3);
        var direction = Direction.SOUTH;

        var actual = Direction.rotateFromTo(from, to, direction);
        Assert.assertEquals(List.of(Instruction.TURN_LEFT), actual);
    }

    @Test
    public void test_rotateFromTo_EAST_WEST() {
        var from = new Vector(2,0);
        var to = new Vector(-2,3);
        var direction = Direction.EAST;

        var actual = Direction.rotateFromTo(from, to, direction);
        Assert.assertEquals(2, actual.size());
    }

    @Test
    public void test_rotateFromTo_EAST_SOUTH() {
        var from = new Vector(0,0);
        var to = new Vector(0,1);
        var direction = Direction.EAST;

        var actual = Direction.rotateFromTo(from, to, direction);
        Assert.assertEquals(List.of(Instruction.TURN_RIGHT), actual);
    }
}
