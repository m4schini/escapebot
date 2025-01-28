package logic.util;

import com.google.gson.Gson;

import java.util.Arrays;

/**
 * Simple vector calculation
 *
 * @author orcid.org/0009-0008-3023-1228 (inf104662)
 */
public final class Vector {
    private final int x;
    private final int y;
    private final int z;

    /**
     * Create vector with length of 0
     */
    public Vector() {
        this(0,0,0);
    }

    /**
     * Create a vector with length on z-Axis of 0
     *
     * @param x x-Axis Direction
     * @param y y-Axis Direction
     */
    public Vector(int x, int y) {
        this(x, y, 0);
    }

    /**
     * Create a full 3 Dimensional vector
     *
     * @param x x-Axis Direction
     * @param y y-Axis Direction
     * @param z z-Axis Direction
     */
    public Vector(int x, int y, int z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    /**
     * Get x dimension of this Vector
     *
     * @return x dimension of this Vector
     */
    public int X() {
        return x;
    }

    /**
     * Get y dimension of this Vector
     *
     * @return y dimension of this Vector
     */
    public int Y() {
        return y;
    }

    /**
     * Get z dimension of this Vector
     *
     * @return z dimension of this Vector
     */
    public int Z() {
        return z;
    }

    /**
     * UNTESTED
     * @return
     */
    public int length() {
        return (int) Math.sqrt(Math.pow(x, 2) + Math.pow(y, 2) + Math.pow(z, 2));
    }

    /**
     * Add two vectors together to a new Vector
     *
     * @param v other Vector
     * @return new Vector
     */
    public Vector add(Vector v) {
        return new Vector(
                this.x + v.x,
                this.y + v.y,
                this.z + v.z
        );
    }

    /**
     * Subtracts one vector from the other
     *
     * @param v other vector
     * @return result as new vector
     */
    public Vector subtract(Vector v) {
        return new Vector(
                this.x - v.x,
                this.y - v.y,
                this.z - v.z
        );
    }

    /**
     * Integer scalar multiplication with this Vector
     *
     * @param t scalar
     * @return t * this Vector
     */
    public Vector multiplyBy(int t) {
        return new Vector(
                this.x * t,
                this.y * t,
                this.z * t
        );
    }

    /**
     * Floating Point Number scalar multiplication with this Vector
     *
     * @param t scalar
     * @return t * this vector
     */
    public Vector multiplyBy(double t) {
        return new Vector(
                (int) Math.round(this.x * t),
                (int) Math.round(this.y * t),
                (int) Math.round(this.z * t)
        );
    }

    /**
     * Calculates the euclidean distance between to points;
     *
     * @param v2 the v 2
     * @return distance between points
     */
    public int distanceTo(Vector v2) {
        var v1 = this;
        return (int) Math.round(
                Math.sqrt(Math.pow(v2.x - v1.x, 2)
                        + Math.pow(v2.y - v1.y, 2)
                        + Math.pow(v2.z - v1.z, 2)
                ));
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Vector other) {
            return this.x == other.x
                    && this.y == other.y
                    && this.z == other.z;
        } else {
            return false;
        }
    }

    @Override
    public int hashCode() {
        return Arrays.hashCode(new int[]{x, y, z});
    }

    @Override
    public String toString() {
        return new Gson().toJson(this);
    }
}
