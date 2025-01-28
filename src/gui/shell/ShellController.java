package gui.shell;

import gui.FxUserInterface;
import gui.components.*;
import gui.components.editor.EditorGrid;
import gui.components.editor.MapEditor;
import gui.components.game.GameGrid;
import gui.components.game.GameInfoBar;
import gui.components.modal.BoardProblemModal;
import gui.components.game.ProcedureEditor;
import gui.components.game.ProcedureGrid;
import gui.components.SwitchButton;
import gui.components.SwitchPane;
import javafx.beans.property.ReadOnlyDoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import logic.board.GameLevel;
import logic.GameLogic;
import logic.board.Board;
import logic.board.FieldType;
import logic.util.Log;

import java.io.*;
import java.net.URL;
import java.util.Objects;
import java.util.ResourceBundle;

import static logic.util.Log.*;

/**
 * The ShellController is the root component of the game.
 *
 * @author orcid.org/0009-0008-3023-1228 (inf104662)
 */
public class ShellController implements Initializable {
    /**
     * The constant GAME_HEIGHT.
     */
    public static ReadOnlyDoubleProperty GAME_HEIGHT;
    /**
     * The constant GAME_WIDTH.
     */
    public static ReadOnlyDoubleProperty GAME_WIDTH;


    /**
     * The Info bar.
     */
    @FXML
    private GameInfoBar infoBar;
    @FXML
    private ProcedureGrid procedure0;
    /**
     * The Procedure 1.
     */
    @FXML
    private ProcedureGrid procedure1;
    /**
     * The Procedure 2.
     */
    @FXML
    private ProcedureGrid procedure2;
    @FXML
    private ProcedureGrid cloneProcedure0;
    @FXML
    private ProcedureGrid cloneProcedure1;
    @FXML
    private ProcedureGrid cloneProcedure2;
    @FXML
    private VBox boxSidebar;
    @FXML
    private SwitchPane editorSwitchPanel;
    @FXML
    private ShellMenuBar menuBar;
    @FXML
    private SwitchButton btnEditor;

    /**
     * The Logic.
     */
    private GameLogic logic;
    /**
     * The current level;
     */
    private GameLevel currentLevel;
    /**
     * The Gui.
     */
    private FxUserInterface gui;

    @FXML
    private BorderPane paneStage;
    @FXML
    private ProcedureEditor procEditor;
    @FXML
    private MapEditor mapEditor;

    private TextureGrid currentGrid;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        var gameHeight = new SimpleDoubleProperty();
        //                  Window height   -       some overlapping stuff
        gameHeight.bind(FxUserInterface.WINDOW_HEIGHT.subtract(100));
        GAME_HEIGHT = gameHeight;


        var gameWidth = new SimpleDoubleProperty();
        //              Window Height       -       sidebar
        gameWidth.bind(FxUserInterface.WINDOW_WIDTH.subtract(boxSidebar.widthProperty()).subtract(8));
        GAME_WIDTH = gameWidth;

        // initialize ui
        gui = new FxUserInterface(this, infoBar, procEditor, procedure0, procedure1, procedure2);

        initializeLogic(GameLevel.fromJson(new InputStreamReader(
                        Objects.requireNonNull(
                        getClass().getResourceAsStream("/gui/resources/resources/level/level0.json")))));

        menuBar.setOnChooseLevel(this::initializeLogic);

        // editor game switch button
        btnEditor.prefWidthProperty().bind(((VBox) btnEditor.getParent()).widthProperty());
        VBox.setMargin(btnEditor, new Insets(8.0));

        cloneProcedure0.bindProcedure(procedure0.procedureProperty());
        cloneProcedure1.bindProcedure(procedure1.procedureProperty());
        cloneProcedure2.bindProcedure(procedure2.procedureProperty());
    }

    /**
     * Initializes new GameLogic and saves on logic property
     *
     * @param level path to level file
     */
    @FXML
    public void initializeLogic(GameLevel level) {
        setLevel(level);
        initializeLogic();
    }

    /**
     * Initialize logic.
     */
    public void initializeLogic() {
        enableSidebar();
        if (currentLevel != null) {
            if (currentGrid instanceof GameGrid grid) {
                grid.stopBotAnimation();
            }
            this.logic = new GameLogic(this.gui, currentLevel);
            infoBar.hide();
            procEditor.clearHighlights();
        } else {
            warning("tried to initialize game without a level");
        }
        btnSetGameMode();
    }

    /**
     * Execute procedures ("build" by user in procedureEditor) in gameLogic
     * @throws IllegalStateException if game logic is not initialized
     */
    @FXML
    private void btnStartGame() {
        assert logic != null;
        debug("[[[NEW GAME TRIGGERED]]]");
        initializeLogic();

        procEditor.clearHighlights();

        logic.execute(
                procEditor.getProcedure(0),
                procEditor.getProcedure(1),
                procEditor.getProcedure(2)
        );
    }

    @FXML
    private void btnLoadEmptyLevel() {
        initializeLogic(GameLevel.EMPTY_LEVEL);
    }

    /**
     * Initialize gamelogic with levelfile chosen by user
     * @param actionEvent event handler
     */
    @FXML
    private void btnLoadLevelFile(ActionEvent actionEvent) {
        try {
            File saveFile = dialogChooseJson(System.getProperty("user.dir"));
            if (saveFile != null) {
                try {
                    initializeLogic(GameLevel.fromJson(new FileReader(saveFile)));
                } catch (FileNotFoundException e) {
                    gui.panic(e);
                }
            } else {
                warning("User didn't select a file. No Level was loaded");
            }
        } catch (Exception e) {
            gui.panic(e);
        }
    }

    @FXML
    private void btnSaveLevelFile(ActionEvent actionEvent) {
        GameLevel level = new GameLevel(currentGrid.getBoard());
        var fileChooser = new FileChooser();
        fileChooser.setInitialDirectory(new File(System.getProperty("user.dir")));
        fileChooser.setTitle("Save Level");

        var saveFile = fileChooser.showSaveDialog(currentGrid.getScene().getWindow());
        if (saveFile != null) {
            try {
                if (!saveFile.exists()) saveFile.createNewFile();

                if (saveFile.canWrite()) {
                    try (FileWriter writer = new FileWriter(saveFile)) {
                        writer.write(level.toJson());
                    }
                } else {
                    Log.error("saveFile not writable (writable:%b, exists:%b)\n", saveFile.canWrite(), saveFile.exists());
                }
            } catch (IOException e) {
                Log.error(e);
            }
        } else {
            Log.warning("User didn't select a file. Level is not saved");
        }
    }

    /**
     * Switch to MapEditor mode
     */
    @FXML
    private void btnSetEditorMode() {
        editorSwitchPanel.showBack();
        var eg = new EditorGrid(logic.getBoard(), mapEditor.SelectedProperty());
        mapEditor.setGrid(eg);
        setGrid(eg);
    }

    @FXML
    private void onCloseEditorMode() {
        debug("closeMapEditor called");
        var board = currentGrid.getBoard();
        var analysis = board.analyze();
        initializeLogic(new GameLevel(board, board.getDirectionOfBot()));
        if (!analysis.isEmpty()) {
            new BoardProblemModal(new Board.Problem(Board.ProblemType.SOLUTION_TOO_BIG, FieldType.START, null))
                    .show();
        }
    }

    /**
     * Switch to Game mode
     */
    @FXML
    private void btnSetGameMode() {
        debug("openProcedureEditor called");

        editorSwitchPanel.showFront();
        setGrid(new GameGrid(logic.getLevel()));
    }

    @FXML
    private void btnClearProcedures() {
        debug("clearProcedures called");
        procEditor.clearGrids();
    }

    @FXML
    private void btnSolveLevel() {
        debug("solveLevel called");

        var board = mapEditor.getBoard();
        var analysis = board.analyze();

        procEditor.clearGrids();
        if (analysis.isEmpty()) {
            var solution = board.solve();

            procedure0.setProcedure(solution.get(0));
            procedure1.setProcedure(solution.get(1));
            procedure2.setProcedure(solution.get(2));
        } else {
            new BoardProblemModal(analysis).show();
        }
    }

    /**
     * Set sidebar disabled
     */
    public void disableSidebar() {
        debug("disableSidebar was fired");
        boxSidebar.getChildren().forEach(node -> node.setDisable(true));
    }

    /**
     * Set sidebar disabled
     */
    public void enableSidebar() {
        debug("enableSidebar was fired");
        boxSidebar.getChildren().forEach(node -> node.setDisable(false));
    }

    @FXML
    private void onHide(ActionEvent actionEvent) {
        initializeLogic();
        enableSidebar();
    }

    /**
     * Sets texture grid.
     *
     * @param textureGrid the texture grid
     */
    public void setGrid(TextureGrid textureGrid) {
        mapEditor.setGrid(textureGrid);
        currentGrid = textureGrid;
        paneStage.setCenter(textureGrid);
        gui.setGameField(textureGrid);
    }

    private void setLevel(GameLevel level) {
        this.currentLevel = level;
    }

    /**
     * Open filechooser dialog and get selected file
     * @param path initial directory
     * @return selected file
     */
    private File dialogChooseJson(String path) {
        try {
            FileChooser fileChooser = new FileChooser();
            fileChooser.setInitialDirectory(new File(path));
            fileChooser.setTitle("Select Json");
            fileChooser.setSelectedExtensionFilter(new FileChooser.ExtensionFilter("json", ".json"));

            return fileChooser.showOpenDialog( paneStage.getScene().getWindow());
        } catch (Exception exception) {
            gui.panic(exception);
        }

        return null;
    }



}
