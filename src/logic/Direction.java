package logic;

import com.google.gson.TypeAdapter;
import com.google.gson.annotations.JsonAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import logic.exception.validation.ValidationException;
import logic.exception.validation.UnexpectedTypeException;
import logic.procedure.Instruction;
import logic.util.Vector;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Direction enum.
 * Used for bot-rotation and getting vectors for possible direction;
 *
 * @author orcid.org/0009-0008-3023-1228 (inf104662)
 */
@JsonAdapter(Direction.JsonAdapter.class)
public enum Direction {
    /**
     * The North.
     * Direction vector has to point "down" instead of "up" because the zero coordinate is top left,
     * instead of down left
     */
    NORTH(0, -1),
    /**
     * East direction.
     */
    EAST(1, 0),
    /**
     * South direction.
     * Direction vector has to point "up" instead of "down" because the zero coordinate is top left,
     * instead of down left
     */
    SOUTH(0, 1),
    /**
     * West direction.
     */
    WEST(-1, 0);

    /**
     * unit vector of direction
     */
    private final Vector direction;

    /**
     * Direction
     * @param x x length of directional vector
     * @param y y length of directional vector
     */
    Direction(int x, int y) {
        this.direction = new Vector(x, y);
    }

    /**
     * Vector vector.
     *
     * @return the vector
     */
    public Vector vector() {
        return direction;
    }

    /**
     * Rotate direction.
     *
     * @param rotation the rotation
     * @return the direction
     */
    public Direction rotate(int rotation) {
        int a = (this.ordinal() + rotation);
        int b = Direction.values().length;

        return fromOrdinal((a % b + b) % b);
    }

    /**
     * From ordinal direction.
     *
     * @param ordinal the ordinal
     * @return the direction
     */
    public static Direction fromOrdinal(int ordinal) {
        var values = values();
        if (ordinal < 0 || ordinal >= values.length)
            throw new IllegalArgumentException(String.format("Doesn't Exist (ordinal=%d)", ordinal));
        return values[ordinal];
    }

    /**
     * Direction from 'from' to 'to'
     * @param from start position
     * @param to end position
     * @return Direction of to position
     */
    public static Direction fromTo(Vector from, Vector to) {
        int horizontal = from.X() - to.X();
        int vertical = from.Y() - to.Y();

        Direction targetDirection = null;

        if (Math.abs(horizontal) >= Math.abs(vertical)) {
            targetDirection = horizontal >= 0 ? WEST : EAST;
        } else {
            targetDirection = vertical >= 0 ? NORTH : SOUTH;
        }

        return targetDirection;
    }

    /**
     * Generate the Instruction needed to rotate from startDirection to targetDirection.
     * @param startdirection start direction
     * @param targetDirection target direction
     * @return list of instructions
     */
    public static List<Instruction> rotateFromTo(Direction startdirection, Direction targetDirection) {
        List<Instruction> instructions = new ArrayList<>();

        int diff = targetDirection.ordinal() - startdirection.ordinal();
        if (diff > 2) diff -= 4;
        if (diff < -2) diff += 4;
        if (Math.abs(diff) == 2) {
            instructions.add(Instruction.TURN_RIGHT);
            instructions.add(Instruction.TURN_RIGHT);
        } else if (diff > 0) {
            instructions.add(Instruction.TURN_RIGHT);
        } else if (diff < 0){
            instructions.add(Instruction.TURN_LEFT);
        }
        return instructions;
    }

    /**
     * Generate the Instruction needed to rotate from startDirection to targetDirection.
     * @param from start position
     * @param to target position
     * @param start start direction
     * @return list of instruction to turn into 'to' direction
     */
    public static List<Instruction> rotateFromTo(Vector from, Vector to, Direction start) {
        return rotateFromTo(start, fromTo(from, to));
    }

    /**
     * This adapter converts Direction values to ordinals instead of string names.
     *
     * Task definition states that the direction has to be saved as an integer
     */
    public static class JsonAdapter extends TypeAdapter<Direction> {

        @Override
        public void write(JsonWriter out, Direction value) throws IOException {
            out.value(value.ordinal());
        }

        @Override
        public Direction read(JsonReader in) throws IOException {
            Integer ordinal = null;
            try {
                ordinal = in.nextInt();
                return fromOrdinal(ordinal);
            } catch (NumberFormatException e) {
                String type = e.getMessage().split(" ")[2];
                throw new UnexpectedTypeException(in.getPath(),
                        "int",
                        type.substring(0, type.length() - 1));
            } catch (IllegalArgumentException e) {
                throw new ValidationException(String.format("Mismatched Format: %s => %s", in.getPath(), ordinal));
            }
        }
    }
}
