package gui.components;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.Node;
import javafx.scene.layout.VBox;

/**
 * SwitchPane can display a virtual front and backside. It will not reconstruct either side, simply remove and add the
 * nodes from the scene graph as needed.
 *
 * @author orcid.org/0009-0008-3023-1228 (inf104662)
 */
public class SwitchPane extends VBox {
    private final ObjectProperty<Node> front;
    private final ObjectProperty<Node> back;
    private boolean isFrontVisible = true;

    public SwitchPane() {
        this(new VBox(), new VBox());
    }

    public SwitchPane(Node front, Node back) {
        this.front = new SimpleObjectProperty<>();
        this.front.set(front);
        this.front.addListener((observableValue, node, newValue) -> {
            if (isFrontVisible) {
                this.showFront();
            }
        });
        this.back = new SimpleObjectProperty<>();
        this.back.set(back);
        this.back.addListener((observableValue, node, newValue) -> {
            if (!isFrontVisible) {
                this.showBack();
            }
        });

        this.showFront();
    }

    public Node getFront() {
        return front.get();
    }

    public ObjectProperty<Node> frontProperty() {
        return front;
    }

    public void setFront(Node front) {
        this.front.set(front);
    }

    public Node getBack() {
        return back.get();
    }

    public ObjectProperty<Node> backProperty() {
        return back;
    }

    public void setBack(Node back) {
        this.back.set(back);
    }

    public void showFront() {
        this.getChildren().clear();
        this.getChildren().add(front.get());
        isFrontVisible = true;
    }

    public void showBack() {
        this.getChildren().clear();
        this.getChildren().add(back.get());
        isFrontVisible = false;
    }

    public void switchSides() {
        if (isFrontVisible) {
            showBack();
        } else {
            showFront();
        }
    }
}
