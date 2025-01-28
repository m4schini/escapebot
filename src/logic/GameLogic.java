package logic;

import logic.action.Action;
import logic.action.ActionType;
import logic.action.Actions;
import logic.board.Board;
import logic.board.GameLevel;
import logic.procedure.Procedure;

import static logic.util.Log.debug;
import static logic.util.Log.warning;

/**
 * Should be single point between logic and gui
 *
 * @author orcid.org/0009-0008-3023-1228 (inf104662)
 */
public class GameLogic {
    /**
     * Connection to gui
     */
    private final GUIConnector gui;

    /**
     * Board instance
     */
    private final Board board;
    /**
     * Bot instance
     */
    private final Bot bot;
    /**
     * Start direction of bot
     */
    private final Direction startDirection;

    /**
     * You have to initialize a new controller if you load a new level, otherwise
     * start position and direction will be wrong.
     *
     * @param gui the gui
     * @param level Game Level
     */
    public GameLogic(GUIConnector gui, GameLevel level) {
        this.gui = gui;
        this.bot = new Bot(level.getBoard());

        this.board = level.getBoard();
        this.startDirection = level.getStartBotDirection();

        gui.onLogicInitialized(level);
    }

    /**
     * Gets board
     * @return copy of internal board instance
     */
    public Board getBoard() {
        return new Board(this.board);
    }

    /**
     * Gets level
     * @return new GameLevel instance
     */
    public GameLevel getLevel() {
        return new GameLevel(board, startDirection);
    }

    /**
     * Executes procedures, handles exceptions and fills action queue
     * @param root main Procedure
     * @param p1 child procedure (not allowed to have recursive instructions)
     * @param p2 child procedure (not allowed to have recursive instructions)
     */
    public void execute(Procedure root, Procedure p1, Procedure p2) {
        final Actions actionQueue = new Actions(new Action(ActionType.START, bot.getPosition(), bot.getDirection()));

        // every execution starts, also when procedure fails
        try {
            actionQueue.addAll(bot.execute(root, p1, p2));

            debug("Bot executed procedures (0[%d], 1[%d]. 2[%d])\n",
                    root.size(), p1.size(), p2.size());

            if (!actionQueue.successful()) {
                warning("Level incomplete.");
            } else {
                debug("Level complete.");
            }

            // send actions to gui
            gui.play(actionQueue);
        } catch (Exception e) {
            gui.panic(e);
        }
    }
}
