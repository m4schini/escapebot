package logic;

import logic.action.Actions;
import logic.board.GameLevel;

/**
 * The interface User interface.
 *
 * @author orcid.org/0009-0008-3023-1228 (inf104662)
 */
public interface GUIConnector {

    /**
     * On game start.
     *
     * @param level the service
     */
    void onLogicInitialized(GameLevel level);

    /**
     * Execute on won game
     */
    void onGameWin();

    /**
     * Execute this on lost game
     */
    void onGameLose();

    /**
     * Execute this on lost game
     * @param reasonForLoss message describing why game was lost
     */
    void onGameLose(String reasonForLoss);

    /**
     * Play a list of actions.
     *
     * @param actions the actions
     */
    void play(Actions actions);

    /**
     * Escalate an exception to the user with a human readable message
     * @param exception exception you want to panic
     */
    void panic(Exception exception);
}
