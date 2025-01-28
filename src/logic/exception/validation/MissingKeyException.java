package logic.exception.validation;

/**
 * The type Missing key exception.
 */
public class MissingKeyException extends ValidationException {
    /**
     * The Key.
     */
    String key;

    /**
     * Instantiates a new Missing key exception.
     *
     * @param key the key
     */
    public MissingKeyException(String key) {
        super("JSON was missing required key: " + key);
        this.key = key;
    }

    /**
     * Gets missing key.
     *
     * @return the missing key
     */
    public String getMissingKey() {
        return key;
    }
}
