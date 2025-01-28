package gui.animation;

import javafx.animation.Transition;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.util.Duration;

/**
 * Acts as a mock transition with the only job of executing a runnable/eventhandler
 * when getting executed in a transition.
 *
 * @author orcid.org/0009-0008-3023-1228 (inf104662)
 */
public class ActionTransition extends Transition {

    /**
     * Initiates a new ActionTransition of given Runnable
     * @param action runnable that gets executed
     */
    public ActionTransition(Runnable action) {
        this(actionEvent -> action.run());
    }

    /**
     * Initiates a new ActionTransition of given eventHandler
     * @param eventHandler event handler
     */
    public ActionTransition(EventHandler<ActionEvent> eventHandler) {
        this.setDelay(Duration.ZERO);
        this.setCycleDuration(Duration.ZERO);

        this.setOnFinished(eventHandler);
    }

    /**
     * Does nothing in this implementation
     * @param v does nothing
     */
    @Override
    protected void interpolate(double v) {

    }
}
