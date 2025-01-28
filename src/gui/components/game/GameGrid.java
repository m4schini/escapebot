package gui.components.game;

import gui.FxUserInterface;
import gui.animation.AnimationBuilder;
import gui.components.TextureGrid;
import gui.resources.Texture;
import javafx.animation.Animation;
import javafx.animation.PauseTransition;
import javafx.application.Platform;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.Node;
import logic.Direction;
import logic.board.GameLevel;
import logic.action.Action;
import logic.board.Board;
import logic.board.FieldType;
import logic.util.Log;
import logic.util.Vector;

import java.util.Collection;

import static logic.util.Log.debug;

/**
 * Implements special logic needed to play the Game missing in {@link TextureGrid}.
 * @author orcid.org/0009-0008-3023-1228 (inf104662)
 */
public class GameGrid extends TextureGrid {
    /**
     * CSS Class name of component
     */
    private static final String CSS_CLASS_NAME = "game-grid";

    /**
     * Node of bot character
     */
    private Node botCharacter = makeBotCharacter();
    /**
     * Start position of bot
     */
    private Vector startPos;
    /**
     * Start direction of bot
     */
    private Direction startDir;

    /**
     * Current Bot Animation
     */
    private Animation currentBotAnimation = new PauseTransition();


    /**
     * Instantiates a new Game grid.
     */
    public GameGrid() {
        super();
        this.getStyleClass().add(CSS_CLASS_NAME);
        this.setId("GameGrid");

        debug("New GameGrid initialized");
    }

    /**
     * Instantiates a new Game grid using information from a board.
     *
     * @param board the board
     */
    public GameGrid(Board board) {
        this();
        setBoard(board);
    }

    /**
     * Instantiates a new Game grid using information from a game level.
     * @param level GameLevel with state information.
     */
    public GameGrid(GameLevel level) {
        this(level.getBoard());
        var pos = level.getStartBotPosition();
        this.placeBot(pos.X(), pos.Y(), level.getStartBotDirection().ordinal());
    }

    /**
     * Set Content
     * @param board the board
     */
    @Override
    public void setBoard(Board board) {
        super.setBoard(board);
        this.startPos = board.getStartPosition();
        this.startDir = board.getDirectionOfBot();
    }

    /**
     * Place bot in Game Grid
     * @param x        the x
     * @param y        the y
     * @param rotation the rotation
     */
    public void placeBot(int x, int y, int rotation) {
        grid.getChildren().remove(botCharacter);
        botCharacter = makeBotCharacter();
        grid.add(botCharacter, x, y);
        botCharacter.setId("EscapeBot");
        botCharacter.setRotate((rotation * 90) - 90);
        botCharacter.setVisible(false);

        Log.debug("placeBot triggered");
    }

    @Override
    protected void draw() {
        super.draw();
        //botCharacter.toFront();
    }

    /**
     * Reset game grid state back to start of level
     */
    public void reset() {
        placeBot(startPos.X(), startPos.Y(), startDir.ordinal());
    }

    @Override
    public void setFieldType(FieldType fieldType, int x, int y) {
        super.setFieldType(fieldType, x, y);
        botCharacter.toFront();
    }

    /**
     * Animate the bot character.
     * @param actions instructions of actions that should be animated
     * @return Property of currently playing action
     */
    public ReadOnlyObjectProperty<Action> animateBot(Collection<Action> actions, Runnable onAnimationFinished) {
        assert actions != null;
        assert !actions.isEmpty();

        final ObjectProperty<Action> currentAction = new SimpleObjectProperty<>();

        if (FxUserInterface.ANIMATION_DURATION_MODIFIER.intValue() > 0) {
            debug("[[[Animating Character (actions: %d)]]]\n", actions.size());
            botCharacter.setVisible(true);
            botCharacter.toFront();

            final AnimationBuilder animationBuilder = new AnimationBuilder(botCharacter, FIELD_SIZE, startPos);

            debug("[[[CONSTRUCTING ANIMATION]]]");
            for (Action action : actions) {
                debug(action);

                var position = action.getPosition();
                var destination = action.getDestination();

                // onFinished Runnable, gets executed after each transition finished
                Runnable onStart = () -> currentAction.set(action);
                Runnable onFinished = () -> Log.debug("finished_animation");

                switch (action.getType()) {
                    case START -> animationBuilder.appendRunnable(() ->  {
                        var p = action.getPosition();
                        setFieldType(FieldType.NORMAL, p.X(), p.Y());
                    });
                    case MOVE -> animationBuilder.appendTranslation(
                            position,
                            destination,
                            onStart,
                            onFinished);
                    case JUMP -> animationBuilder.appendJumpTranslation(
                            position,
                            destination,
                            1.3,
                            onStart,
                            onFinished
                    );
                    case TURN_RIGHT -> animationBuilder.appendRotation(
                            90,
                            onStart,
                            onFinished);
                    case TURN_LEFT -> animationBuilder.appendRotation(
                            -90,
                            onStart,
                            onFinished);
                    case FALL_INTO_ABYSS, RUN_INTO_WALL -> {
                        animationBuilder.appendFallTranslation(
                                position,
                                destination,
                                onStart,
                                onFinished
                        );
                    }
                    case COLLECT_COIN -> {
                        animationBuilder.appendScaling(1.5, onStart, () -> {});
                        animationBuilder.appendRunnable(() -> {
                            var p = action.getDestination();
                            setFieldType(FieldType.NORMAL, p.X(), p.Y());
                        });
                        animationBuilder.appendScaling(1, () -> {}, onFinished);
                    }
                    case EXIT_SUCCESSFUL -> {
                        var target = action.getDestination().add(action.getDirection().vector());

                        animationBuilder.appendTranslation(action.getDestination(), target);
                        animationBuilder.appendFading(1.0, 0.0, onStart, onFinished);
                        animationBuilder.appendRunnable(() -> setFieldType(FieldType.NORMAL, target.X(), target.Y()));
                    }
                    default -> {
                        animationBuilder.appendRunnable(onStart);
                        animationBuilder.appendRunnable(onFinished);
                    }
                }
            }
            animationBuilder.appendWait(10); //without this wall isn't overridden


            currentBotAnimation = animationBuilder.toTransition();
            //CURRENT_ANIMATION = currentBotAnimation;
            currentBotAnimation.setOnFinished(actionEvent -> {
                onAnimationFinished.run();
                debug("[[[FINISHED ANIMATION]]]");
            });

            debug("[[[PLAYING ANIMATION]]]");
            currentBotAnimation.play();

        } else {
            debug("[[[SKIPPED ANIMATION]]]");
            onAnimationFinished.run();
        }

        return currentAction;
    }

    /**
     * Stopping bot animation
     * @return if animation was running before stop
     */
    public boolean stopBotAnimation() {
        boolean wasRunning = currentBotAnimation.getStatus() == Animation.Status.RUNNING;
        Platform.runLater(() -> {
            currentBotAnimation.pause();
            currentBotAnimation.stop();
            currentBotAnimation = new PauseTransition();
            //CURRENT_ANIMATION = currentBotAnimation;
        });
        return wasRunning;
    }

    /**
     * Generate new Bot Character Node
     * @return node of Bot character
     */
    private Node makeBotCharacter() {
        var offset = FIELD_SIZE.divide(4);
        var size = FIELD_SIZE.subtract(offset);

        var imgView = Texture.BOT.ImageView(size.intValue());
        imgView.setPreserveRatio(true);
        imgView.fitHeightProperty().bind(size);
        imgView.fitWidthProperty().bind(size);

        var img = imgView.getImage();

        imgView.xProperty().bind(
                imgView.fitWidthProperty()
                        .subtract(img.getWidth()).divide(2)
                        .subtract(offset)
        );
        imgView.yProperty().bind(
                imgView.fitHeightProperty()
                        .subtract(img.getHeight()).divide(2)
        );
        return imgView;
    }
}
