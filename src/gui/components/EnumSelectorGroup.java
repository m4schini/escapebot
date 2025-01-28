package gui.components;

import gui.resources.Texture;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.control.ToggleGroup;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import logic.Readable;

import java.util.EnumSet;

import static logic.util.Log.*;

/**
 * EnumSelectorGroup is a vertical instructions of {@link TextureCard} with enum values.
 * The {@link #SelectedProperty()} exposes selected enum.
 *
 * @param <E> class of enum type with options
 * @author orcid.org/0009-0008-3023-1228 (inf104662)
 */
public class EnumSelectorGroup<E extends Enum<E>> extends VBox {
    /**
     * Selected Enum
     */
    private final ObjectProperty<E> selectedProp;

    /**
     * Instantiates a new V selector group.
     *
     * @param prop    selected enum
     * @param options set of available options for selector
     */
    public EnumSelectorGroup(ObjectProperty<E> prop, EnumSet<E> options) {
        this.getStyleClass().add("selector");
        this.selectedProp = prop;
        ToggleGroup toggleGroup = new ToggleGroup();

        // Style stuff
        HBox.setHgrow(this, Priority.ALWAYS);

        // Add toggle cards with texture as children
        try {
            for (E option : options) {
                var card = new TextureCard(Texture.of(option), toggleGroup);

                if (option instanceof Readable me) {
                    Tooltip enumInfo = new Tooltip();
                    enumInfo.setText(me.asReadableString());
                    enumInfo.setWrapText(true);
                    enumInfo.maxWidth(10);

                    Tooltip.install(card, enumInfo);
                }

                this.getChildren().add(card);
            }
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException(
                    String.format(
                            "The enum '%s' does not have Textures associated with it. " +
                            "EnumSelectorGroup can't display enums without textures.",
                            getElementType(options)),
                    e);
        }

        // translate selected toggle to a selected enum value and set selectedProperty
        toggleGroup.selectedToggleProperty().addListener((observableValue, oldValue, newValue) -> {
            if (newValue instanceof TextureCard) {
                var value = E.valueOf(getElementType(options), ((TextureCard) newValue).getTexture().name());
                selectedProp.set(value);
            } else {
                throw new IllegalStateException("should be togglecard");
            }
        });

        selectedProp.addListener((observableValue, before, after) -> {
            debug("Selector<%s> Selected: %s%n", after.getClass().getName(), after);
        });

        // select first card
        ((TextureCard) this.getChildren().get(0)).setSelected(true);
    }

    /**
     * Instantiates a new Vertical selector group.
     *
     * @param enumClass the enum class
     */
    public EnumSelectorGroup(Class<E> enumClass) {
        this(new SimpleObjectProperty<>(), EnumSet.allOf(enumClass));
    }

    /**
     * Initiates a new EnumSelector of given Enum Set
     * @param set enumset
     */
    public EnumSelectorGroup(EnumSet<E> set) {
        this(new SimpleObjectProperty<>(), set);
    }

    /**
     * Property of currently selected enum constant
     *
     * @return selected enum constant
     */
    public ReadOnlyObjectProperty<E> SelectedProperty() {
        return selectedProp;
    }

    /**
     * Returns Class of enumSet's type
     * @param enumSet target
     * @param <T> enum type
     * @see <a href="https://stackoverflow.com/a/41283598/9146426">Source</a>
     * @return enum type of set
     */
    public static <T extends Enum<T>> Class<T> getElementType(EnumSet<T> enumSet) {
        if (enumSet.isEmpty())
            enumSet = EnumSet.complementOf(enumSet);
        return enumSet.iterator().next().getDeclaringClass();
    }
}
