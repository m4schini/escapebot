package logic.exception.validation;

import logic.board.FieldType;

/**
 * The type Missing key exception.
 */
public class MissingFieldException extends ValidationException {
    /**
     * The Key.
     */
    FieldType fieldType;

    /**
     * Instantiates a new Missing field exception.
     *
     * @param fieldType the fieldType
     */
    public MissingFieldException(FieldType fieldType) {
        super("Board was missing required field: " + fieldType);
        this.fieldType = fieldType;
    }

    /**
     * Gets missing key.
     *
     * @return the missing key
     */
    public FieldType getMissingFieldType() {
        return fieldType;
    }
}
