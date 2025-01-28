package gui.components.editor;

import gui.components.TextureGrid;
import gui.resources.Texture;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.text.Text;
import logic.Direction;
import logic.board.Board;
import logic.board.FieldType;
import logic.util.Log;

import static logic.util.Log.debug;

/**
 * The type Editor grid.
 *
 * @author orcid.org/0009-0008-3023-1228 (inf104662)
 */
public class EditorGrid extends TextureGrid {
    /**
     * Property of selected fieldtype by user
     */
    private ReadOnlyObjectProperty<FieldType> selectedFieldTypeProperty;
    /**
     * Property of start direction
     */
    private ObjectProperty<Direction> startDirectionProperty;

    /**
     * Instantiates a new Editor grid.
     */
    public EditorGrid() {
        super();
        this.getStyleClass().add("editor-grid");

        debug("New EditorGrid initialized");
    }

    /**
     * Instantiates a new Editor grid.
     *
     * @param board                     the board
     * @param selectedFieldTypeProperty the selected field type property
     */
    public EditorGrid(Board board, ReadOnlyObjectProperty<FieldType> selectedFieldTypeProperty) {
        this();
        this.selectedFieldTypeProperty = selectedFieldTypeProperty;
        this.startDirectionProperty = new SimpleObjectProperty<>(board.getDirectionOfBot());
        this.startDirectionProperty.addListener((observableValue, before, direction) -> {
            Log.debug("START DIRECTION: %s | %s | %s\n", observableValue, before, direction);
        });
        setBoard(board);
    }

    /**
     * Sets the direction of the start field
     * @param ordinal direction of field in as ordinal
     */
    private void setStartDirection(int ordinal) {
        setStartDirection(Direction.fromOrdinal(ordinal));
    }

    /**
     * Sets the direction of the start field
     * @param direction direction of field
     */
    private void setStartDirection(Direction direction) {
        this.startDirectionProperty.set(direction);
        board.setDirectionOfBot(direction);
        Log.debug("Changed direction of bot on start position: %s -> %s\n",
                board.getDirectionOfBot().rotate(-1),
                board.getDirectionOfBot());
        draw();
    }

    @Override
    protected void draw() {
        grid.getChildren().clear();
        for (int x = 0; x < board.getWidth(); x++) {
            for (int y = 0; y < board.getHeight(); y++) {
                // add to grid
                var f = new Field(x, y, board.get(x, y));
                grid.add(f, x, y);
            }
        }
        FIELD_SIZE.set(calcFieldSize());
        debug("EditorGrid update triggered");
    }

    @Override
    public void setBoard(Board board) {
        super.setBoard(board);
        draw();
    }

    /**
     * Grid Field
     */
    protected class Field extends TextureGrid.Field {
        /**
         * FieldType of Grid field
         */
        private FieldType fieldType;

        /**
         * Instantiates a new Field.
         *
         * @param x            the x
         * @param y            the y
         * @param fieldType    the fieldType
         */
        public Field(int x, int y, FieldType fieldType) {
            super(x, y, Texture.of(fieldType, startDirectionProperty.get()));
            this.fieldType = fieldType;
            if (fieldType == FieldType.START && startDirectionProperty != null) {
                Log.debug("Rotate to: %s %d\n", startDirectionProperty.get(), (startDirectionProperty.get().ordinal()) * 90);
                //setRotate((startDirectionProperty.get().ordinal()) * 90);
            }

            this.setOnClick(mouseEvent -> {
                if (mouseEvent.getSource() instanceof Field field) {
                    var selectedType = selectedFieldTypeProperty.get();
                    switch (selectedType) {
                        case START -> {
                            if (field.fieldType == FieldType.START) {
                                setStartDirection((startDirectionProperty.get().ordinal() + 1) % Direction.values().length);
                            } else {
                                replaceFieldType(selectedType, FieldType.NORMAL);
                            }
                        }
                        case DOOR -> replaceFieldType(selectedType, FieldType.WALL);
                        default -> setFieldType(selectedType);
                    }
                } else {
                    Log.error("Unexpected fieldType at x=%d, y=%d\n", x, y);
                }
            });

            var positionLabel = new Text(String.format("(%d/%d)", x, y));
            positionLabel.setStyle("-fx-fill: white; -fx-font-weight: 900");

            this.getChildren().add(positionLabel);
        }

        /**
         * Replaces FieldType
         * @param before FieldType you want to replace
         * @param after FieldType you want to replace it with
         */
        public void replaceFieldType(FieldType before, FieldType after) {
            if (board.contains(before)) {
                board.set(board.positionOf(before), after);
                assert !board.contains(before);
                draw();
            }

            setFieldType(before);
        }

        /**
         * Set FieldType of grid-field
         * @param fieldType type of field
         */
        public void setFieldType(FieldType fieldType) {
            this.fieldType = fieldType;

            // set field type
            board.set(x, y, fieldType);
            draw();
            debug("Field[%d|%d] now: %s\n", x, y, fieldType);
        }
    }
}
