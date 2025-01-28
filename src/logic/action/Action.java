package logic.action;

import logic.Direction;
import logic.util.Vector;

/**
 * The type Action.
 */
public class Action {
    /**
     * What action happened?
     */
    private final ActionType type;
    /**
     * Where did it happen?
     */
    private final Vector position;
    /**
     * (Optional) Where did it end?
     */
    private final Vector destination;
    /**
     * In what direction did the character look
     */
    private final Direction direction;
    /**
     * This action happened while executing what procedure?
     * -1 means no procedure is specified
     */
    private int procedure = -1;
    /**
     * This action happened while executing what instruction?
     * -1 means no instruction is specified
     */
    private int instruction = -1;

    /**
     * Instantiates a new Action.
     *
     * @param type        the type
     * @param position    the position
     * @param direction   the direction
     * @param destination the destination
     */
    public Action(ActionType type, Vector position, Direction direction, Vector destination) {
        this.type = type;
        this.position = position;
        this.direction = direction;
        this.destination = destination;
    }

    /**
     * Instantiates a new Action.
     *
     * @param type      the type
     * @param position  the position
     * @param direction the direction
     */
    public Action(ActionType type, Vector position, Direction direction) {
        this(type, position, direction, null);
    }

    /**
     *
     * @param type          the type
     * @param procedure     procedure
     * @param instruction   instruction
     */
    public Action(ActionType type, int procedure, int instruction) {
        this(type, null, null);
        this.procedure = procedure;
        this.instruction = instruction;
    }

    /**
     * Failed boolean.
     *
     * @return the boolean
     */
    public boolean failed() {
        return type.failed();
    }

    /**
     * Gets type.
     *
     * @return the type
     */
    public ActionType getType() {
        return type;
    }

    /**
     * Gets position.
     *
     * @return the position
     */
    public Vector getPosition() {
        return position;
    }

    /**
     * Gets direction.
     *
     * @return the direction
     */
    public Direction getDirection() {
        return direction;
    }

    /**
     * Gets instruction.
     *
     * @return the instruction
     */
    public int getInstruction() {
        return instruction;
    }

    /**
     * Sets instruction.
     *
     * @param instruction the instruction
     */
    public void setInstruction(int instruction) {
        this.instruction = instruction;
    }

    /**
     * Gets procedure.
     *
     * @return the procedure
     */
    public int getProcedure() {
        return procedure;
    }

    /**
     * Sets procedure.
     *
     * @param procedure the procedure
     */
    public void setProcedure(int procedure) {
        this.procedure = procedure;
    }

    /**
     * Gets destination.
     *
     * @return the destination
     */
    public Vector getDestination() {
        return destination == null ? position : destination;
    }

    @Override
    public String toString() {
        return String.format(
                "Action[procedure=%d, instruction=%d, type=%s, direction=%s, position=%s, destination=%s]",
                procedure,
                instruction,
                type,
                direction,
                position,
                destination);
    }


    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Action other) {
            return this.type.equals(other.type)
                    && this.procedure == other.procedure
                    && this.instruction == other.instruction;
        } else if (obj instanceof ActionType type) {
            return this.type.equals(type);
        } else {
            return false;
        }
    }
}
