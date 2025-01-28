package logic.logic;

import logic.action.Action;
import logic.action.ActionType;
import logic.action.Actions;
import org.junit.Assert;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

public class ActionsTest {
    private static final Action ACTION_START = new Action(ActionType.START, -1, -1);
    private static final Action ACTION_START_EXECUTE_P1 = new Action(ActionType.START_EXECUTE_P1, -1, -1);
    private static final Action ACTION_STOP_EXECUTE_P1 = new Action(ActionType.STOP_EXECUTE_P1, -1, -1);

    @Test
    public void test_constructor_empty() {
        Actions actions = new Actions();
        Assert.assertEquals("actions should be empty", 0, actions.size());
        Assert.assertTrue("actions should be empty", actions.isEmpty());
    }

    @Test
    public void test_constructor_varArgs() {
        Actions actions = new Actions(
                ACTION_START,
                ACTION_START_EXECUTE_P1,
                ACTION_STOP_EXECUTE_P1);
        Assert.assertEquals("actions size should be 3", 3, actions.size());
    }

    @Test
    public void test_constructor_collection() {
        var listOfActions = List.of(ACTION_START, ACTION_START_EXECUTE_P1, ACTION_STOP_EXECUTE_P1);
        Actions actions = new Actions(listOfActions);

        Assert.assertEquals(listOfActions.size(), actions.size());
        Assert.assertEquals(listOfActions, actions.stream().toList());
    }

    @Test
    public void test_with() {
        Action added = ACTION_START_EXECUTE_P1;

        Actions actions = new Actions(ACTION_START);
        Actions expected = new Actions(ACTION_START, added);

        Assert.assertEquals(expected, actions.with(added));
    }

    @Test(expected = IllegalArgumentException.class)
    public void test_with_null() {
        Actions actions = new Actions(ACTION_START);

        actions.with(null);

        Assert.assertTrue("Test shouldn't have reached this point", false);
    }

    @Test
    public void test_last() {
        Actions actions = new Actions();
        Assert.assertEquals(null, actions.last());

        actions = new Actions(ACTION_START);
        Assert.assertEquals(ACTION_START, actions.last());

        actions = new Actions(ACTION_START, ACTION_START_EXECUTE_P1);
        Assert.assertEquals(ACTION_START_EXECUTE_P1, actions.last());
    }

}
