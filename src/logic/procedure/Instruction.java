package logic.procedure;

import logic.Readable;

/**
 * The enum Instruction.
 */
public enum Instruction implements Readable {
    /**
     * Forward instruction.
     */
    FORWARD(false,
            """
                    The bot moves forward one field in its current orientation.
                    Only works if the target field is normal, a coin or the start field.
                    """),
    /**
     * Turn left instruction.
     */
    TURN_LEFT(false,
            "The bot rotates 90° to the left."),
    /**
     * Turn right instruction.
     */
    TURN_RIGHT(false,
            "The bot rotates 90° to the right."),
    /**
     * Jump instruction.
     */
    JUMP(false,
            """
                    The bot jumps in its current orientation over exactly one field.
                    Only works if the field in front of the bot is an abyss and the field after it is normal,
                    a coin or the starting field."""),
    /**
     * Exit instruction.
     */
    EXIT(false,
            """
                    Procedure 1 is called and all instructions in it are executed.
                    After the procedure has been processed, the caller (program or procedure 2)
                    continues with the next instruction. A procedure may not call itself. Likewise,
                    the two procedures may not call each other (endless recursion).
                    """),
    /**
     * Execute child procedure 1.
     */
    EXECUTE_P1(true,
            """
                    Procedure 2 is called and all instructions in it are executed.
                    After the procedure has been processed, the caller (program or procedure 1)
                    continues with the next instruction. A procedure may not call itself. Likewise,
                    the two procedures may not call each other (endless recursion).
                    """),
    /**
     * Execute child procedure 2.
     */
    EXECUTE_P2(true,
            """
                    The door is opened and thus removed (the field becomes a normal field).
                    Only works if the bot is directly in front of the door in the correct
                    orientation and there are no more coins. If further instructions are present
                    after this instruction, the solution is not valid.
                    """);

    /**
     * true, if this Instruction is intended to call another instruction
     */
    private final boolean isRecursionCall;

    /**
     * description instruction
     */
    private final String description;

    Instruction(boolean isRecursionCall, String description) {
        this.isRecursionCall = isRecursionCall;
        this.description = description;
    }

    /**
     * Safe method to check if Instruction executes other procedure
     *
     * @param instruction instruction that should be checked
     * @return true, if instruction is a recursion call
     */
    public static boolean isRecursionCall(Instruction instruction) {
        return instruction != null && (instruction.isRecursionCall);
    }

    /**
     * Gets the EXECUTE Instruction corresponding to i
     * @param i number of recursive instruction
     * @return return matching recursive instruction
     */
    public static Instruction getRecursive(int i) {
        return switch (i) {
            case 1 -> EXECUTE_P1;
            case 2 -> EXECUTE_P2;
            default -> throw new UnsupportedOperationException("doesnt exist");
        };
    }

    @Override
    public String asReadableString() {
        return this.name() + "\n" + description;
    }
}
