package gui.components.game;


import gui.resources.Texture;
import javafx.beans.NamedArg;
import javafx.beans.property.*;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.scene.control.Button;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.*;
import javafx.scene.text.Text;
import logic.procedure.Instruction;
import logic.procedure.Procedure;

import java.util.Collection;

import static logic.util.Log.*;

/**
 * A ProcedureGrid is a grid of instructions with specified rows and columns. Instructions in this
 * grid are handled in a linear fashion. Meaning that instructions are added starting from the top-left
 * cell and continuing in that row until its full. Continuing with all other rows until everything is full.
 *
 * @author orcid.org/0009-0008-3023-1228 (inf104662)
 */
public class ProcedureGrid extends VBox {
    private static final String CSS_CLASS_NAME = "procedure-grid";

    private static final String TEXT_CLEAR_BTN = "Clear";
    /**
     * Name of procedure
     */
    private final StringProperty nameProperty = new SimpleStringProperty("Procedure");
    /**
     * Max size of ProcedureGrids (max amounts of instructions)
     */
    private final int size;

    /**
     * Toggle group
     */
    private final ToggleGroup toggleGroup = new ToggleGroup();
    /**
     * Observable Property of Instructions
     */
    private final ListProperty<Instruction> instructions = new SimpleListProperty<>(FXCollections.observableArrayList());
    /**
     * Observable Property of the selected Instruction.
     */
    private final ObjectProperty<Instruction> selectedInstruction = new SimpleObjectProperty<>();
    /**
     * GridPane of ProcedureFields used to display the instructions.
     */
    private final GridPane grid;

    /**
     * Instantiates a new Procedure grid with values provided by javafx (Likely through a .fxml file)
     *
     * @param name Name of Procedure
     * @param rows amount of rows
     * @param cols amount of columns
     */
    public ProcedureGrid(@NamedArg("name") String name,
                         @NamedArg("rows") String rows,
                         @NamedArg("columns") String cols) {
        this(name, Integer.parseInt(rows), Integer.parseInt(cols));
    }

    /**
     * Instantiates a new Procedure grid.
     *
     * @param name Name of Procedure
     * @param rows amount of rows
     * @param cols amount of columns
     */
    public ProcedureGrid(String name, int rows, int cols) {
        this(rows, cols);
        if (name != null) this.nameProperty.set(name);
    }

    /**
     * Instantiates a new Procedure grid.
     *
     * @param rows amount of rows
     * @param cols amount of columns
     */
    public ProcedureGrid(int rows, int cols) {
        this.getStyleClass().add(CSS_CLASS_NAME);

        this.size = rows * cols;
        this.grid = makeGrid(this, rows, cols);
        setUpListChangeRender(grid);

        // label
        var textName = new Text();
        textName.textProperty().bind(nameProperty);
        textName.getStyleClass().add("text");

        // clear button
        var btnClear = new Button(TEXT_CLEAR_BTN);
        btnClear.setOnMouseClicked(event -> {
            instructions.clear();
            toggleGroup.selectToggle(null);
        });

        // above grid
        var head = new BorderPane();
        head.setLeft(textName);
        head.setRight(btnClear);

        this.getChildren().setAll(
                head,
                grid
        );

        debug("New ProcedureGrid [%d x %d] initialized%n", rows, cols);
    }

    /**
     * Only needed for {@link ProcedureField}, so access is package private.
     * @return Toggle Group os selected instruction
     */
    ToggleGroup getToggleGroup() {
        return toggleGroup;
    }

    /**
     * Get Name of this Procedure and ProcedureGrid
     * @return name
     */
    public String getName() {
        return this.nameProperty.get();
    }

    /**
     * remove instruction at position
     * @param pos position of instruction that should be removed
     */
    public void removeInstruction(int pos) {
        instructions.remove(pos);
    }

    /**
     * Count of instructions currently in grid
     * @return count of instructions
     */
    public int getInstructionsCount() {
        return instructions.size();
    }

    /**
     * Gets procedure.
     *
     * @return the procedure
     */
    public Procedure getProcedure() {
        return new Procedure(instructions);
    }

    /**
     * Observable property of instructions in grid
     * @return instructions of instructions
     */
    public ListProperty<Instruction> procedureProperty() {
        return instructions;
    }

    /**
     * Bind instructions to another property
     * @param instructions bindable instructions property
     */
    public void bindProcedure(ReadOnlyListProperty<Instruction> instructions) {
        this.instructions.bind(instructions);
    }

    /**
     * Override current instructions with other instructions
     * @param instructions other instructions
     */
    public void setProcedure(Collection<Instruction> instructions) {
        this.instructions.setAll(instructions);
    }

    /**
     * Bind selectedInstruction to another property
     * @param selected selected instructions property
     */
    public void bindSelected(ReadOnlyObjectProperty<Instruction> selected) {
        this.selectedInstruction.bind(selected);
    }

    /**
     * Property of currently selected instructions
     * @return Selected Instruction Property
     */
    ReadOnlyObjectProperty<Instruction> selectedInstruction() {
        return selectedInstruction;
    }
    
    /**
     * Add instruction.
     *
     * @param position    the position
     * @param instruction the instruction
     */
    void addInstruction(int position, Instruction instruction) {
        if (instruction == null) throw new IllegalArgumentException("null not allowed");

        if (instructions.size() >= position && size > position && instructions.size() < size) {
            instructions.add(position, instruction);

        } else {
            warning("not allowed to add on field: %s (of %s)%n",
                    position,
                    instructions.size() - 1);
        }
    }

    /**
     * Add instruction method as defined by Aufgabenstellung
     * @param position position in instruction instructions
     * @param instruction new instruction
     */
    void offerInstruction(int position, Instruction instruction) {
        if (instruction == null) throw new IllegalArgumentException("null not allowed");

        if (instructions.size() == position && size > position) {
            instructions.add(position, instruction);

        } else if (instructions.size() > position && size > position && instructions.size() < size) {
            instructions.remove(position);
            instructions.add(position, instruction);
        } else {
            warning("not allowed to add on field: %s (of %s)%n",
                    position,
                    instructions.size() - 1);
        }
    }

    /**
     * Clears instructions and highlight
     */
    public void clear() {
        instructions.clear();
        clearHighlight();
    }

    /**
     * Clears visual selection of single instruction
     */
    public void clearHighlight() {
        toggleGroup.selectToggle(null);
    }

    /**
     *
     * @param i the
     */
    public void highlight(int i) {
        var n = grid.getChildren().get(i);
        if (n instanceof ProcedureField f) {
            f.setSelected(true);
        } else {
            error("%s tried to highlight something that isn't a Field");
        }
    }

    private void setUpListChangeRender(GridPane grid) {
        this.instructions.addListener((ListChangeListener<? super Instruction>) c -> {
            for (int i = 0; i < this.size; i++) {
                // throws if grid[i] is not Field.class
                if (!(grid.getChildren().get(i) instanceof ProcedureField field))
                    throw new IllegalStateException(String.format("grid[%s] should be Field", i));

                // either sets image of instruction or clears field of there are no instructions
                if (i < instructions.size()) {
                    field.setTexture(Texture.of(instructions.get(i)));
                } else {
                    field.clearTexture();
                }
            }
        });
    }

    /**
     * Build new GridPane of ProcedureFields connected to this properties.
     * @param parent parent of ProcedureFields
     * @param rows amount of rows
     * @param cols amount of columns
     * @return new GridPane of ProcedureFields
     */
    private static GridPane makeGrid(ProcedureGrid parent, int rows, int cols) {
        var grid = new GridPane();
        // add rows
        for (int i = 0; i < rows; i++) {
            var row = new RowConstraints();
            grid.getRowConstraints().add(row);
        }
        // add columns
        for (int i = 0; i < cols; i++) {
            var col = new ColumnConstraints();
            grid.getColumnConstraints().add(col);
        }

        // add fiels to "cells"
        int pos = 0;
        for (int r = 0; r < grid.getRowCount(); r++) {
            for (int c = 0; c < grid.getColumnCount(); c++) {
                grid.add(new ProcedureField(pos++, parent), c, r);
            }
        }
        return grid;
    }
}
