package gui.components;

import gui.resources.Texture;
import javafx.scene.control.RadioButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.image.ImageView;

/**
 * TextureCard is a Radio button with a texture instead of text.
 * @author orcid.org/0009-0008-3023-1228 (inf104662)
 */
public class TextureCard extends RadioButton implements Cloneable {
    private static final double PREF_HEIGHT = 67F;
    
    private Texture texture;

    /**
     * Instantiates a new Texture card.
     *
     * @param texture texture for this card
     * @param group   toggle group for radio buttons
     */
    public TextureCard(Texture texture, ToggleGroup group) {
        this(group);
        this.setTexture(texture);
    }

    /**
     * Instantiates a new Texture card.
     *
     * @param group toggle group for radio buttons
     */
    public TextureCard(ToggleGroup group) {
        this.setToggleGroup(group);

        this.prefWidthProperty().bind(this.prefHeightProperty());
        this.setPrefHeight(PREF_HEIGHT);

        // convert styling to custom css class
        this.getStyleClass().remove("radio-button");
        this.getStyleClass().add("texture-card");

        this.selectedProperty().addListener((observable, wasSelected, isSelected) -> {
            this.setPrefHeight(isSelected ? PREF_HEIGHT - 3 : PREF_HEIGHT);
        });
    }



    /**
     * Gets texture.
     *
     * @return the texture
     */
    public Texture getTexture() {
        return texture;
    }

    /**
     * Set texture of this card.
     * null parameter will clear texture
     *
     * @param texture the texture
     */
    public void setTexture(Texture texture) {
        this.texture = texture;
        if (texture == null) {
            this.setGraphic(null);
        } else {
            var iv = new ImageView(texture.Image());
            iv.fitHeightProperty().bind(iv.fitWidthProperty());
            iv.fitWidthProperty().bind(this.prefWidthProperty());
            this.setGraphic(iv);
        }
    }

    /**
     * Clear texture.
     */
    public void clearTexture() {
        this.setTexture(null);
    }



    @Override
    public TextureCard clone() {
        try {
            return (TextureCard) super.clone();
        } catch (CloneNotSupportedException e) {
            throw new AssertionError();
        }
    }
}
