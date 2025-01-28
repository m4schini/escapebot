package logic;

import logic.board.GameLevel;
import logic.exception.validation.ValidationException;
import logic.exception.validation.MissingKeyException;
import logic.exception.validation.UnexpectedTypeException;
import org.junit.Test;

public class GameServiceSerialisationTest {

    /**
     * Checks if exceptions are thrown during the deserialization
     */
    @Test
    public void correctFormat() {
        var gameDataAsJson = "{\"field\":[[4,5,3,3,3,5,5,5],[3,5,3,5,3,5,5,5],[3,5,3,5,3,5,5,5],[3,5,3,5,3,5,5,5],[3,5,3,5,3,5,5,5],[3,5,3,5,3,5,5,5],[3,5,3,5,3,5,5,5],[3,3,3,5,3,3,3,2]],\"botRotation\":2}";
        GameLevel.fromJson(gameDataAsJson);
    }

    @Test
    public void missingKey_field() throws Exception {
        try {
            var gameDataAsJson = "{\"botRotation\":2}";
            GameLevel.fromJson(gameDataAsJson);
        } catch (Exception e) {
            assert e instanceof MissingKeyException;
            assert ((MissingKeyException) e).getMissingKey().equals("field");
            return;
        }
        throw new Exception("Test shouldn't reach this line");
    }

    @Test
    public void missingKey_botRotation() throws Exception {
        try {
            var gameDataAsJson = "{\"field\":[[4,5,3,3,3,5,5,5],[3,5,3,5,3,5,5,5],[3,5,3,5,3,5,5,5],[3,5,3,5,3,5,5,5],[3,5,3,5,3,5,5,5],[3,5,3,5,3,5,5,5],[3,5,3,5,3,5,5,5],[3,3,3,5,3,3,3,2]]}";
            GameLevel.fromJson(gameDataAsJson);
        } catch (Exception e) {
            assert e instanceof MissingKeyException;
            assert ((MissingKeyException) e).getMissingKey().equals("botRotation");
            return;
        }
        throw new Exception("Test shouldn't reach this line");
    }

    @Test(expected = UnexpectedTypeException.class)
    public void field_NaN() {
        var gameDataAsJson = "{\"field\":[[\"NORMAL\",\"NORMAL\",\"NORMAL\",\"NORMAL\",\"NORMAL\",\"NORMAL\",\"NORMAL\",\"NORMAL\"],[3,5,3,5,3,5,5,5],[3,5,3,5,3,5,5,5],[3,5,3,5,3,5,5,5],[3,5,3,5,3,5,5,5],[3,5,3,5,3,5,5,5],[3,5,3,5,3,5,5,5],[3,3,3,5,3,3,3,2]],\"botRotation\":2}";
        GameLevel.fromJson(gameDataAsJson);
    }

    @Test(expected = ValidationException.class)
    public void field_wrongNumber() {
        var gameDataAsJson = "{\"field\":[[4,5,3,3,42,5,5,5],[3,5,3,5,3,5,5,5],[3,5,3,5,3,5,5,5],[3,5,3,5,3,5,5,5],[3,5,3,5,3,5,5,5],[3,5,3,5,3,5,5,5],[3,5,3,5,3,5,5,5],[3,3,3,5,3,3,3,2]],\"botRotation\":2}";
        GameLevel.fromJson(gameDataAsJson);
    }

    @Test(expected = UnexpectedTypeException.class)
    public void botRotation_NaN() {
        var gameDataAsJson = "{\"field\":[[4,5,3,3,3,5,5,5],[3,5,3,5,3,5,5,5],[3,5,3,5,3,5,5,5],[3,5,3,5,3,5,5,5],[3,5,3,5,3,5,5,5],[3,5,3,5,3,5,5,5],[3,5,3,5,3,5,5,5],[3,3,3,5,3,3,3,2]],\"botRotation\":\"NORTH\"}";
        GameLevel.fromJson(gameDataAsJson);
    }

    @Test(expected = ValidationException.class)
    public void botRotation_wrongNumber() {
        var gameDataAsJson = "{\"field\":[[4,5,3,3,3,5,5,5],[3,5,3,5,3,5,5,5],[3,5,3,5,3,5,5,5],[3,5,3,5,3,5,5,5],[3,5,3,5,3,5,5,5],[3,5,3,5,3,5,5,5],[3,5,3,5,3,5,5,5],[3,3,3,5,3,3,3,2]],\"botRotation\":42}";
        GameLevel.fromJson(gameDataAsJson);
    }
}
