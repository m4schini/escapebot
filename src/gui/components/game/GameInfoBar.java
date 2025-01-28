package gui.components.game;

import gui.FxUserInterface;
import gui.resources.Texture;
import gui.shell.ShellController;
import javafx.application.Platform;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.css.Style;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.effect.ColorInput;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import logic.util.Log;

import java.util.ArrayList;
import java.util.List;

/**
 * The GameInfoBar is the bar displayed under the game board. Being visible when information
 * about the game should be displayed (like a win).
 * <p>
 * This is not to be used for error information.
 *
 * @author orcid.org/0009-0008-3023-1228 (inf104662)
 */
public class GameInfoBar extends HBox {
    /**
     * CSS Class name of compoenent
     */
    private static final String CSS_CLASS_NAME = "game-info-bar";

    /**
     * Text in button
     */
    private final static String BUTTON_TEXT_DEFAULT = "Next try";
    /**
     * Default game bar message
     */
    private final static String INFO_TEXT_DEFAULT = "Running... ";

    /**
     * Abort button text
     */
    private final static String BUTTON_TEXT_RUNNING = "Abort";

    /**
     * Game Win text
     */
    private final static String INFO_TEXT_WIN = "you did it!";

    /**
     * On hide button press event handler
     */
    private final ObjectProperty<EventHandler<ActionEvent>> onHide = new SimpleObjectProperty<>(actionEvent -> {});

    /**
     * On stop game button press event handler
     */
    private final ObjectProperty<EventHandler<ActionEvent>> onStopGame = new SimpleObjectProperty<>(actionEvent -> {});

    /**
     * Inner pane, styles have to be applied here
     */
    private BorderPane innerPane;

    /**
     * Instantiates new GameInfoBar.
     */
    public GameInfoBar() {

        this.setBackground(new Background(new BackgroundFill(Color.WHITE, CornerRadii.EMPTY, Insets.EMPTY)));

        setContent("", BUTTON_TEXT_DEFAULT);

        this.hide();
    }

    /**
     * Set content of info bar
     * @param infoText description text
     * @param btnText button text
     */
    private void setContent(String infoText, String btnText) {
        this.getChildren().clear();

        var text = new Text(infoText);
        BorderPane.setMargin(text, new Insets(8));

        var button = new Button();
        button.setText(btnText);
        button.setOnAction(actionEvent -> {
            onStopGame.get().handle(actionEvent);
            onHide.get().handle(actionEvent);
            this.hide();
        });
        BorderPane.setMargin(button, new Insets(8, 16, 8, 8));

        var innerPane = new BorderPane();
        innerPane.setLeft(text);
        innerPane.setRight(button);
        innerPane.getStyleClass().add(CSS_CLASS_NAME);
        this.innerPane = innerPane;

        Platform.runLater(() -> innerPane.minWidthProperty().bind(ShellController.GAME_WIDTH));

        this.getChildren().add(innerPane);
    }

    /**
     * Hide GameInfobar
     */
    public void hide() {
        this.setVisible(false);
    }

    /**
     * Make GameInfoBar visible
     */
    private void show() {
        this.hide();
        this.setVisible(true);
    }

    /**
     * Show default information and styling
     */
    public void showRunning() {
        setContent(INFO_TEXT_DEFAULT, BUTTON_TEXT_RUNNING);
        innerPane.setStyle("-fx-background-color: white");
        show();
    }

    /**
     * Show win information and styling
     */
    public void showWin() {
        setContent(INFO_TEXT_WIN, BUTTON_TEXT_DEFAULT);
        innerPane.setStyle("-fx-background-color: yellowgreen");
        show();
    }

    /**
     * Show lose information and styling.
     *
     * @param t message to show when losing
     */
    public void showLose(String t) {
        setContent(t, BUTTON_TEXT_DEFAULT);
        innerPane.setStyle("-fx-background-color: red");
        show();
    }

    /**
     * Show lose information and styling.
     */
    public void showLose() {
        this.showLose(BUTTON_TEXT_DEFAULT);
    }

    /**
     * Gets on hide.
     *
     * @return the on hide
     */
    public EventHandler<ActionEvent> getOnHide() {
        return onHide.get();
    }

    /**
     * On hide property object property.
     *
     * @return the object property
     */
    public ObjectProperty<EventHandler<ActionEvent>> onHideProperty() {
        return onHide;
    }

    /**
     * Sets on hide.
     *
     * @param onHide the on hide
     */
    public void setOnHide(EventHandler<ActionEvent> onHide) {
        this.onHide.set(onHide);
    }

    /**
     * Gets on stop game.
     *
     * @return the on stop game
     */
    public EventHandler<ActionEvent> getOnStopGame() {
        return onStopGame.get();
    }

    /**
     * On stop game property object property.
     *
     * @return the object property
     */
    public ObjectProperty<EventHandler<ActionEvent>> onStopGameProperty() {
        return onStopGame;
    }

    /**
     * Sets on stop game.
     *
     * @param action the action
     */
    public void setOnStopGame(EventHandler<ActionEvent> action) {
        onStopGame.set(action);
    }
}
