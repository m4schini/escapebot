package logic.board;

import com.google.gson.TypeAdapter;
import com.google.gson.annotations.JsonAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import logic.Readable;
import logic.exception.validation.ValidationException;
import logic.exception.validation.UnexpectedTypeException;

import java.io.IOException;

/**
 * The enum FieldType.
 *
 * @author orcid.org/0009-0008-3023-1228 (inf104662)
 */
@JsonAdapter(FieldType.JsonAdapter.class)
public enum FieldType implements Readable {

    /**
     * Abyss.
     *
     * Cannot be entered by the bot.
     * However, a a exactly one hex wide can be jumped over.
     */
    ABYSS(false,
            true,
            """
                    Cannot be entered by the bot.
                    However, a abyss that is exactly one field wide can be jumped over.
                    """),

    /**
     * Coins can be collected by the bot, the field becomes a "normal" field.
     * Only when all coins have been collected, the door can be opened.
     */
    COIN(true,
            false,
            """
                    Coins can be collected by the bot, the field becomes a "normal" field.
                    Only when all coins have been collected, the door can be opened.
                    """),

    /**
     * When the door is opened, the level is won.
     * There must be exactly one door per level.
     */
    DOOR(true,
            false,
            """
                    When the door is opened, the level is won.
                    There must be exactly one door per level.
                    """),

    /**
     * A simple, empty field.
     */
    NORMAL(true,
            false,
            "A simple, empty field."),

    /**
     * On the start field the bot starts the level in an orientation to be defined per level.
     * There must be exactly one start field per level.
     */
    START(true,
            false,
            """
                    On the start field the bot starts the level in an orientation to be defined per level.
                    There must be exactly one start field per level.
                    """),

    /**
     * A field that cannot be entered.
     */
    WALL(false,
            false,
            "A field that cannot be entered.");

    /**
     * The character is allowed to walk over this field
     */
    public final boolean IS_WALKABLE;
    /**
     * The character is allowed to jump over this field
     */
    public final boolean IS_JUMPABLE;
    /**
     * description of field type
     */
    private final String description;

    /**
     * @param isWalkable if character is allowed to <b>walk</b> over this field
     * @param isJumpable if character is allowed to <b>jump</b> over this field
     * @param description description of fieldType
     */
    FieldType(boolean isWalkable, boolean isJumpable, String description) {
        this.IS_WALKABLE = isWalkable;
        this.IS_JUMPABLE = isJumpable;
        this.description = description;
    }

    /**
     * Safe way to check the IS_JUMPABLE property, without the risk of producing null pointers
     * @param fieldType fieldType you want to check
     * @return false if null, else IS_JUMPABLE
     */
    public static boolean isJumpable(FieldType fieldType) {
        if (fieldType == null) return false;
        return fieldType.IS_JUMPABLE;
    }

    /**
     * Safe way to check the IS_WALKABLE property, without the risk of producing null pointers
     * @param fieldType fieldType you want to check
     * @return false if null, else IS_WALKABLE
     */
    public static boolean isWalkable(FieldType fieldType) {
        if (fieldType == null) return false;
        return fieldType.IS_WALKABLE;
    }

    /**
     * From ordinal fieldtype.
     *
     * @param ordinal the ordinal
     * @return the fieldtype
     */
    public static FieldType fromOrdinal(int ordinal) {
        var values = values();
        if (ordinal < 0 || ordinal >= values.length) throw new IllegalArgumentException("Doesn't Exist");
        return values[ordinal];
    }

    @Override
    public String asReadableString() {
        return this.name() + "\n" + description;
    }

    /**
     * This adapter converts field type values to ordinals instead of string names.
     *
     * Task definition states that the field type has to be saved as an integer
     */
    public static class JsonAdapter extends TypeAdapter<FieldType> {
        @Override
        public void write(JsonWriter out, FieldType value) throws IOException {
            out.value(value.ordinal());
        }

        @Override
        public FieldType read(JsonReader in) throws IOException {
            Integer ordinal = null;
            try {
                ordinal = in.nextInt();
                return fromOrdinal(ordinal);
            } catch (NumberFormatException e) {
                String type = e.getMessage().split(" ")[2];
                throw new UnexpectedTypeException(in.getPath(),
                        "int",
                        type.substring(0, type.length() - 1));
            } catch (IllegalArgumentException e) {
                throw new ValidationException(String.format("Mismatched Format: %s => %s", in.getPath(), ordinal));
            }
        }
    }
}
