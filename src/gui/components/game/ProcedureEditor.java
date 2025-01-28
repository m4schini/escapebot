package gui.components.game;

import gui.components.EditorControls;
import gui.components.EnumSelectorGroup;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.Node;
import javafx.scene.layout.*;
import javafx.scene.text.Text;
import logic.procedure.Instruction;
import logic.procedure.Procedure;

import java.util.ArrayList;
import java.util.List;

/**
 * The ProcedureEditor is the panel with all controls for the creation of procedures
 *
 * @author orcid.org/0009-0008-3023-1228 (inf104662)
 */
public class ProcedureEditor extends HBox implements EditorControls {
    private static final String CSS_CLASS_NAME = "editor";
    private static final String CSS_CLASS_NAME_INSTR_BOX = "instructions-box";
    /**
     * The instruction currently selected by user
     */
    private final ReadOnlyObjectProperty<Instruction> selectedInstruction;
    /**
     * The parent node of the procedure grids
     */
    private final ObjectProperty<Node> grids = new SimpleObjectProperty<>();
    /**
     * List of all procedures managed by this editor
     */
    private final List<ProcedureGrid> procedures = new ArrayList<>();

    /**
     * Instantiates a new Procedure editor.
     */
    public ProcedureEditor() {
        this.getStyleClass().add(CSS_CLASS_NAME);
        grids.set(new Text("SOMETHING HAPPENED :("));

        EnumSelectorGroup<Instruction> instructionSelector = new EnumSelectorGroup<>(Instruction.class);
        this.selectedInstruction = instructionSelector.SelectedProperty();

        var label = new Text("Instructions");
        label.getStyleClass().add("text");
        //label.setFill(Color.WHITE);
        //VBox.setMargin(label, new Insets(8));

        VBox instructionsBox = new VBox();
        instructionsBox.getStyleClass().add(CSS_CLASS_NAME_INSTR_BOX);
        instructionsBox.getChildren().addAll(
                label,
                instructionSelector
        );

        this.getChildren().addAll(
                grids.get(),
                instructionsBox
        );

        // replace grid and make sure it's not disabled
        this.grids.addListener((observableValue, before, after) -> {
            int index = this.getChildren().indexOf(before);
            after.setDisable(false);
            this.getChildren().set(index, after);
        });
    }

    /**
     * Highlight specified instruction in one of the grids with a different styling
     * @param procedure position of procedure (in order of grids seen in editor)
     * @param instruction position of instruction in procedureGrid
     */
    public void highlightInstruction(int procedure, int instruction) {
        procedures.get(procedure).highlight(instruction);
    }

    /**
     * Get Grids node.
     * @implNote Required getter to make this property accessible by java fxml.
     * @return grids node
     */
    public Node getGrids() {
        return grids.get();
    }

    /**
     * Get grid node property
     * @implNote Required getter to make this property accessible by java fxml.
     * @return Node Property
     */
    public ObjectProperty<Node> gridsProperty() {
        return grids;
    }

    /**
     * Set grid node.
     * @param grids grids node
     */
    public void setGrids(Node grids) {
        if (grids instanceof Pane p) {
            // wrapping and binding
            //var pB = new VBox();
            p.getChildren().forEach(node -> {
                if (node instanceof ProcedureGrid pg) {
                    pg.bindSelected(selectedInstruction);
                    if (!procedures.contains(pg)) procedures.add(pg);
                }
            });
        }

        this.grids.set(grids);
    }

    /**
     * ChildProcedure 0 is Parent-Procedure
     *
     * @param p id of procedure, from 1 onward
     * @return Procedure child procedure
     */
    public Procedure getProcedure(int p) {
        return this.procedures.get(p).getProcedure();
    }

    /**
     * Clear highlights in procedure grids
     */
    public void clearHighlights() {
        procedures.forEach(ProcedureGrid::clearHighlight);
    }

    public void clearGrids() {
        clearHighlights();
        procedures.forEach(ProcedureGrid::clear);
    }
}
