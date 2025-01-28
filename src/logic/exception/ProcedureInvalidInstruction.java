package logic.exception;

public class ProcedureInvalidInstruction extends IllegalStateException {
    public ProcedureInvalidInstruction(String message) {
        super(message);
    }
}
