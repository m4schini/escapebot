package gui.components.editor;

import gui.components.EditorControls;
import gui.components.TextureGrid;
import gui.components.EnumSelectorGroup;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import logic.board.Board;
import logic.board.FieldType;

/**
 * MapEditor is the controls panel used to edit the level files
 *
 * @author orcid.org/0009-0008-3023-1228 (inf104662)
 */
public class MapEditor extends VBox implements EditorControls {
    /**
     * CSS Class name
     */
    private static final String CSS_CLASS_NAME = "editor";
    /**
     * Text for label
     */
    private static final String TXT_LABEL_SELECTOR = "Field Types";
    /**
     * Text for button
     */
    private static final String TXT_BTN_SOLVE_LEVEL = "Solve Level";

    /**
     * By user selected FieldType
     */
    private final ReadOnlyObjectProperty<FieldType> selectedProperty;

    /**
     * Parent Node of procedure grids
     */
    private final ObjectProperty<Node> procedures;
    /**
     * TextureGrid Node
     */
    private final ObjectProperty<TextureGrid> gridProperty;
    /**
     * onSolveLevel handler
     */
    private final ObjectProperty<EventHandler<ActionEvent>> onSolveLevel;

    /**
     * Instantiates a new Map editor.
     */
    public MapEditor() {
        this.getStyleClass().add(CSS_CLASS_NAME);
        this.procedures = new SimpleObjectProperty<>(new Text("placeholder"));
        this.gridProperty = new SimpleObjectProperty<>();
        this.onSolveLevel = new SimpleObjectProperty<>();

        var fieldTypeSelector = new EnumSelectorGroup<>(FieldType.class);
        this.selectedProperty = fieldTypeSelector.SelectedProperty();

        var label = new Text(TXT_LABEL_SELECTOR);
        label.getStyleClass().add("text");
        //label.setFill(Color.WHITE);
        VBox.setMargin(label, new Insets(8));

        var selected = new Text("");
        selected.getStyleClass().add("text");
        selected.textProperty().bind(selectedProperty.asString());
        //selected.setFill(Color.WHITE);
        //selected.setTextAlignment(TextAlignment.CENTER);
        VBox.setMargin(selected, new Insets(0, 0, 0, 8));

        var mapTilesBox = new VBox();
        mapTilesBox.getChildren().addAll(
                label,
                selected,
                fieldTypeSelector
        );

        var main = new HBox();
        main.getChildren().addAll(
                mapTilesBox,
                procedures.get()
        );

        var options = new HBox();
        var btnCheckSolve = new Button(TXT_BTN_SOLVE_LEVEL);
        btnCheckSolve.onActionProperty().bind(onSolveLevel);

        options.getChildren().addAll(
                btnCheckSolve
        );
        VBox.setMargin(options, new Insets(0,0,8,0));

        this.getChildren().addAll(
                options,
                main
        );

        procedures.addListener((observableValue, before, after) -> {
            var context = main.getChildren();
            int index = context.indexOf(before);
            context.set(index, after);
        });
    }

    /**
     * Gets Board
     * @return current board
     */
    public Board getBoard() {
        return gridProperty.get().getBoard();
    }

    /**
     * Selected property read only object property.
     *
     * @return the read only object property
     */
    public ReadOnlyObjectProperty<FieldType> SelectedProperty() {
        return selectedProperty;
    }

    /**
     * Gets procedures.
     *
     * @return the procedures
     */
    public Node getProcedures() {
        return procedures.get();
    }

    /**
     * Procedures property object property.
     *
     * @return the object property
     */
    public ObjectProperty<Node> proceduresProperty() {
        return procedures;
    }

    /**
     * Sets procedures.
     *
     * @param procedures the procedures
     */
    public void setProcedures(Node procedures) {
        procedures.setDisable(true);
        this.procedures.set(procedures);
    }

    /**
     * Current Grid
     * @return current grid
     */
    public Node getGrid() {
        return gridProperty.get();
    }

    public ObjectProperty<TextureGrid> gridProperty() {
        return gridProperty;
    }

    public void setGrid(Node node) {
        if (node instanceof TextureGrid grid) {
            this.gridProperty.set(grid);
        } else {
            throw new IllegalArgumentException("node has to be TextureGrid or subclass");
        }
    }

    public EventHandler<ActionEvent> getOnSolveLevel() {
        return onSolveLevel.get();
    }

    public ObjectProperty<EventHandler<ActionEvent>> onSolveLevelProperty() {
        return onSolveLevel;
    }

    public void setOnSolveLevel(EventHandler<ActionEvent> onSolveLevel) {
        this.onSolveLevel.set(onSolveLevel);
    }
}
