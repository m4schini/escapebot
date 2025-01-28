package logic.board;

import com.google.gson.*;
import com.google.gson.annotations.SerializedName;
import logic.Direction;
import logic.exception.validation.MissingKeyException;
import logic.util.Vector;

import java.io.Reader;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.Field;
import java.lang.reflect.Type;

/**
 * The type Game level.
 *
 */
public class GameLevel {
    /**
     * Empty level
     */
    public static final GameLevel EMPTY_LEVEL = GameLevel.fromJson("{\"name\": \"Leeres Level\",\"field\":[" +
            "[4, 5, 5, 5, 5, 5, 5, 5],[5, 5, 5, 5, 5, 5, 5, 5],[5, 5, 5, 5, 5, 5, 5, 5],[5, 5, 5, 5, 5, 5, 5, 5]," +
            "[5, 5, 5, 5, 5, 5, 5, 5],[5, 5, 5, 5, 5, 5, 5, 5],[5, 5, 5, 5, 5, 5, 5, 5],[5, 5, 5, 5, 5, 5, 5, 5]]," +
            "\"botRotation\": 1}");

    /**
     * Optional name of level
     */
    private String name;

    /**
     * field data of level
     */
    @Required
    @SerializedName("field")
    private final Board board;

    /**
     * Start direction of level
     */
    @Required
    @SerializedName("botRotation")
    private final Direction botDirection;

    /**
     * Instantiates a new Game level.
     *
     * @param board        the board
     * @param botDirection the bot direction
     */
    public GameLevel(Board board, Direction botDirection) {
        this.board = board;
        this.board.setDirectionOfBot(botDirection);
        this.botDirection = botDirection;
    }

    /**
     * Instantiates a new Game level.
     * @param board the board
     */
    public GameLevel(Board board) {
        this(board, board.getDirectionOfBot());
    }

    /**
     * Gets name.
     *
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * Gets name.
     *
     * @param defaultName the default name
     * @return the name
     */
    public String getName(String defaultName) {
        var name = getName();
        return name != null ? name : defaultName;
    }

    /**
     * Sets name.
     *
     * @param name the name
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Gets board.
     *
     * @return the board
     */
    public Board getBoard() {
        var board = new Board(this.board);
        board.setDirectionOfBot(getStartBotDirection());
        return board;
    }

    /**
     * Gets start bot direction.
     *
     * @return the start bot direction
     */
    public Direction getStartBotDirection() {
        return this.botDirection;
    }

    /**
     * Gets start bot position.
     *
     * @return the start bot position
     */
    public Vector getStartBotPosition() {
        return this.board.getStartPosition();
    }

    /**
     * To json string.
     *
     * @return the string
     */
    public String toJson() {
        return new GsonBuilder().setPrettyPrinting().create().toJson(this);
    }

    @Override
    public String toString() {
        return String.format("GameLevel[name=%s, startPosition=%s, startDirection=%s]",
                getName(),
                getStartBotPosition(),
                getStartBotDirection());
    }

    /**
     * From json game level.
     *
     * @param jsonInput the json input
     * @return the game level
     */
    public static GameLevel fromJson(String jsonInput) {
        var gson = new GsonBuilder()
                .registerTypeAdapter(GameLevel.class, new GameLevelDeserializer())
                .create();
        return gson.fromJson(jsonInput, GameLevel.class);
    }

    /**
     * From json game level.
     *
     * @param reader the reader
     * @return the game level
     */
    public static GameLevel fromJson(Reader reader) {
        var gson = new GsonBuilder()
                .registerTypeAdapter(GameLevel.class, new GameLevelDeserializer())
                .create();
        return gson.fromJson(reader, GameLevel.class);
    }

    /**
     * Use this Annotation to mark fields as "required".
     * If you then want to deserialize into that class, the strict deserializer will check if the fields exists,
     * and abort if it doesn't;
     */
    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.FIELD)
    private @interface Required {}

    /**
     * Used to deserialize gamelevel json. Injects checking for required attributes
     */
    private static class GameLevelDeserializer implements JsonDeserializer<GameLevel> {
        @Override
        public GameLevel deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
            GameLevel pojo = new Gson().fromJson(json, typeOfT);

            // check for required fields
            for (Field f : pojo.getClass().getDeclaredFields()) {
                if (f.getAnnotation(Required.class) != null) {
                    // use value of {@code serializedName} from Gson, if available
                    String name = f.getAnnotation(SerializedName.class) != null
                            ? f.getAnnotation(SerializedName.class).value()
                            : f.getName();

                    if (json.getAsJsonObject().get(name) == null)
                        throw new MissingKeyException(name);
                }
            }

            return pojo;
        }
    }
}
