package gui.components.game;

import gui.components.TextureCard;

import static logic.util.Log.*;

/**
 * A ProcedureField is part of
 *
 * @author orcid.org/0009-0008-3023-1228 (inf104662)
 */
class ProcedureField extends TextureCard {
    private static final String CSS_CLASS_NAME = "procedure-field";
    /**
     * The Position.
     */
    private final int position;
    /**
     * Grid this field belongs to
     */
    private final ProcedureGrid parent;

    /**
     * Instantiates a new Field.
     *
     * @param position the position
     */
    public ProcedureField(int position, ProcedureGrid parent) {
        super(parent.getToggleGroup());
        this.parent = parent;
        this.setId(parent.getName() + 'f' + position);
        this.getStyleClass().add(CSS_CLASS_NAME);
        this.position = position;

        this.setOnMouseClicked(event -> {
            switch (event.getButton()) {
                case PRIMARY -> parent.offerInstruction(position, parent.selectedInstruction().get());
                case SECONDARY -> clearInstruction();
                case MIDDLE -> parent.addInstruction(position, parent.selectedInstruction().get());
                default -> debug("%s[%s] clicked with undefined mouse button (%s) %n",
                        parent.getName(),
                        position,
                        event.getButton());
            }

            event.consume();
        });
    }

    /**
     * Overriding this method with an empty body prevents the
     * radio selection of this field by mouse clicks.
     *
     */
    @Override
    public void fire() {
        // super.fire();
    }

    /**
     * Clear instruction.
     */
    void clearInstruction() {
        if (position >= parent.getInstructionsCount() || position < 0) {
            warning("field has no instruction");
        } else {
            parent.removeInstruction(position);
            debug("procedure changed: cleared field[%s]%n", position);
        }
    }
}
