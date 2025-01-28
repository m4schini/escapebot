package gui;

import com.google.gson.JsonSyntaxException;
import gui.components.editor.MapEditor;
import gui.components.game.GameGrid;
import gui.components.TextureGrid;
import gui.components.game.GameInfoBar;
import gui.components.game.ProcedureEditor;
import gui.components.game.ProcedureGrid;
import gui.shell.ShellController;
import javafx.beans.property.*;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import logic.*;
import logic.action.ActionType;
import logic.action.Actions;
import logic.board.GameLevel;
import logic.exception.ProcedureInvalidInstruction;
import logic.exception.ProcedureInvalidRecursionException;
import logic.exception.validation.MissingFieldException;
import logic.exception.validation.MissingKeyException;
import logic.exception.validation.UnexpectedTypeException;
import logic.exception.validation.ValidationException;
import logic.util.Log;

import java.io.FileNotFoundException;

import static logic.util.Log.*;

/**
 * JavaFX-implementation of GUIConnector. Used by logic to communicate with UI.
 *
 * @author orcid.org/0009-0008-3023-1228 (inf104662)
 */
public class FxUserInterface implements GUIConnector {

    // EXCEPTION MESSAGES
    // io
    public static final String EX_HEAD_IO = "Something went wrong while reading the file";
    public static final String EX_MSG_FILE_NOT_FOUND = "File doesn't exist";
    public static final String EX_MSG_WRONG_FILE_FORMAT = "Selected file is not a json file. Level files have to be json";

    // instructions
    public static final String EX_HEAD_INVALID_INSTRUCTION = "Procedures are invalid";
    public static final String EX_MSG_INVALID_INSTRUCTION = "Procedures are invalid. Did you maybe forgot to add an Exit instruction?";
    public static final String EX_MSG_ILLEGAL_RECURSION = "The procedures contain a illegal recursion. Pls check again and make sure that there are no infinite Loops";

    // Corrupted File
    public static final String EX_HEAD_CORRUPTED_FILE = "Corrupted File";
    public static final String EX_MSG_MISSING_KEY = "The level file is missing required data: \n'%s'";
    public static final String EX_MSG_MISSING_FIELDTYPE = "Board of level file is missing required field: '%s'";
    public static final String EX_MSG_ILLEGAL_CHARACTER = "While reading the File we expected '%s' \nbut found '%s'";
    public static final String EX_MSG_CORRUPTED_DATA = "The level file has corrupted data";

    // GAME LOSS MESSAGES
    public static final String GAME_LOSE_MSG_EXIT_FAILED = "You didn't make it to the End";
    public static final String GAME_LOSE_MSG_RUN_INTO_WALL = "Your bot has run into a wall!";
    public static final String GAME_LOSE_MSG_FALLEN_INTO_ABYSS = "You bot has fallen into an endless abyss!";

    // STATIC PROPERTIES
    /**
     * Height Property of primary stage.
     */
    public static ReadOnlyDoubleProperty WINDOW_HEIGHT;
    /**
     * Width Property of primary stage.
     */
    public static ReadOnlyDoubleProperty WINDOW_WIDTH;
    /**
     * Animation multiplication modifier
     */
    public final static IntegerProperty ANIMATION_DURATION_MODIFIER = new SimpleIntegerProperty(2);
    /**
     * Standard duration of animation
     */
    public final static IntegerProperty ANIMATION_STD_DURATION = new SimpleIntegerProperty(200);

    /**
     * The Board.
     */
    private TextureGrid textureGrid;

    /**
     * Procedure editor (besides game board)
     */
    private final ProcedureEditor procedureEditor;

    /**
     * Game info bar (under game board)
     */
    private final GameInfoBar infoBar;

    /**
     * Controller of shell
     */
    private final ShellController shell;

    /**
     * The Main Procedure
     */
    private ProcedureGrid procedure0;
    /**
     * The Procedure 1.
     */
    private final ProcedureGrid procedure1;
    /**
     * The Procedure 2.
     */
    private final ProcedureGrid procedure2;

    /**
     * Instantiates a new Fx user interface.
     *
     * @param shell reference to shellcontroller
     * @param infoBar reference to game info bar
     * @param procedureEditor reference to procedureEditor
     * @param procedure0 reference to first procedure grid
     * @param procedure1 reference to second procedure grid
     * @param procedure2 reference to third procedure grid
     */
    public FxUserInterface(
            ShellController shell,
            GameInfoBar infoBar,
            ProcedureEditor procedureEditor,
            ProcedureGrid procedure0,
            ProcedureGrid procedure1,
            ProcedureGrid procedure2) {
        this.shell = shell;
        this.infoBar = infoBar;
        this.procedureEditor = procedureEditor;
        this.procedure0 = procedure0;
        this.procedure1 = procedure1;
        this.procedure2 = procedure2;
    }

    /**
     * Sets game field.
     *
     * @param field the field
     */
    public void setGameField(TextureGrid field) {
        this.textureGrid = field;
    }



    @Override
    public void onLogicInitialized(GameLevel data) {
        shell.setGrid(new GameGrid(data));
        textureGrid.setBoard(data.getBoard());
        var pos = data.getBoard().getStartPosition();
        if (textureGrid instanceof GameGrid gg)
            gg.placeBot(pos.X(),pos.Y(), data.getStartBotDirection().ordinal());
        else
            throw new IllegalStateException("grid has to be gameboard");
    }

    @Override
    public void onGameWin() {
        debug("onGameWin triggered");

        infoBar.showWin();
    }

    @Override
    public void onGameLose() {
        debug("onGameLose triggered");
        if (textureGrid instanceof GameGrid gg) {
            gg.reset();
        }

        infoBar.showLose();
    }

    @Override
    public void onGameLose(String reasonForLoss) {
        debug("onGameLose(String) triggered");
        if (textureGrid instanceof GameGrid gg) {
            gg.reset();
        }

        infoBar.showLose(reasonForLoss);
    }

    @Override
    public void play(Actions actions) {
        if (!(textureGrid instanceof GameGrid grid)) throw new IllegalStateException("gamegrid is needed to play animations");
        shell.disableSidebar();

        infoBar.showRunning();
        var currentPlayingAction = grid.animateBot(actions, () -> {
            if (actions.successful()) {
                onGameWin();
            } else {
                switch (actions.getFailed().getType()) {
                    case FALL_INTO_ABYSS -> onGameLose(GAME_LOSE_MSG_FALLEN_INTO_ABYSS);
                    case RUN_INTO_WALL -> onGameLose(GAME_LOSE_MSG_RUN_INTO_WALL);
                    case EXIT_FAILED -> onGameLose(GAME_LOSE_MSG_EXIT_FAILED);
                    default -> onGameLose();
                }

            }
        });

        currentPlayingAction.addListener((observable, before, action) -> {
            if (action != null && action.getProcedure() >= 0 && action.getInstruction() >= 0) {
                debug("Selecting instruction[%d] in procedure[%d]\n", action.getInstruction(), action.getProcedure());

                // STOP EXECUTE PX actions are meta actions to clear highlight in grids
                if (action.getType() == ActionType.STOP_EXECUTE_P1) {
                    procedure1.clearHighlight();
                } else if (action.getType() == ActionType.STOP_EXECUTE_P2) {
                    procedure2.clearHighlight();
                } else {
                    procedureEditor.highlightInstruction(action.getProcedure(), action.getInstruction());
                }
            }
        });

        infoBar.setOnStopGame(actionEvent -> {
            grid.stopBotAnimation();
            shell.initializeLogic();
        });

    }

    @Override
    public void panic(Exception exception) {
        if (exception instanceof FileNotFoundException) {
            alertError(EX_HEAD_IO, EX_MSG_FILE_NOT_FOUND);
        } else if (exception instanceof JsonSyntaxException) {
            alertError(EX_HEAD_IO, EX_MSG_WRONG_FILE_FORMAT);
        } else if (exception instanceof ProcedureInvalidInstruction) {
            alertError(EX_HEAD_INVALID_INSTRUCTION, EX_MSG_INVALID_INSTRUCTION);
        } else if (exception instanceof ProcedureInvalidRecursionException) {
            alertError(EX_HEAD_INVALID_INSTRUCTION, EX_MSG_ILLEGAL_RECURSION);
        } else if (exception instanceof MissingKeyException e) {
            alertError(EX_HEAD_CORRUPTED_FILE,
                    String.format(EX_MSG_MISSING_KEY,
                            e.getMissingKey()
                    ));
        } else if (exception instanceof MissingFieldException e) {
            alertError(EX_HEAD_CORRUPTED_FILE,
                    String.format(EX_MSG_MISSING_FIELDTYPE,
                            e.getMissingFieldType()
                    ));
        } else if (exception instanceof UnexpectedTypeException e) {
            alertError(EX_HEAD_CORRUPTED_FILE,
                    String.format(EX_MSG_ILLEGAL_CHARACTER,
                            e.getExpectedType(),
                            e.getActualType()
                    ));
        } else if (exception instanceof ValidationException) {
            alertError(EX_HEAD_CORRUPTED_FILE, EX_MSG_CORRUPTED_DATA);
        } else {
            Log.warning("UNEXPECTED EXCEPTION PANIC: %s\n", exception);
            alertError(
                    "Something went wrong",
                    String.format("%s%n%s%n%s%n%s",
                            String.format("CLASSNAME: \"%s\"", exception.getClass().descriptorString()),
                            exception,
                            exception.getMessage(),
                            exception.getCause())
            );
        }
        Log.error(exception);
    }


    /**
     * Alert error.
     *
     * @param title       the title
     * @param description the description
     */
    public static void alertError(String title, String description ) {
        Alert alert = makeAlert(description, title, "Something bad happened :(", Alert.AlertType.ERROR);
        alert.showAndWait();
    }

    /**
     * Initiates a new Alert
     * @param description description of alert
     * @param header header text of alert
     * @param title title of alert
     * @param type type of alert
     * @return new alert instance
     */
    private static Alert makeAlert(String description, String header, String title, Alert.AlertType type) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.setContentText(description);

        return alert;
    }
}
