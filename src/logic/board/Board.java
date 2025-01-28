package logic.board;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.TypeAdapter;
import com.google.gson.annotations.JsonAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import logic.Direction;
import logic.board.graph.PathFinder;
import logic.exception.validation.MissingFieldException;
import logic.procedure.Procedure;
import logic.util.Vector;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


/**
 * This class is responsible for field IO operations.
 *
 * @author orcid.org/0009-0008-3023-1228 (inf104662)
 */
@JsonAdapter(Board.JsonAdapter.class)
public class Board {

    /**
     * Problems with board
     */
    public enum ProblemType {
        /**
         * Field is not reachable from start field
         */
        NOT_REACHABLE,
        /**
         * Field doesn't exist
         */
        DOESNT_EXIST,
        /**
         * There are too many fields of this type
         */
        TOO_MANY,
        /**
         * The solution found for this level exceeds procedure capacity
         */
        SOLUTION_TOO_BIG
    }
    /**
     * Problem record. Describes Problem with analysed board
     * @param problemType type of problem
     * @param fieldType fieldType of problem
     * @param position position of problem (null if problem is not position specific)
     */
    public record Problem(ProblemType problemType, FieldType fieldType, Vector position){}

    /**
     * Internal representation of board
     */
    private final FieldType[][] fields;
    /**
     * The Height. Measured in vertical cells.
     */
    private final int height;
    /**
     * The Width. Measured in horizontal cells.
     */
    private final int width;
    /**
     * position of bot
     */
    private Vector positionOfBot;
    /**
     * direction of bot
     */
    private Direction directionOfBot;

    /**
     * Instantiates a new Board.
     *
     * @param directionOfBot the direction of bot
     * @param fields         the fields
     */
    public Board(Direction directionOfBot, FieldType[][] fields) {
        this.fields = fields;
        this.positionOfBot = this.getStartPosition();
        this.directionOfBot = directionOfBot; //DEFAULT VALUE

        this.height = fields.length;
        this.width = fields.length < 1 ? 0 : fields[0].length;
    }

    /**
     * Initiates a new board from given fields
     * @param fields arrays of fields
     */
    public Board(FieldType[][] fields) {
        this(null, fields);
    }

    /**
     * Instantiates a new Board.
     *
     * @param level the level
     */
    public Board(GameLevel level) {
        this(level.getStartBotDirection(), level.getBoard().fields);
    }

    /**
     * Copy Constructor.
     *
     * @param board board that should be copied.
     */
    public Board(Board board) {
        assert (board != null);

        this.height = board.height;
        this.width = board.width;
        this.positionOfBot = board.positionOfBot;
        this.directionOfBot = board.directionOfBot;
        // deep copy field
        this.fields = Arrays
                .stream(board.fields)
                .map(FieldType[]::clone)
                .toArray(FieldType[][]::new);
    }

    /**
     * Returns position of first tile with matching FieldType
     * @param fieldType FieldType to find
     * @return position of first field with fieldType, null if none was found
     */
    public Vector positionOf(FieldType fieldType) {
        for (int y = 0; y < fields.length; y++) {
            for (int x = 0; x < fields[y].length; x++) {
                if (fields[y][x] == fieldType) {
                    return new Vector(x, y);
                }
            }
        }

        return null;
    }

    /**
     * Returns instructions of positions of all occurrences of given FieldType.
     * @param fieldType FieldType to find
     * @return instructions of positions
     */
    public List<Vector> positionsOf(FieldType fieldType) {
        List<Vector> positions = new ArrayList<>();

        for (int y = 0; y < fields.length; y++) {
            for (int x = 0; x < fields[y].length; x++) {
                if (fields[y][x] == fieldType) {
                    positions.add(new Vector(x, y));
                }
            }
        }

        return positions;
    }

    /**
     * Counts occurrences of given fieldType
     * @param fieldType type of field
     * @return count of field
     */
    public int count(FieldType fieldType) {
        int count = 0;

        for (FieldType[] row : fields) {
            for (FieldType cell : row) {
                if (cell == fieldType) {
                    count++;
                }
            }
        }

        return count;
    }

    /**
     * Are coins left in board?
     * @return true, if coins are contained in board
     */
    public boolean hasCoins() {
        return contains(FieldType.COIN);
    }

    /**
     * Find problems with the current board and returns a summary of all problems
     * @return list of Problems
     */
    private List<Problem> findProblems() {
        List<Problem> problems = new ArrayList<>();

        // a valid board is only allowed to have 1 EXIT
        int countOfExit = count(FieldType.DOOR);
        if (countOfExit == 0) problems.add(new Problem(ProblemType.DOESNT_EXIST, FieldType.DOOR, null));
        if (countOfExit > 1) problems.add(new Problem(ProblemType.TOO_MANY, FieldType.DOOR, null));

        // a valid board is only allowed to have 1 START
        int countOfStart = count(FieldType.START);
        boolean canBeFlooded = countOfStart == 1;
        if (countOfStart == 0) problems.add(new Problem(ProblemType.DOESNT_EXIST, FieldType.START, null));
        if (countOfStart > 1) problems.add(new Problem(ProblemType.TOO_MANY, FieldType.START, null));

        if (canBeFlooded) {
            // flooding board from bot start position to "remove" all reachable fields
            var clone = new Board(this);
            Board.flood(clone, clone.getStartPosition());

            // after flood, exit shouldn't exist. If it does, it is not reachable
            var exitPos = clone.positionOf(FieldType.DOOR);
            if (exitPos != null) problems.add(new Problem(ProblemType.NOT_REACHABLE, FieldType.DOOR, exitPos));

            // after flood, there should be no coins left, if they are, they're not reachable
            var coinPos = clone.positionsOf(FieldType.COIN);
            for (Vector position : coinPos) {
                problems.add(new Problem(ProblemType.NOT_REACHABLE, FieldType.COIN, position));
            }
        }

        return problems;
    }

    /**
     * Analysis this board and generates a list of all problems
     * @return analysis with result and instructions of problems
     */
    public List<Problem> analyze() {
        List<Problem> problems = findProblems();

        if (problems.isEmpty()) {
            List<Procedure> solution = new Board(this).solve();

            if ((solution.size() >= 1 && solution.get(0).size() > 12)
                || (solution.size() >= 2 && solution.get(1).size() > 8)
                || (solution.size() >= 3 && solution.get(2).size() > 8)) {
                problems.add(new Problem(ProblemType.SOLUTION_TOO_BIG, FieldType.START, null));
            }
        }

        return problems;
    }

    /**
     * Checks if field is isSolvable, meaning that there is a way from start to exit
     *
     * @return true, if field is isSolvable
     */
    public boolean hasProblems() {
        return findProblems().isEmpty();
    }

    /**
     * Flooding all fields reachable from startPosition
     * @param board board to flood
     * @param startPosition position flooding starts from
     */
    private static void flood(Board board, Vector startPosition) {
        floodFill(startPosition.X(), startPosition.Y(), board.fields);
    }

    /**
     * Solves board and returns 3 procedures with solution
     * @return 3 Procedures
     */
    public List<Procedure> solve() {
        if (!hasProblems()) throw new IllegalStateException("Board is not isSolvable");
        final int procCount = 3;

        PathFinder pathFinder = new PathFinder(this);
        var rawSolution = pathFinder.solve();

        var solution = Procedure.optimize(rawSolution);

        var finalSol = new ArrayList<>(solution);

        // fill empty procs
        while (finalSol.size() < procCount) {
            finalSol.add(new Procedure());
        }

        return finalSol;
    }

    /**
     * Recursive functions for board flooding.
     * @param x X coordinate
     * @param y Y coordinate
     * @param fields two dimensional array of field types
     * @return true, if door was found
     */
    private static boolean floodFill(int x, int y, FieldType[][] fields) {
        FieldType f = get(fields, x, y);
        if (f == null)
            return false;
        else if (f.IS_WALKABLE) {
            fields[y][x] = null;
            return floodFill(x + 1, y, fields)
                    || floodFill(x - 1, y, fields)
                    || floodFill(x, y + 1, fields)
                    || floodFill(x, y - 1, fields)
                    || (FieldType.isJumpable(get(fields, x+1, y)) && floodFill(x + 2, y, fields))
                    || (FieldType.isJumpable(get(fields, x-1, y)) && floodFill(x - 2, y, fields))
                    || (FieldType.isJumpable(get(fields, x, y+1)) && floodFill(x, y + 2, fields))
                    || (FieldType.isJumpable(get(fields, x, y-1)) && floodFill(x, y - 2, fields));
        } else {
            return false;
        }
    }

    /**
     * Get position of start in board.
     *
     * @return the start position
     */
    public Vector getStartPosition() {
        return positionOf(FieldType.START);
    }

    /**
     * Get position of exit (door) in board
     *
     * @return the exit position
     */
    public Vector getExitPosition() {
        return positionOf(FieldType.DOOR);
    }

    /**
     * Width int.
     *
     * @return the int
     */
    public int getWidth() {
        return this.width;
    }

    /**
     * Height int.
     *
     * @return the int
     */
    public int getHeight() {
        return this.height;
    }

    /**
     * safe 2d array get method (doesnt produce ArrayOutOfBoundsException),
     * never returns null.
     *
     * @param fields 2d array of fieldTypes
     * @param x      x coordinate
     * @param y      y coordinate
     * @return FieldType or, if coordinates are out of bounds, {@link FieldType#WALL}
     */
    public static FieldType get(FieldType[][] fields, int x, int y) {
        if (y >= fields.length || y < 0) return FieldType.WALL;
        if (x >= fields[y].length || x < 0) return FieldType.WALL;
        return fields[y][x];
    }

    /**
     * Get {@code FieldType} at Point described by given Vector
     *
     * @param v vector to point
     * @return {@code FieldType} at point in field
     */
    public FieldType get(Vector v) {
        return get(v.X(), v.Y());
    }

    /**
     * Get {@code FieldType} at given coordinates
     *
     * @param x X-Axis coordinate
     * @param y Y-Axis coordinate
     * @return {@code FieldType} at point in field (if x, y is out of bounds, returns {@link FieldType#WALL})
     */
    public FieldType get(int x, int y) {
        return get(this.fields, x, y);
    }

    /**
     * safe 2d array set method
     *
     * @param fields    2d array of fieldTypes
     * @param x         x coordinate
     * @param y         y coordinate
     * @param fieldType FieldType
     * @return true, if method was successful
     */
    static boolean set(FieldType[][] fields, int x, int y, FieldType fieldType) {
        if (y >= fields.length || y < 0) return false;
        if (x >= fields[y].length || x < 0) return false;

        fields[y][x] = fieldType;
        return true;
    }

    /**
     * Set {@code FieldType} at Point described by given Vector
     *
     * @param v    vector to point
     * @param type {@code FieldType} that should be set at point
     */
    public void set(Vector v, FieldType type) {
        set(v.X(), v.Y(), type);
    }

    /**
     * Set {@code FieldType} at Point described by coordinates
     *
     * @param x    X-Axis coordinate
     * @param y    Y-Axis coordinate
     * @param type {@code FieldType} that should be set at point
     * @return the boolean
     */
    public boolean set(int x, int y, FieldType type) {
        return set(this.fields, x, y, type);
    }

    /**
     * Sets direction of bot.
     *
     * @param direction the direction
     */
    public void setDirectionOfBot(Direction direction) {
        this.directionOfBot = direction;
    }

    /**
     * Sets bot.
     *
     * @param position the position
     */
    public void setBot(Vector position) {
        this.setBot(position, directionOfBot);
    }

    /**
     * Sets bot.
     *
     * @param position  the position
     * @param direction the direction
     */
    public void setBot(Vector position, Direction direction) {
        this.positionOfBot = position;
        this.directionOfBot = direction;
    }

    /**
     * Gets position of bot.
     *
     * @return the position of bot
     */
    public Vector getPositionOfBot() {
        return this.positionOfBot;
    }

    /**
     * Gets direction of bot.
     *
     * @return the direction of bot
     */
    public Direction getDirectionOfBot() {
        return directionOfBot;
    }

    /**
     * Is given object contained in board?
     *
     * @param obj obj to look for
     * @return true, if contained
     */
    public boolean contains(Object obj) {
        return contains(toArray(), obj);
    }

    /**
     * static helper function for contains. Static so it can be used in isSolvable on cloned fields
     *
     * @param fields board as array
     * @param obj to check if contained
     * @return true, if obj is contained
     */
    private static boolean contains(FieldType[][] fields, Object obj) {
        if (!(obj instanceof FieldType fieldType)) return false;

        boolean contains = false;
        for (int y = 0; y < fields.length && !contains; y++) {
            for (int x = 0; x < fields[y].length && !contains; x++) {
                if (fields[y][x] == fieldType) {
                    contains = true;
                }
            }
        }

        return contains;
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof Board board
                && Arrays.deepEquals(board.fields, this.fields);
    }

    /**
     * Converts board to a 2D array
     *
     * @return 2D array representation of board.
     */
    public FieldType[][] toArray() {
        return Arrays.stream(fields)
                .map(FieldType[]::clone)
                .toArray(FieldType[][]::new);
    }

    @Override
    public String toString() {
        return "Board:\n" + new GsonBuilder()
                .setPrettyPrinting().create()
                .toJson(this)
                .replaceAll("],", "],\n ")
                .replaceAll(",", ", ");
    }

    /**
     * Generates a new board from given FieldTypes
     * @param directionOfBot start direction of bot
     * @param rows varargs of fieldtype, has to be atleast one large
     * @return new Board
     */
    @SafeVarargs
    public static Board from(Direction directionOfBot, List<FieldType>... rows) {
        assert rows.length > 0;

        var b = new Board(
                (Arrays.stream(rows)
                .map(fieldTypes -> fieldTypes.toArray(FieldType[]::new))
                .toArray(FieldType[][]::new)));
        b.setDirectionOfBot(directionOfBot);

        return b;
    }

    /**
     * This TypeAdapter directly puts through the field to the json,
     * ignoring all other fields.
     */
    public static class JsonAdapter extends TypeAdapter<Board> {
        @Override
        public void write(JsonWriter out, Board value) throws IOException {
            out.jsonValue(new Gson().toJson(value.fields));
        }

        @Override
        public Board read(JsonReader in) {
            var fields = new Gson().fromJson(in, FieldType[][].class);

            if (!contains((FieldType[][]) fields, FieldType.START)) {
                throw new MissingFieldException(FieldType.START);
            }

            return new Board((FieldType[][]) fields);
        }
    }
}
