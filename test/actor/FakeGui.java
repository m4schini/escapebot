package actor;

import logic.action.Action;
import logic.GUIConnector;
import logic.board.GameLevel;
import logic.action.Actions;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class FakeGui implements GUIConnector {

    private final List<Action> actionTypeList = new ArrayList<>();

    @Override
    public void onLogicInitialized(GameLevel level) {

    }

    @Override
    public void onGameWin() {

    }

    @Override
    public void onGameLose() {

    }

    @Override
    public void onGameLose(String reasonForLoss) {

    }

    @Override
    public void play(Actions actions) {
        setActions(actions);
        System.out.print("FAKEGUI PLAY: \n");
        actions.forEach(action -> System.out.printf("%s, \n", action));
        System.out.println();
    }

    public List<Action> getActions() {
        return new ArrayList<>(actionTypeList);
    }

    public void setActions(Collection<Action> actionTypes) {
        // Adding all items of actionTypes to this actionlist to derefernce from actual instructions
        // (If only reference, reset function will also reset this instructions.
        this.actionTypeList.clear();
        this.actionTypeList.addAll(actionTypes);
    }

    public void panic(Exception exception) {

    }
}
