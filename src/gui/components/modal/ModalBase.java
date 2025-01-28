package gui.components.modal;

import gui.resources.Texture;
import javafx.scene.Scene;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

/**
 * Modal Base, handles common Logic of {@link BoardProblemModal}.
 *
 * @author orcid.org/0009-0008-3023-1228 (inf104662)
 */
public abstract class ModalBase extends Stage {
    /**
     * Title of modal
     */
    private static final String MODAL_TITLE = "Problem found... ";
    /**
     * Height of modal
     */
    protected static final int HEIGHT = 300;
    /**
     * Width of modal
     */
    protected static final int WIDTH = 235;

    /**
     * Initializes new modal
     * @param modality modality
     */
    public ModalBase(Modality modality) {
        this.initModality(modality);
        this.setTitle(MODAL_TITLE);
        this.setAlwaysOnTop(true);
        this.initStyle(StageStyle.UTILITY);
    }

    /**
     * Sets default config for scene
     * @param scene scene to configure
     */
    protected void initScene(Scene scene) {
        this.setScene(scene);
        getScene().getStylesheets().add(Texture.STYLESHEET_LOCATION);
    }
}
