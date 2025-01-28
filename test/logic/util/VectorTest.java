package logic.util;

import org.junit.Test;

public class VectorTest {

    @Test
    public void construct_zeroVector() {
        var v = new Vector();

        assert v.X() == 0;
        assert v.Y() == 0;
        assert v.Z() == 0;
    }

    @Test
    public void construct_2dVector() {
        var xExpected = 1;
        var yExpected = 1;
        var v = new Vector(xExpected, yExpected);

        assert v.X() == xExpected;
        assert v.Y() == yExpected;
        assert v.Z() == 0;
    }

    @Test
    public void construct_3dVector() {
        var xExpected = 1;
        var yExpected = 1;
        var zExpected = 1;
        var v = new Vector(xExpected, yExpected, zExpected);

        assert v.X() == xExpected;
        assert v.Y() == yExpected;
        assert v.Z() == zExpected;
    }

    @Test
    public void add_positiveNumbers() {
        var v1 = new Vector(0,0,0);
        var v2 = new Vector(1,2,3);

        var expected = new Vector(1,2,3);
        var actual = v1.add(v2);

        System.out.printf("expected %s; actual %s", expected, actual);
        assert actual.equals(expected);
    }

    @Test
    public void add_negativeNumbers() {
        var v1 = new Vector(0,0,0);
        var v2 = new Vector(1,2,3);

        var expected = new Vector(1,2,3);
        var actual = v1.add(v2);

        System.out.printf("expected %s; actual %s", expected, actual);
        assert actual.equals(expected);

    }

    @Test
    public void add_Numbers() {
        var v1 = new Vector(1,2,3);
        var v2 = new Vector(-1,-2,-3);

        var expected = new Vector(0,0,0);
        var actual = v1.add(v2);

        System.out.printf("expected %s; actual %s", expected, actual);
        assert actual.equals(expected);

    }

    @Test
    public void multiplyBy_positiveInteger() {
        var v = new Vector(1,2,3);

        var expected = new Vector(2, 4, 6);
        var actual = v.multiplyBy(2);

        System.out.printf("expected %s; actual %s", expected, actual);
        assert actual.equals(expected);
    }

    @Test
    public void multiplyBy_negativeInteger() {
        var v = new Vector(1, 2, 3);

        var expected = new Vector(-1, -2, -3);
        var actual = v.multiplyBy(-1);

        System.out.printf("expected %s; actual %s", expected, actual);
        assert actual.equals(expected);
    }

    @Test
    public void multiplyBy_positiveFloat() {
        var v = new Vector(2,4,6);

        var expected = new Vector(1,2,3);
        var actual = v.multiplyBy(.5F);

        System.out.printf("expected %s; actual %s", expected, actual);
        assert actual.equals(expected);
    }

    @Test
    public void multiplyBy_negativeFloat() {
        var v = new Vector(2,4,6);

        var expected = new Vector(-1,-2,-3);
        var actual = v.multiplyBy(-.5F);

        System.out.printf("expected %s; actual %s", expected, actual);
        assert actual.equals(expected);
    }

    @Test
    public void distanceTo_1Dimension() {
        var v0 = new Vector();
        var v1 = new Vector(5,0,0);

        var expected = 5;
        var actual = v0.distanceTo(v1);

        System.out.printf("expected %d; actual %d", expected, actual);
        assert actual == expected;
    }

    @Test
    public void distanceTo_3Dimensions_roundingUp() {
        var v0 = new Vector();
        var v1 = new Vector(5,5,5);

        var expected = 9;
        var actual = v0.distanceTo(v1);

        System.out.printf("expected %d; actual %d", expected, actual);
        assert actual == expected;
    }

    @Test
    public void distanceTo_3Dimensions_roundingDown() {
        var v0 = new Vector();
        var v1 = new Vector(5,4,5);

        var expected = 8;
        var actual = v0.distanceTo(v1);

        System.out.printf("expected %d; actual %d", expected, actual);
        assert actual == expected;
    }

    @Test
    public void equals() {
        var v1 = new Vector(1,1,1);
        var v2 = new Vector(1,1,1);
        var v3 = new Vector(42, 42, 42);

        assert v1.equals(v2);
        assert v2.equals(v1);
        assert !v1.equals(v3);
        assert !v3.equals(v1);
        assert !v2.equals(v3);
        assert !v3.equals(v2);
    }
}
