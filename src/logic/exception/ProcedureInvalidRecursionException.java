package logic.exception;

public class ProcedureInvalidRecursionException extends IllegalStateException {
    public ProcedureInvalidRecursionException(String message) {
        super(message);
    }
}
