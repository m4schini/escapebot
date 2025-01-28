package logic;

import logic.action.Action;
import logic.action.ActionType;
import logic.action.Actions;
import logic.board.Board;
import logic.board.FieldType;
import logic.exception.ProcedureInvalidInstruction;
import logic.exception.ProcedureInvalidRecursionException;
import logic.procedure.Instruction;
import logic.procedure.Procedure;
import logic.util.Vector;

import static logic.action.ActionType.*;
import static logic.util.Log.*;

/**
 * This base class owns the procedure execution logic, actual bot operations have to be implemented.
 *
 * @author orcid.org/0009-0008-3023-1228 (inf104662)
 */
public class Bot {
    /**
     * The constant JUMP_DISTANCE.
     */
    public static final int JUMP_DISTANCE = 2;
    /**
     * instance of board. Used to check board movements against board
     */
    private final Board board;

    /**
     * Instantiates a new board
     * @param board board copy
     */
    public Bot(Board board) {
        this.board = board;
    }

    /**
     * Gets position.
     *
     * @return the position
     */
    public Vector getPosition() {
        return board.getPositionOfBot();
    }

    /**
     * Gets direction of bot
     * @return direction
     */
    public Direction getDirection() {
        return board.getDirectionOfBot();
    }

    /**
     * Sets position.
     *
     * @param v the new position of bot
     * @return Actions that occured during execution
     */
    public Actions placeBot(Vector v) {
        var actions = new Actions();
        //if bot is on field with coin -> collect coin
        if (board.get(v).equals(FieldType.COIN)) {
            actions.add(makeAction(ActionType.COLLECT_COIN, v));
            board.set(v, FieldType.NORMAL);
        }
        board.setBot(v);
        return actions;
    }

    /**
     * The bot moves forward one field in its current orientation.
     * Only works if the target field is normal, a coin or the start field
     * @return Actions that occured during execution
     */
    public Actions moveForward() {
        var destination = getPosition().add(board.getDirectionOfBot().vector());
        var actions = new Actions();
        try {
            if (board.get(destination).IS_WALKABLE) {
                actions.add(new Action(ActionType.MOVE, getPosition(), getDirection(), destination));
                actions.addAll(placeBot(destination));

            } else {
                if (board.get(destination).equals(FieldType.ABYSS)) {
                    return new Actions(
                            new Action(ActionType.MOVE, getPosition(), getDirection(), destination),
                            new Action(ActionType.FALL_INTO_ABYSS, destination, getDirection())
                    );
                }
                if (board.get(destination).equals(FieldType.WALL)) {
                    return new Actions(
                            new Action(ActionType.MOVE, getPosition(), getDirection(), destination),
                            new Action(ActionType.RUN_INTO_WALL, destination, getDirection())
                    );
                }

                return new Actions(
                        new Action(ActionType.MOVE, getPosition(), getDirection(), destination),
                        new Action(ActionType.RUN_INTO_WALL, destination, getDirection())
                );
            }
        } catch (NullPointerException e) {
            return new Actions(
                    new Action(ActionType.MOVE, getPosition(), getDirection(), destination),
                    new Action(ActionType.RUN_INTO_WALL, destination, getDirection())
            );
        }

        return actions;
    }

    /**
     * The bot rotates 90° to the left.
     * @return Actions that occured during execution
     */
    public Actions turnLeft() {
        board.setDirectionOfBot(board.getDirectionOfBot().rotate(-1));
        return new Actions(makeAction(ActionType.TURN_LEFT));
    }

    /**
     * The bot rotates 90° to the right.
     * @return Actions that occured during execution
     */
    public Actions turnRight() {
        board.setDirectionOfBot(board.getDirectionOfBot().rotate(1));
        return new Actions(makeAction(ActionType.TURN_RIGHT));
    }

    /**
     * The bot jumps in its current orientation over exactly one field with an abyss.
     * Only works if the field in front of the bot is a chasm and the field after it is normal,
     * a coin or the starting field.
     *
     * @return Actions that occured during execution
     */
    public Actions jump() {
        var destination = getPosition()
                .add(board
                        .getDirectionOfBot().vector()
                        .multiplyBy(JUMP_DISTANCE)
                );
        var actions = new Actions();

        var jumpOverPos = getPosition().add(board.getDirectionOfBot().vector());

        if (FieldType.isWalkable(board.get(destination))) {
            if (FieldType.isJumpable(board.get(jumpOverPos))) {
                actions.add(new Action(ActionType.JUMP, getPosition(), getDirection(), destination));
                actions.addAll(placeBot(destination));
            } else {
                error("Obstacle in the way: %s: %s%n", jumpOverPos, board.get(jumpOverPos));
                actions.add(makeAction(ActionType.RUN_INTO_WALL));
            }
        }


        return actions;
    }

    /**
     *  The door is opened and thus removed (the field becomes a normal field).
     *  Works only if the bot has correct alignment to the front of the door and
     *  no more coins are available.
     *
     * @return true, if exit was successful
     */
    public Actions exit() {
        boolean exited = board.get(getPosition().add(getDirection().vector())).equals(FieldType.DOOR);
        boolean coinsLeft = board.hasCoins();
        return (!coinsLeft && exited)
                ? new Actions(makeAction(ActionType.EXIT_SUCCESSFUL))
                : new Actions(makeAction(ActionType.EXIT_FAILED));
    }

    /**
     * Execute procedures that also contain recursive instructions (e.g. EXECUTE_P1).
     * @param root this procedure is executed first
     * @param p1 procedure for EXECUTE_P1
     * @param p2 procedure for EXECUTE_P2
     * @return Actions of executed procedures
     */
    public Actions execute(Procedure root, Procedure p1, Procedure p2)
            throws IllegalArgumentException, IllegalStateException {

        // null checks
        if (root == null) throw new IllegalArgumentException("Null is not allowed as an Argument for root");
        if (p1 == null) throw new IllegalArgumentException("Null is not allowed as an Argument for p1");
        if (p2 == null) throw new IllegalArgumentException("Null is not allowed as an Argument for p2");
        // calling itself
        if (Procedure.containIllegalRecursion(p1, p2)) throw new ProcedureInvalidRecursionException("p1/p2 contain illegal recursion");
        if (!Procedure.verify(root, p1, p2)) throw new ProcedureInvalidInstruction("procedures are incorrect");

        final Actions allActions = new Actions();
        int i = 0;
        while (!root.isEmpty() && !allActions.failed()) {
            var instruction = root.remove();
            switch (instruction) {
                case EXECUTE_P1 -> {
                    var actionsFromProcedure = new Actions();
                    actionsFromProcedure.add(new Action(START_EXECUTE_P1, 0, i));
                    actionsFromProcedure.addAll(executeRecursive(
                            new Procedure(1, p1),
                            new Procedure(2, p2)
                    ));
                    actionsFromProcedure.add(new Action(STOP_EXECUTE_P1, 0, i));
                    allActions.addAll(actionsFromProcedure);
                }
                case EXECUTE_P2 -> {

                    var actionsFromProcedure = new Actions();
                    actionsFromProcedure.add(new Action(START_EXECUTE_P2, 0, i));
                    actionsFromProcedure.addAll(executeRecursive(
                            new Procedure(2, p2),
                            new Procedure(1, p1)
                    ));
                    actionsFromProcedure.add(new Action(STOP_EXECUTE_P2, 0, i));
                    allActions.addAll(actionsFromProcedure);
                }
                default -> {
                    var actions = execute(instruction);
                    actions.forEach(action -> action.setProcedure(0));
                    int posInProcedure = i;
                    actions.forEach(action -> action.setInstruction(posInProcedure));
                    allActions.addAll(actions);
                }
            }

            ++i;
        }
        
        return allActions;
    }

    /**
     * This method is used to implement the recursive logic of the procedures as defined in the task.
     * @param current procedure that should be executed
     * @param other recursive procedure that should execute when recursion appears.
     * @return collection of actions that happened during execution.
     */
    private Actions executeRecursive(Procedure current, Procedure other) {
        var actionsFromProcedure = new Actions();

        var procedure = current.clone();
        for (int instrIndex = 0; !procedure.isEmpty() && !actionsFromProcedure.failed(); instrIndex++) {
            var subInstruction = procedure.remove();
            if (Instruction.isRecursionCall(subInstruction)) {
                actionsFromProcedure.add(
                        new Action(
                                current.getId() == 1 ? START_EXECUTE_P2 : START_EXECUTE_P1,
                                current.getId(),
                                instrIndex
                        ));
                actionsFromProcedure.addAll(executeRecursive(other, current));
                actionsFromProcedure.add(
                        new Action(
                                current.getId() == 1 ? STOP_EXECUTE_P2 : STOP_EXECUTE_P1,
                                current.getId(),
                                instrIndex
                        ));
            } else {
                var actionsFromInstruction = execute(subInstruction);
                int instr = instrIndex;
                actionsFromInstruction.forEach(action -> action.setInstruction(instr));
                actionsFromInstruction.forEach(action -> action.setProcedure(current.getId()));
                actionsFromProcedure.addAll(actionsFromInstruction);
            }
        }

        return actionsFromProcedure;
    }

    /**
     * Execute a single instruction on this bot.
     * Recursive Instructions (e.g. EXECUTE_P1) are not allowed for this method.
     * Use {@link #execute(Procedure, Procedure, Procedure)} to execute recursive Instructions.
     *
     * @param instruction Instruction for Bot
     * @throws IllegalArgumentException if recursive Instruction (e.g. EXECUTE_P1) is given
     * @throws IllegalStateException if an unhandled Instruction is given
     * @return Actions of executed instruction
     */
    public Actions execute(Instruction instruction)
            throws  IllegalArgumentException,
                    IllegalStateException {

        return new Actions(switch (instruction) {
            case FORWARD -> this.moveForward();
            case TURN_LEFT -> this.turnLeft();
            case TURN_RIGHT -> this.turnRight();
            case JUMP -> this.jump();
            case EXIT -> this.exit();
            case EXECUTE_P1, EXECUTE_P2 -> throw new IllegalArgumentException("Recursive procedures are not allowed in this method");
            default -> throw new IllegalStateException("Unexpected value: " + instruction);
        });
    }

    /**
     * Action factory method.
     * @param actionType type of action
     * @param destination end position
     * @return new action
     */
    private Action makeAction(ActionType actionType, Vector destination) {
        return new Action(actionType, getPosition(), getDirection(), destination);
    }

    /**
     * Action factory method
     * @param actionType type of action
     * @return new action
     */
    private Action makeAction(ActionType actionType) {
        return new Action(actionType, getPosition(), getDirection(), null);
    }
}
