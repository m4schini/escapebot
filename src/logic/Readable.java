package logic;

@FunctionalInterface
public interface Readable {
    /**
     * Returns all metadata of object as human-readable String
     * @return human-readable info string
     */
    String asReadableString();
}
