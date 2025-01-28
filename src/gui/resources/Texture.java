package gui.resources;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import logic.Direction;
import logic.board.FieldType;
import logic.procedure.Instruction;
import logic.util.Log;

import java.util.Arrays;
import java.util.Locale;

import static logic.util.Log.*;

/**
 * This enum manages all used texture files.
 *
 * @apiNote To switch between themes, one has to set the environment var "THEME" to "classic" or "dungeon".
 *          If var is not set, it defaults to "dungeon".
 * @see <a href="https://0x72.itch.io/dungeontileset-ii">dungeon textures by "0x72"</a>
 * @author orcid.org/0009-0008-3023-1228 (inf104662)
 */
public enum Texture {
    FORWARD     ("AGehen.png",     true,  60,  60),
    TURN_LEFT   ("ALinks.png",     true,  60,  60),
    TURN_RIGHT  ("ARechts.png",    true,  60,  60),
    JUMP        ("ASpringen.png",  true,  60,  60),
    EXIT        ("AExit.png",      true,  60,  60),
    EXECUTE_P1  ("AProzedur1.png", true,  60,  60),
    EXECUTE_P2  ("AProzedur2.png", true,  60,  60),
    ABYSS       ("FAbgrund.png",   false, 60,  60),
    WALL        ("FMauer.png",     false, 60,  60),
    COIN        ("FMünze.png",     false, 60,  60),
    NORMAL      ("FNormal.png",    false, 60,  60),
    START       ("FStart_East.png",false, 60,  60),
    DOOR        ("FTür.png",       false, 60,  60),
    BOT         ("FBot.png",       false, 300, 277),

    START_NORTH ("FStart_North.png", false, 320, 320),
    START_EAST  ("FStart_East.png",  false, 320, 320),
    START_SOUTH ("FStart_South.png", false, 320, 320),
    START_WEST  ("FStart_West.png",  false, 320, 320);

    public static final String STYLESHEET_LOCATION = Texture.class
            .getResource("resources/style.css")
            .toExternalForm();

    private final Image image;
    private final boolean isInstruction;

    Texture(String fileName, boolean isInstruction, int width, int height) {
        String PATH_TO_DUNGEON = "resources/dungeon/";
        String PATH_TO_CLASSIC = "resources/classic/";

        // if the "THEME" environment var is set to "classic", the textures given by the university are used
        // else the dungeon tileset
        var res = getClass().getResourceAsStream(
                System.getenv("THEME") != null &&
                System.getenv("THEME").toLowerCase(Locale.ROOT).equals("classic")
                        ? PATH_TO_CLASSIC + fileName
                        : PATH_TO_DUNGEON + fileName);
        assert res != null;
        // image with deactivated anti aliasing
        this.image = new Image(res, width, height, true, false);

        this.isInstruction = isInstruction;

        // Printing on error channel to be more obvious
        debug("LOADED TEXTURE FOR: " + name());
    }

    /**
     * Get texture associated to obj
     *
     * @param obj the obj
     * @return the texture
     * @throws IllegalArgumentException the illegal argument exception
     */
    public static Texture of(Object obj) throws IllegalArgumentException {
        if (obj instanceof Instruction) {
            return of(((Instruction) obj));
        } else if (obj instanceof FieldType) {
            return of(((FieldType) obj));
        } else {
            throw new IllegalArgumentException("no texture associated");
        }
    }

    /**
     * Get texture associated to instruction
     *
     * @param instruction the instruction
     * @return the texture
     * @throws IllegalArgumentException the illegal argument exception
     */
    public static Texture of(Instruction instruction) throws IllegalArgumentException {

        Texture t = valueOf(instruction.name());
        if (!t.isInstruction) throw new IllegalArgumentException("not a instruction: " + instruction);

        return t;
    }

    public static Texture of(FieldType fieldtype, Direction direction) {
        if (fieldtype != FieldType.START || direction == null) return of(fieldtype);

        return switch (direction) {
            case NORTH -> START_NORTH;
            case EAST -> START_EAST;
            case SOUTH -> START_SOUTH;
            case WEST -> START_WEST;
        };
    }

    /**
     * Get texture associated to fieldtype
     *
     * @param fieldtype the fieldtype
     * @return the texture
     * @throws IllegalArgumentException the illegal argument exception
     */
    public static Texture of(FieldType fieldtype) throws IllegalArgumentException {
        Texture t = valueOf(fieldtype.name());
        if (t.isInstruction) throw new IllegalArgumentException("not a field: " + fieldtype);

        return t;
    }

    /**
     * Return {@link javafx.scene.image.Image} of Texture
     *
     * @return FX Image
     */
    public Image Image() {
        return image;
    }

    public ImageView ImageView() {
        var imageView = new ImageView(this.Image());
        imageView.setPreserveRatio(true);
        return imageView;
    }

    /**
     * Returns new imageview of image of texture
     *
     * @return new ImageView
     */
    public ImageView ImageView(int size) {
        var imageView = new ImageView(this.Image());
        imageView.setPreserveRatio(true);
        imageView.setFitHeight(size);
        imageView.setFitWidth(size);
        return imageView;
    }

    /**
     * All textures that belong to an instruction
     *
     * @return array of textures
     */
    public static Texture[] instructions() {
        return Arrays.stream(values())
                .filter(texture -> texture.isInstruction)
                .toArray(Texture[]::new);
    }

    /**
     * All textures that belong to map tiles
     *
     * @return array of textures
     */
    public static Texture[] mapTiles() {
        return Arrays.stream(values())
                .filter(texture -> !texture.isInstruction)
                .toArray(Texture[]::new);
    }
}
