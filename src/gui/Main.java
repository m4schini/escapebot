package gui;

import com.google.gson.Gson;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.stage.Stage;
import logic.util.Log;

import java.io.File;
import java.net.URISyntaxException;
import java.nio.file.FileSystem;


/**
 * The Main Class. Entry class for jre.
 *
 * This program uses ENV Variables for additional configuration.
 * <ul>
 *     <li>THEME - Used to force a certain theme (set of textures) ({@link gui.resources.Texture})</li>
 *     <li>VERBOSE - Forces output of debug information ({@link logic.util.Log})</li>
 * </ul>
 *
 * @author orcid.org/0009-0008-3023-1228 (inf104662)
 */
public class Main extends Application {
    /**
     * The name of the game.
     */
    public static final String NAME_OF_GAME = "EscapeBot";

    @Override
    public void start(Stage primaryStage) throws Exception {
        // Catching uncaught/unexpected exception
        Thread.currentThread().setUncaughtExceptionHandler((Thread th, Throwable ex)-> {
            ex.printStackTrace();
            Log.error(ex);
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Unexpected Error");
            alert.setContentText("This shouldn't have happened");
            alert.showAndWait();
        });


        FxUserInterface.WINDOW_HEIGHT = primaryStage.heightProperty();
        FxUserInterface.WINDOW_WIDTH = primaryStage.widthProperty();

        Parent root = FXMLLoader.load(Main.class.getResource("shell/shell.fxml"));
        primaryStage.setTitle("EscapeBot");
        primaryStage.setScene(new Scene(root, 1024, 750));
        primaryStage.setMinWidth(1024);
        primaryStage.setMinHeight(700);
        primaryStage.show();
    }

    /**
     * The entry point of application.
     *
     * @param args the input arguments
     */
    public static void main(String[] args) {
        launch(args);
    }
}
