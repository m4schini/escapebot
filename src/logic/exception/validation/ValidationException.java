package logic.exception.validation;

import com.google.gson.JsonParseException;

/**
 * The type Validation exception.
 *
 * @author orcid.org/0009-0008-3023-1228 (inf104662)
 */
public class ValidationException extends JsonParseException {
    /**
     * Instantiates a new Validation exception.
     *
     * @param message the message
     */
    public ValidationException(String message) {
        super("[VALIDATION] " + message);
    }
}
