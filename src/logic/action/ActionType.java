package logic.action;

/**
 * The enum ActionType.
 *
 * @author orcid.org/0009-0008-3023-1228 (inf104662)
 */
public enum ActionType {
    /**
     * Move action.
     */
    MOVE(false),
    /**
     * Turn left action.
     */
    TURN_LEFT(false),
    /**
     * Turn right action.
     */
    TURN_RIGHT(false),
    /**
     * Jump action.
     */
    JUMP(false),
    /**
     * Run into wall action.
     */
    RUN_INTO_WALL(true),
    /**
     * Fall into abyss action.
     */
    FALL_INTO_ABYSS(true),
    /**
     * Collect coin action.
     */
    COLLECT_COIN(false),
    /**
     * Exit successful action.
     */
    EXIT_SUCCESSFUL(false),
    /**
     * Exit failed action.
     */
    EXIT_FAILED(true),
    /**
     * Start execute p 1 action type.
     */
    START_EXECUTE_P1(false),
    /**
     * Start execute p 2 action type.
     */
    START_EXECUTE_P2(false),
    /**
     * Stop execute p 1 action type.
     */
    STOP_EXECUTE_P1(false),
    /**
     * Stop execute p 2 action type.
     */
    STOP_EXECUTE_P2(false),
    /**
     * Start action.
     */
    START(false);


    /**
     * Block attribute
     */
    private final boolean failed;

    /**
     * ActionType represents an action that can happen during execution of procedures
     * @param failed true, if this action signals a failed action/instruction
     */
    ActionType(boolean failed) {
        this.failed = failed;
    }

    /**
     * A failed action indicates that something went wrong with the execution. Further execution should be stopped.
     *
     * @return true, if failed.
     */
    public boolean failed() {
        return failed;
    }
}
