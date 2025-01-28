package logic.exception.validation;

public class UnexpectedTypeException extends ValidationException {
    /**
     * The Field.
     */
    final String field;
    /**
     * The Expected type.
     */
    final String expectedType;
    /**
     * The Actual type.
     */
    final String actualType;

    /**
     * Instantiates a new Unexpected type.
     *
     * @param field        the field
     * @param expectedType the expected type
     * @param actualType   the actual type
     */
    public UnexpectedTypeException(String field, String expectedType, String actualType) {
        super(String.format("Expected \"%s\" for field \"%s\", got: \"%s\"", expectedType, field, actualType));
        this.field = field;
        this.expectedType = expectedType;
        this.actualType = actualType;
    }

    /**
     * Gets field.
     *
     * @return the field
     */
    public String getField() {
        return field;
    }

    /**
     * Gets expected type.
     *
     * @return the expected type
     */
    public String getExpectedType() {
        return expectedType;
    }

    /**
     * Gets actual type.
     *
     * @return the actual type
     */
    public String getActualType() {
        return actualType;
    }
}
