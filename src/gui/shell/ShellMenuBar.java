package gui.shell;

import gui.FxUserInterface;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.*;
import logic.board.GameLevel;
import logic.util.Log;

import java.io.File;
import java.io.FileReader;
import java.io.InputStreamReader;
import java.util.*;
import java.util.function.Consumer;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

/**
 * MenuBar displayed at the top of the program
 */
public class ShellMenuBar extends MenuBar {
    private static final String LEVEL_PATH = "gui/resources/resources/level";
    private final ToggleGroup selectedLevelItem = new ToggleGroup();

    private final ObjectProperty<Consumer<GameLevel>> onChooseLevel = new SimpleObjectProperty<>(Log::debug);
    private final ObjectProperty<EventHandler<ActionEvent>> onLoadLevel = new SimpleObjectProperty<>();
    private final ObjectProperty<EventHandler<ActionEvent>> onLoadEmptyLevel = new SimpleObjectProperty<>();
    private final ObjectProperty<EventHandler<ActionEvent>> onSaveLevel = new SimpleObjectProperty<>();

    /**
     * Instantiates new MenuBar
     */
    public ShellMenuBar() {
        var menuFile = new Menu("Level");

        var miChooseLevel = new Menu("Choose Level");
        miChooseLevel.getItems().addAll(makeLevelChooserItems(LEVEL_PATH));

        var miLoadLevelFile = new MenuItem("Load Level File");
        miLoadLevelFile.onActionProperty().bind(onLoadLevel);

        var miLoadEmptyLevel = new MenuItem("Create Empty Level");
        miLoadEmptyLevel.onActionProperty().bind(onLoadEmptyLevel);

        var miSaveLevelFile = new MenuItem("Save Current Level");
        miSaveLevelFile.onActionProperty().bind(onSaveLevel);


        menuFile.getItems().addAll(
                miChooseLevel,
                miLoadLevelFile,
                miLoadEmptyLevel,
                miSaveLevelFile
        );

        var menuPlayback = makePlaybackMenu(
                4, "Slow",
                2, "Normal",
                1, "Fast");

        this.getMenus().addAll(
                menuFile,
                menuPlayback
        );
    }

    /**
     * This generates the level selection accessible through the MenuBar.
     * It also generates the file readers for the level.
     * @implNote There a non-trivial differences between running the program as a jar
     * and in the IDE. You have to use diffrent implementation and detect what scenario
     * is currently happening.
     * @param levelPath path to the level directory
     * @return Level Menu items
     */
    private Collection<MenuItem> makeLevelChooserItems(String levelPath) {
        var items = new ArrayList<MenuItem>();

        try {
            List<File> levelFiles = new ArrayList<>();
            boolean notAJar = false;

            try {
                // first we use the jar method. We have to use the JarFile class to get a list of all files
                // contained in the jar.
                Iterator<JarEntry> dir = new JarFile(
                        new File(ShellMenuBar.class.getProtectionDomain()
                                .getCodeSource()
                                .getLocation()
                                .toURI()))
                        .entries()
                        .asIterator();

                // Then we iterate over all files and search for the level files.
                while (dir.hasNext()) {
                    JarEntry entry = dir.next();
                    Log.debug(entry);
                    // if we found a level, we add it to the list
                    if (entry.getName().contains(levelPath) && entry.getName().contains(".json")) {
                        Log.debug("found level %s\n", entry);
                        levelFiles.add(new File(entry.getName()));
                    }
                }


            } catch (Exception e) {
                // if the above code fails we can assume that the program is currently not being executed
                // as a jar, but probably in an IDE.
                Log.warning("Program is probably not being executed as a jar. Switch to non jar method.");

                notAJar = true;
                // again we add the files of the level directory to the list.
                File dir = new File(getClass().getResource("/" + levelPath).getPath());
                levelFiles = new ArrayList<>(List.of(dir.listFiles()));
            }

            Collections.sort(levelFiles);
            for (File file : levelFiles) {
                String fileName = file.getName().split("\\.")[0];

                // Again we have to differentiate between jar and IDE mode.
                GameLevel level = GameLevel.fromJson(notAJar
                        ? new FileReader(file.getPath())
                        : new InputStreamReader(
                            getClass().getResourceAsStream("/" + file.getPath().replaceAll("\\\\", "/"))));

                var levelItem = new RadioMenuItem(level.getName(fileName));
                levelItem.setToggleGroup(selectedLevelItem);
                levelItem.setOnAction(actionEvent -> {
                    onChooseLevel.get().accept(level);
                    Log.debug("Level consumer fired for " + level.getName(fileName));
                });

                items.add(levelItem);
            }
        } catch (Exception e) {
            Log.error(e);
        }

        return items;
    }

    /**
     * Generates new Playback Menu
     * @param slowModifier value of the slowest speed modifier
     * @param slowName name of the slowest speed modifier
     * @param mediumModifier value of the normal speed modifier
     * @param mediumName name of the normal speed modifier
     * @param fastModifier value of the slowest speed modifier
     * @param fastName name of the normal speed modifier
     * @return new playback Menu Node
     */
    private static Menu makePlaybackMenu(
            int slowModifier, String slowName,
            int mediumModifier, String mediumName,
            int fastModifier, String fastName) {
        var mPlayback = new Menu("Playback");
        var playbackToggleGroup = new ToggleGroup();

        var mriSkip = new RadioMenuItem("Skip Animations");
        mriSkip.setToggleGroup(playbackToggleGroup);
        mriSkip.setOnAction(actionEvent -> FxUserInterface.ANIMATION_DURATION_MODIFIER.set(0));

        var mriSlow = new RadioMenuItem(slowName);
        mriSlow.setToggleGroup(playbackToggleGroup);
        mriSlow.setOnAction(actionEvent -> FxUserInterface.ANIMATION_DURATION_MODIFIER.set(slowModifier));

        var mriNormal = new RadioMenuItem(mediumName);
        mriNormal.setToggleGroup(playbackToggleGroup);
        mriNormal.setOnAction(actionEvent -> FxUserInterface.ANIMATION_DURATION_MODIFIER.set(mediumModifier));
        mriNormal.setSelected(true);

        var mriFast = new RadioMenuItem(fastName);
        mriFast.setOnAction(actionEvent -> FxUserInterface.ANIMATION_DURATION_MODIFIER.set(fastModifier));
        mriFast.setToggleGroup(playbackToggleGroup);

        mPlayback.getItems().addAll(mriSlow, mriNormal, mriFast, mriSkip);
        return mPlayback;
    }

    /**
     * get EventHandler used for loading a new Level
     *
     * @return EventHandler on load level
     * @implNote required by javafx so value can be set in .fxml
     */
    public EventHandler<ActionEvent> getOnLoadLevel() {
        return onLoadLevel.get();
    }

    /**
     * EventHandler property for loading a new level
     *
     * @return Observable property of event handler
     * @implNote required by javafx so value can be set in .fxml
     */
    public ObjectProperty<EventHandler<ActionEvent>> onLoadLevelProperty() {
        return onLoadLevel;
    }

    /**
     * Set EventHandler for loading a new level
     *
     * @param action new event handler
     * @implNote required by javafx so value can be set in .fxml
     */
    public void setOnLoadLevel(EventHandler<ActionEvent> action) {
        onLoadLevel.set(actionEvent -> {
            action.handle(actionEvent);
            selectedLevelItem.selectToggle(null);
        });
    }

    /**
     * Gets on load empty level.
     *
     * @return the on load empty level
     */
    public EventHandler<ActionEvent> getOnLoadEmptyLevel() {
        return onLoadEmptyLevel.get();
    }

    /**
     * On load empty level property object property.
     *
     * @return the object property
     */
    public ObjectProperty<EventHandler<ActionEvent>> onLoadEmptyLevelProperty() {
        return onLoadEmptyLevel;
    }

    /**
     * Sets on load empty level.
     *
     * @param onLoadEmptyLevel the on load empty level
     */
    public void setOnLoadEmptyLevel(EventHandler<ActionEvent> onLoadEmptyLevel) {
        this.onLoadEmptyLevel.set(onLoadEmptyLevel);
    }

    /**
     * Gets on save level.
     *
     * @return the on save level
     */
    public EventHandler<ActionEvent> getOnSaveLevel() {
        return onSaveLevel.get();
    }

    /**
     * On save level property object property.
     *
     * @return the object property
     */
    public ObjectProperty<EventHandler<ActionEvent>> onSaveLevelProperty() {
        return onSaveLevel;
    }

    /**
     * Sets on save level.
     *
     * @param onSaveLevel the on save level
     */
    public void setOnSaveLevel(EventHandler<ActionEvent> onSaveLevel) {
        this.onSaveLevel.set(onSaveLevel);
    }

    /**
     * Gets on choose level.
     *
     * @return the on choose level
     */
    public Consumer<GameLevel> getOnChooseLevel() {
        return onChooseLevel.get();
    }

    /**
     * On choose level property object property.
     *
     * @return the object property
     */
    public ObjectProperty<Consumer<GameLevel>> onChooseLevelProperty() {
        return onChooseLevel;
    }

    /**
     * Sets on choose level.
     *
     * @param onChooseLevel the on choose level
     */
    public void setOnChooseLevel(Consumer<GameLevel> onChooseLevel) {
        this.onChooseLevel.set(onChooseLevel);
    }
}
