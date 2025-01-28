package gui.components;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.Button;

import static logic.util.Log.*;

/**
 * Button that switches between an abstract front and back side with custom actionHandlers on either side
 * @author orcid.org/0009-0008-3023-1228 (inf104662)
 */
public class SwitchButton extends Button {
    // front side properties
    private String frontText;
    private final ObjectProperty<EventHandler<ActionEvent>> onFrontOpen;
    private final ObjectProperty<EventHandler<ActionEvent>> onFrontClose;

    // back side properties
    private String backText;
    private final ObjectProperty<EventHandler<ActionEvent>> onBackOpen;
    private final ObjectProperty<EventHandler<ActionEvent>> onBackClose;


    /**
     * Property of button state. If true, front side of button is "visible", else back side
     */
    private final BooleanProperty isFront;

    public SwitchButton() {
        isFront = new SimpleBooleanProperty();
        onFrontOpen = new SimpleObjectProperty<>(actionEvent -> {});
        onFrontClose = new SimpleObjectProperty<>(actionEvent -> {});
        onBackOpen = new SimpleObjectProperty<>(actionEvent -> {});
        onBackClose = new SimpleObjectProperty<>(actionEvent -> {});

        isFront.addListener((observableValue, wasFront, isFront) -> {
            if (isFront) {
                this.setText(frontText);
            } else {
                this.setText(backText);
            }
        });

        isFront.set(true);

        this.setOnAction((actionEvent) -> {
            debug("SwitchButton triggered");
            if (isFront.get()) {
                onBackClose.get().handle(actionEvent);
                onFrontOpen.get().handle(actionEvent);
            } else {
                onFrontClose.get().handle(actionEvent);
                onBackOpen.get().handle(actionEvent);
            }
            isFront.set(!isFront.get());
        });
    }

    /**
     * Gets the value of the property front text
     * @return text for "front side" of button
     */
    public String getTextFront() {
        return frontText;
    }

    /**
     * Sets the value of the property front text
     */
    public void setTextFront(String text) {
        debug("front text set to '%s'%n", text);
        this.frontText = text;
        if (isFront.get()) this.setText(text);
    }

    /**
     * Gets the value of the property back text
     * @return text for "front side" of button
     */
    public String getTextBack() {
        return backText;
    }

    /**
     * Sets the value of the property back text
     */
    public void setTextBack(String text) {
        debug("back text set to '%s'%n", text);
        this.backText = text;
        if (!isFront.get()) this.setText(text);
    }

    /**
     * Sets actionHandler that is used when "front side" is active
     * @param action EventHandler for button press
     */
    public void setOnFrontOpen(EventHandler<ActionEvent> action) {
        debug("onFrontOpenAction set");
        onFrontOpen.set(action);
    }

    /**
     * Gets actionHandler that is used when "front side" is active
     */
    public EventHandler<ActionEvent> getOnFrontOpen() {
        return onFrontOpen.get();
    }

    /**
     * Sets actionHandler that is used when "front side" is active
     * @param action EventHandler for button press
     */
    public void setOnFrontClose(EventHandler<ActionEvent> action) {
        debug("onFrontCloseAction set");
        onFrontClose.set(action);
    }

    /**
     * Gets actionHandler that is used when "front side" is active
     */
    public EventHandler<ActionEvent> getOnFrontClose() {
        return onFrontOpen.get();
    }

    /**
     * Sets actionHandler that is used when "back side" is active
     * @param action EventHandler for button press
     */
    public void setOnBackOpen(EventHandler<ActionEvent> action) {
        debug("onBackOpenAction set");
        onBackOpen.set(action);
    }

    /**
     * Gets actionHandler that is used when "back side" is active
     */
    public EventHandler<ActionEvent> getOnBackOpen() {
        return onBackOpen.get();
    }

    /**
     * Sets actionHandler that is used when "back side" is active
     * @param action EventHandler for button press
     */
    public void setOnBackClose(EventHandler<ActionEvent> action) {
        debug("onBackCloseAction set");
        onBackClose.set(action);
    }

    /**
     * Gets actionHandler that is used when "back side" is active
     */
    public EventHandler<ActionEvent> getOnBackClose() {
        return onBackClose.get();
    }
}
