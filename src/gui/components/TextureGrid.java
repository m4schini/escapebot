package gui.components;

import gui.resources.Texture;
import gui.shell.ShellController;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.ReadOnlyDoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.event.EventHandler;
import javafx.scene.Node;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import logic.board.Board;
import logic.board.FieldType;
import logic.util.Log;
import logic.util.Vector;

import java.util.HashMap;
import java.util.Map;

import static logic.util.Log.*;

/**
 * The type Texture grid.
 *
 * @author orcid.org/0009-0008-3023-1228 (inf104662)
 */
public abstract class TextureGrid extends HBox {
    /**
     * The GridPane.
     */
    protected final GridPane grid;

    protected final Map<Vector, Field> gridMap;
    /**
     * The size of a single field in the grid.
     */
    protected final DoubleProperty FIELD_SIZE;
    protected Board board;


    /**
     * Instantiates a new Texture grid.
     */
    public TextureGrid() {
        this.grid = new GridPane();
        this.gridMap = new HashMap<>();
        this.FIELD_SIZE = new SimpleDoubleProperty();

        // wrapping grid in hbox and vbox
        var wrappingBox = new VBox();
        wrappingBox.getChildren().add(
                this.grid
        );
        this.getChildren().add(wrappingBox);

        ShellController.GAME_WIDTH.addListener((o, b, a) -> this.FIELD_SIZE.set(calcFieldSize()));
        ShellController.GAME_HEIGHT.addListener((o, b, a) -> this.FIELD_SIZE.set(calcFieldSize()));

        // ratio
        grid.prefWidthProperty().bind(wrappingBox.widthProperty());
        grid.prefHeightProperty().bind(wrappingBox.widthProperty());
        wrappingBox.prefWidthProperty().bind(this.heightProperty());
    }

    protected double calcFieldSize() {
        return Math.min(
                ShellController.GAME_HEIGHT.divide(board.getHeight()).get(),
                ShellController.GAME_WIDTH.divide(board.getWidth()).get()
        );
    }

    /**
     * Sets the board for the field
     * @param board board instance
     */
    public void setBoard(Board board) {
        this.board = board;
        draw();
    }

    public Board getBoard() {
        var board = this.board == null ? null : new Board(this.board);
        if (board != null) {
            board.setBot(board.getStartPosition(), board.getDirectionOfBot());
        }
        return board;
    }

    public ReadOnlyDoubleProperty fieldSizeProperty() {
        return FIELD_SIZE;
    }

    /**
     * Redrawing (constructing new) Grid with Fields
     */
    protected void draw() {
        draw(mouseEvent -> {
            var source = ((Field) mouseEvent.getSource());
            debug("Field(%s/%s): %s | %s | %n",
                    source.getX(),
                    source.getY(),
                    mouseEvent.getEventType(),
                    mouseEvent.getButton());
        });
    }

    protected void draw(EventHandler<? super MouseEvent> eventHandler) {
        for (int x = 0; x < board.getWidth(); x++) {
            for (int y = 0; y < board.getHeight(); y++) {
                // add to grid
                var f = new TextureGrid.Field(x, y, Texture.of(board.get(x, y), board.getDirectionOfBot()), eventHandler);
                grid.add(f, x, y);
            }
        }
        // set fieldsize to avoid reset to default size after draw
        FIELD_SIZE.set(calcFieldSize());
        debug("TextureGrid update triggered");
    }


    /**
     * On field click.
     *
     * @param eventHandler the event handler
     */
    public void setOnFieldClick(EventHandler<? super MouseEvent> eventHandler) {
        for (Node node : grid.getChildren()) {
            if (node instanceof Field field) {
                field.setOnClick(eventHandler);
            }
        }
    }

    /**
     * sets new fieldtype in board and redraws grid
     * @param fieldType new fieldtype
     * @param x coordinate
     * @param y coordinate
     */
    public void setFieldType(FieldType fieldType, int x, int y) {
        board.set(x, y, fieldType);
        draw();
    }

    /**
     * The type Field.
     */
    protected class Field extends StackPane {
        protected final int x;
        protected final int y;
        protected ImageView textureView;

        private Field(int x, int y) {
            this.x = x;
            this.y = y;

            this.maxHeightProperty().bind(FIELD_SIZE);
            this.maxWidthProperty().bind(FIELD_SIZE);
        }

        /**
         * Instantiates a new Field.
         *
         * @param x         the x
         * @param y         the y
         * @param texture   the texture
         */
        public Field(int x, int y, Texture texture) {
            this(x, y);
            this.getStyleClass().add("texture-grid-field");
            this.setTexture(texture);
        }

        /**
         * Instantiates a new Field.
         *
         * @param x         the x
         * @param y         the y
         * @param texture   the texture
         * @param eventHandler the eventHandler
         */
        public Field(int x, int y, Texture texture, EventHandler<? super MouseEvent> eventHandler) {
            this(x, y, texture);
            this.setOnClick(eventHandler);
        }

        /**
         * Add node.
         *
         * @param node the node
         */
        public void addNode(Node node) {
            this.getChildren().add(node);
        }

        /**
         * Clear.
         */
        public void clear() {
            this.getChildren().clear();
        }

        /**
         * Gets x.
         *
         * @return the game-logic x-coordinate of game
         */
        public int getX() {
            return x;
        }

        /**
         * Gets y.
         *
         * @return the game-logic y-coordinate of game
         */
        public int getY() {
            return y;
        }

        private void setTexture(Texture texture) {
            this.getChildren().clear();

            this.textureView = texture.ImageView();
            this.textureView.fitHeightProperty().bind(FIELD_SIZE);
            this.textureView.fitWidthProperty().bind(FIELD_SIZE);
            this.getChildren().add(textureView);
        }

        /**
         * On click.
         *
         * @param eventHandler the event handler
         */
        public void setOnClick(EventHandler<? super MouseEvent> eventHandler) {
            this.setOnMouseClicked(eventHandler);
        }


    }
}
