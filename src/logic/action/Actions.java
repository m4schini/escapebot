package logic.action;

import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

/**
 * Helper class for easier handling and creation of Actions
 */
public class Actions implements Collection<Action> {
    /**
     * Actual list of actions
     */
    private final List<Action> actionList;

    /**
     * Initiates empty list of actions
     */
    public Actions() {
        actionList = new ArrayList<>();
    }

    /**
     * Initiates a new list of given actions
     * @param actionList varargs of actions
     */
    public Actions(Action... actionList) {
        this(List.of(actionList));
    }

    /**
     * Initiates a new list of given actions
     * @param actionList list of actions
     */
    public Actions(Collection<Action> actionList) {
        this();
        this.actionList.addAll(actionList);
    }

    /**
     * Convenience helper method allows add to actions and immediate return in one line
     * @param actionType actionType added to end of list
     * @throws IllegalArgumentException if actionType is null
     * @return list with added actionType
     */
    public Actions with(Action actionType) {
        if (actionType == null) throw new IllegalArgumentException("null not allowed");
        actionList.add(actionType);
        return this;
    }

    /**
     * Get the last actions of this actionsList
     * @return last element in actions list, null if empty
     */
    public Action last() {
        return actionList.isEmpty()
                ? null
                : actionList.get(actionList.size() - 1);
    }

    /**
     * A collection of actionTypes was successful if the last action is EXIT_SUCCESSFUL and no other action failed.
     * @return true, if actions are successful
     */
    public boolean successful() {
        return actionList.stream().noneMatch(Action::failed)
            && actionList.stream().filter(action -> action.getType() == ActionType.EXIT_SUCCESSFUL).count() == 1;
    }

    /**
     * Analyses list if actions failed.
     * @return true, if failed
     */
    public boolean failed() {
        return getFailed() != null;
    }

    /**
     * Analyses list if actions failed.
     * @return null, if successful. Failed action if failed
     */
    public Action getFailed() {
        return actionList.stream().filter(Action::failed).findFirst().orElse(null);
    }

    @Override
    public int size() {
        return actionList.size();
    }

    @Override
    public boolean isEmpty() {
        return actionList.isEmpty();
    }

    @Override
    public boolean contains(Object o) {
        return actionList.contains(o);
    }

    @Override
    public Iterator<Action> iterator() {
        return actionList.iterator();
    }

    @Override
    public Object[] toArray() {
        return actionList.toArray();
    }

    @Override
    public <T> T[] toArray(T[] a) {
        return actionList.toArray(a);
    }

    @Override
    public boolean add(Action actionType) {
        return actionList.add(actionType);
    }

    @Override
    public boolean remove(Object o) {
        return actionList.remove(o);
    }

    @Override
    public boolean containsAll(Collection<?> c) {
        return actionList.containsAll(c);
    }

    @Override
    public boolean addAll(Collection<? extends Action> c) {
        return actionList.addAll(c);
    }

    @Override
    public boolean removeAll(Collection<?> c) {
        return actionList.removeAll(c);
    }

    @Override
    public boolean retainAll(Collection<?> c) {
        return actionList.retainAll(c);
    }

    @Override
    public void clear() {
        actionList.clear();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Actions other) {
            return this.actionList.equals(other.actionList);
        } else {
            return false;
        }
    }

    @Override
    public String toString() {
        return new Gson().toJson(actionList);
    }
}
