package gui.animation;

import javafx.animation.*;
import javafx.beans.property.ReadOnlyDoubleProperty;
import javafx.scene.Node;
import javafx.util.Duration;
import logic.util.Vector;

import java.util.ArrayList;
import java.util.List;

import static gui.FxUserInterface.ANIMATION_DURATION_MODIFIER;
import static gui.FxUserInterface.ANIMATION_STD_DURATION;

/**
 * AnimationBuilder, like a StringBuilder, can be used to append multiple transition together and
 * export as sequentialTransition or Animation.
 *
 * @author orcid.org/0009-0008-3023-1228 (inf104662)
 */
public class AnimationBuilder {
    /**
     * logic uses the upper left corner of board as 0,0. An Animation uses the current position as 0,0.
     * To circumvent this we have to calculate an offset.
     */
    private final Vector offset;
    /**
     * Size of field to calculate distance animation
     */
    private final ReadOnlyDoubleProperty fieldSize;
    /**
     * list of added transitions
     */
    private final List<Transition> listOfTransitions;
    /**
     * target of animations
     */
    private final Node actor;

    /**
     * Instantiates a new Transition builder
     * @param actor node that transitions are applied to
     * @param fieldSize size of a single 'step'
     */
    public AnimationBuilder(Node actor, ReadOnlyDoubleProperty fieldSize) {
        this(actor, fieldSize, new Vector(0,0));
    }

    /**
     * Instantiates a new Transition builder
     * @param actor node that transitions are applied to
     * @param fieldSize size of a single 'step'
     * @param offset if pos of actor is not 0,0 you should specify an offset vector. (Current pos of actor)
     */
    public AnimationBuilder(Node actor, ReadOnlyDoubleProperty fieldSize, Vector offset) {
        this.listOfTransitions = new ArrayList<>();
        this.offset = offset;
        this.fieldSize = fieldSize;
        this.actor = actor;
    }

    /**
     * Translating coordinates used in GameLogic to pixel coordinates used by gui
     * (adds small offset to center actor in field)
     * @param logicCoordinate logic coordinate
     * @return pixel coordinate
     *
     */
    private double xPixel(int logicCoordinate) {
        return fieldSize.doubleValue() * logicCoordinate + fieldSize.divide(8).doubleValue();
    }

    /**
     * Translating coordinates used in GameLogic to pixel coordinates used by gui
     * @param logicCoordinate logic coordinate
     * @return pixel coordinate
     */
    private double yPixel(int logicCoordinate) {
        return fieldSize.doubleValue() * logicCoordinate;
    }

    /**
     * Calculates standard duration with default duration and speed modifier.
     * @return standard duration.
     */
    private Duration getDuration() {
        return Duration.millis(ANIMATION_STD_DURATION.intValue() * ANIMATION_DURATION_MODIFIER.intValue());
    }

    /**
     * Add start and finish runner to transition
     * @param target node to add runner to
     * @param onStart onStart runner
     * @param onFinished onFinished runner
     */
    private static void addStartAndFinishHandler(Transition target, Runnable onStart, Runnable onFinished) {
        target.statusProperty().addListener((observableValue, before, after) -> {
            if (after == Animation.Status.RUNNING) onStart.run();
        });

        // on end run
        target.onFinishedProperty().set(actionEvent -> onFinished.run());
    }

    /**
     * Add a translateTransition. Translating from start position to destination.
     * @param start start position
     * @param destination end position
     * @return this AnimationBuilder
     */
    public AnimationBuilder appendTranslation(Vector start, Vector destination) {
        return appendTranslation(start, destination, () -> {}, () -> {});
    }

    /**
     * Adds a translateTransition. Translating from start position to destination.
     * @param start start position
     * @param destination end position
     * @param onStart this is run when this single transition is started
     * @param onFinished this is run when this single transition is finished
     * @return this AnimationBuilder
     */
    public AnimationBuilder appendTranslation(Vector start, Vector destination, Runnable onStart, Runnable onFinished) {
        assert onStart != null;
        assert onFinished != null;

        start = start.subtract(offset);
        destination = destination.subtract(offset);

        var translateTransition = makeTranslateTransition(getDuration(), start, destination);
        addStartAndFinishHandler(translateTransition, onStart, onFinished);

        this.listOfTransitions.add(translateTransition);
        return this;
    }

    /**
     * Adds a scale transition. Scaling actor by specified scale.
     * @param scale end scale of actor
     * @param onStart this is run when this single transition is started
     * @param onFinished this is run when this single transition is finished
     * @return this AnimationBuilder
     */
    public AnimationBuilder appendScaling(double scale, Runnable onStart, Runnable onFinished) {
        assert onStart != null;
        assert onFinished != null;

        // => Scale Transition
        var scaleTransition = makeScaleTransition(getDuration(), scale);
        addStartAndFinishHandler(scaleTransition, onStart, onFinished);

        this.listOfTransitions.add(scaleTransition);
        return this;
    }

    /**
     * Adds a rotate-transition.
     * @param degrees degrees by which the actor is rotated
     * @param onStart this is run when this single transition is started
     * @param onFinished this is run when this single transition is finished
     * @return this AnimationBuilder
     */
    public AnimationBuilder appendRotation(int degrees,
                                           Runnable onStart,
                                           Runnable onFinished) {
        assert onStart != null;
        assert onFinished != null;

        var rotateTransition = new RotateTransition();
        rotateTransition.setDuration(getDuration());
        rotateTransition.setByAngle((degrees));

        addStartAndFinishHandler(rotateTransition, onStart, onFinished);

        this.listOfTransitions.add(rotateTransition);
        return this;
    }

    /**
     * Adds a fade transition.
     * @param fromOpacity start opacity
     * @param toOpacity end opacity
     * @return this AnimationBuilder
     */
    public AnimationBuilder appendFading(double fromOpacity, double toOpacity) {
        return appendFading(fromOpacity, toOpacity, () -> {}, () -> {});
    }

    /**
     * Adds a fade transition.
     * @param fromOpacity start opacity
     * @param toOpacity end opacity
     * @param onStart this is run when this single transition is started
     * @param onFinished this is run when this single transition is finished
     * @return this AnimationBuilder
     */
    public AnimationBuilder appendFading(double fromOpacity,
                                         double toOpacity,
                                         Runnable onStart,
                                         Runnable onFinished) {
        assert onStart != null;
        assert onFinished != null;

        var fadeTransition = new FadeTransition();
        fadeTransition.setFromValue(fromOpacity);
        fadeTransition.setToValue(toOpacity);
        fadeTransition.setDuration(getDuration());

        var scaleTransition = makeScaleTransition(getDuration(), 0.5);

        // => Parallel
        ParallelTransition parallelTransition = new ParallelTransition(
                fadeTransition,
                scaleTransition
        );

        addStartAndFinishHandler(parallelTransition, onStart, onFinished);
        this.listOfTransitions.add(parallelTransition);
        return this;
    }

    /**
     * Adds a pause transition.
     * @param millis duration of pause in milliseconds
     * @return this AnimationBuilder
     */
    public AnimationBuilder appendWait(int millis) {
        return appendWait(millis, () -> {}, () -> {});
    }

    /**
     * Adds a pause transition.
     * @param millis duration of pause in milliseconds
     * @param onStart this is run when this single transition is started
     * @param onFinished this is run when this single transition is finished
     * @return this AnimationBuilder
     */
    public AnimationBuilder appendWait(int millis,
                                       Runnable onStart,
                                       Runnable onFinished) {
        assert onStart != null;
        assert onFinished != null;

        var pauseTransition = new PauseTransition();
        pauseTransition.setDuration(Duration.millis(millis));

        addStartAndFinishHandler(pauseTransition, onStart, onFinished);

        this.listOfTransitions.add(pauseTransition);
        return this;
    }

    /**
     * Adds a parallel transition of translate and scale.
     * @param start from position of transition
     * @param destination to position of transition
     * @param scale scale the actor jumps to
     * @param onStart this is run when this single transition is started
     * @param onFinished this is run when this single transition is finished
     * @return this AnimationBuilder
     */
    public AnimationBuilder appendJumpTranslation(Vector start,
                                                  Vector destination,
                                                  double scale,
                                                  Runnable onStart,
                                                  Runnable onFinished) {
        assert onStart != null;
        assert onFinished != null;

        start = start.subtract(offset);
        destination = destination.subtract(offset);

        // => Translate Transition
        var translateTransition = makeTranslateTransition(getDuration(), start, destination);

        // => Scale Transition
        var scaleTransition = makeScaleTransition(getDuration().divide(2), scale);
        scaleTransition.setCycleCount(2);
        scaleTransition.setAutoReverse(true);

        // => Parallel
        ParallelTransition parallelTransition = new ParallelTransition(
                scaleTransition,
                translateTransition
        );

        addStartAndFinishHandler(parallelTransition, onStart, onFinished);

        this.listOfTransitions.add(parallelTransition);
        return this;
    }

    /**
     * Adds a parallel transition of translate and scale (scale to zero).
     * @param start from position of transition (start point)
     * @param destination to position of transition (fall point)
     * @param onStart this is run when this single transition is started
     * @param onFinished this is run when this single transition is finished
     * @return this AnimationBuilder
     */
    public AnimationBuilder appendFallTranslation(Vector start, Vector destination, Runnable onStart, Runnable onFinished) {
        assert onStart != null;
        assert onFinished != null;

        start = start.subtract(offset);
        destination = destination.subtract(offset);

        // => Translate Transition
        var translateTransition = makeTranslateTransition(getDuration(), start, destination);

        // => Scale Transition
        var scaleTransition = makeScaleTransition(getDuration(), 0);

        // => Parallel
        ParallelTransition parallelTransition = new ParallelTransition(
                scaleTransition,
                translateTransition
        );

        addStartAndFinishHandler(parallelTransition, onStart, onFinished);

        this.listOfTransitions.add(parallelTransition);
        return this;
    }

    /**
     * Append a runnable. This runnable will be executed in the sequential order of the transition.
     * @param action runnable
     * @return this AnimationBuilder
     */
    public AnimationBuilder appendRunnable(Runnable action) {
        var actionTransition = new ActionTransition(action);

        this.listOfTransitions.add(actionTransition);
        return this;
    }

    /**
     * Generates sequential Transition of all added transitions.
     * Closes this builder. (This makes the internal transition immutable)
     * @return SequentialTransition of all added transitions
     */
    public SequentialTransition toTransition() {
        var seqTransition = new SequentialTransition(listOfTransitions.toArray(Animation[]::new));
        seqTransition.setNode(actor);

        return seqTransition;
    }

    /**
     * Generates animation from Builder.
     * @return animation of all appended transitions.
     */
    public Animation toAnimation() {
        return toTransition();
    }

    /**
     * Factory method for a TranslateTransition
     * @param duration duration of transition
     * @param start from position of transition
     * @param destination to position of transition
     * @return new translateTransition
     */
    private TranslateTransition makeTranslateTransition(Duration duration, Vector start, Vector destination) {
        var translateTransition = new TranslateTransition();

        //translateTransition.setNode(actor);
        translateTransition.setDuration(duration);

        translateTransition.setFromX(xPixel(start.X()));
        translateTransition.setFromY(yPixel(start.Y()));

        translateTransition.setToX(xPixel(destination.X()));
        translateTransition.setToY(yPixel(destination.Y()));

        return translateTransition;
    }

    /**
     * Factory method for a ScaleTransition
     * @param duration duration of transition
     * @param scale to scale of transition
     * @return new ScaleTransition
     */
    private ScaleTransition makeScaleTransition(Duration duration, double scale) {
        var scaleTransition = new ScaleTransition();
        scaleTransition.setDuration(duration);

        scaleTransition.setFromX(actor.getScaleX());
        scaleTransition.setFromY(actor.getScaleY());

        scaleTransition.setToX(scale);
        scaleTransition.setToY(scale);

        return scaleTransition;
    }

}
