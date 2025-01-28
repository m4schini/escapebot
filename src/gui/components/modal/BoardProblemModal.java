package gui.components.modal;

import gui.resources.Texture;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.stage.Modality;
import logic.board.Board;
import logic.util.Log;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Creates a modal with a list of Problems relating to the Board.
 *
 * @author orcid.org/0009-0008-3023-1228 (inf104662)
 */
public class BoardProblemModal extends ModalBase {
    /**
     * CSS class name
     */
    private static final String CSS_CLASS_NAME = "board-problem-modal";
    /**
     * "Doesn't exist" message format.
     * 1. Param: {@link logic.board.FieldType} that doesn't exist
     */
    private final static String MSG_FORMAT_DOESNT_EXIST = "You need at least one field of type: '%s'.";
    /**
     * "Too Many" message format.
     * 1. Param: The {@link logic.board.FieldType} that exists too often
     */
    private final static String MSG_FORMAT_TOO_MANY = "You have to many fields with type: '%s'.";
    /**
     * "Not Reachable" message format.
     * 1. Param: {@link logic.board.FieldType} that isn't reachable
     * 2. Param: {@link Integer} x coordinate
     * 3. Param: {@link Integer} y coordinate
     */
    private final static String MSG_FORMAT_NOT_REACHABLE = "%s at (%d/%d) is not reachable from the start position.";
    /**
     * "Solution too large" message format. No parameter required.
     */
    private final static String MSG_SOLUTION_TOO_LARGE =
            "The solution EscapeBot found for this level was too large too fit the Procedures.\n" +
                    "It could be possible that the level is still solvable, but EscapeBot cannot confirm that.";

    /**
     * Initiates a new modal of given problems.
     * @param problems varargs of problems
     */
    public BoardProblemModal(Board.Problem... problems) {
        this(List.of(problems));
    }

    /**
     * Initiates a new modal of given problems
     * @param problems collection of problems
     */
    public BoardProblemModal(Collection<Board.Problem> problems) {
        super(Modality.WINDOW_MODAL);

        // log all problems
        Log.debug("PROBLEMS FOUND: ");
        for (Board.Problem p : problems) {
            Log.debug(p);
        }

        var list = new VBox();
        list.getChildren().addAll(generateProblemList(problems));
        initScene(makeScene(list));
    }

    /**
     * Generates nodes for list of problems
     * @param problems list of problems
     * @return parent node of list
     */
    private static List<Node> generateProblemList(Collection<Board.Problem> problems) {
        List<Node> nodes = new ArrayList<>();
        for (Board.Problem problem : problems) {
            var row = new HBox();

            var fieldImage = Texture.of(problem.fieldType()).ImageView(32);
            HBox.setMargin(fieldImage, new Insets(0, 8, 0,0));

            var pos = problem.position();
            String problemDesc = switch (problem.problemType()) {
                case DOESNT_EXIST -> String.format(MSG_FORMAT_DOESNT_EXIST, problem.fieldType());
                case TOO_MANY -> String.format(MSG_FORMAT_TOO_MANY, problem.fieldType());
                case NOT_REACHABLE -> String.format(MSG_FORMAT_NOT_REACHABLE,
                        problem.fieldType(), pos.X(), pos.Y());
                case SOLUTION_TOO_BIG -> MSG_SOLUTION_TOO_LARGE;
            };
            var problemLabel = new Text(problemDesc);
            problemLabel.setWrappingWidth(WIDTH - fieldImage.getFitWidth() - 16);
            HBox.setMargin(problemLabel, new Insets(0, 8, 0,0));

            //row.setBackground(new Background(new BackgroundFill(Color.GRAY, CornerRadii.EMPTY, Insets.EMPTY)));
            row.setBorder(new Border(new BorderStroke(
                    Color.BLACK, Color.WHITE, Color.WHITE, Color.WHITE,
                    BorderStrokeStyle.SOLID, BorderStrokeStyle.NONE, BorderStrokeStyle.NONE, BorderStrokeStyle.NONE,
                    CornerRadii.EMPTY, BorderWidths.DEFAULT, Insets.EMPTY
            )));
            row.setPadding(new Insets(8));
            row.getChildren().addAll(
                    fieldImage,
                    problemLabel
            );
            nodes.add(row);
        }
        return nodes;
    }

    /**
     * Initiates the Scene for the Modal
     * @param list modal
     * @return new scene
     */
    protected static Scene makeScene(Region list) {
        var mainBox = new VBox();
        VBox.setVgrow(list, Priority.ALWAYS);
        mainBox.getChildren().addAll( list );

        ScrollPane scroll = new ScrollPane();
        scroll.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        scroll.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        scroll.setContent(mainBox);
        scroll.getStyleClass().add(CSS_CLASS_NAME);

        mainBox.maxWidthProperty().bind(scroll.widthProperty());

        return new Scene(scroll, WIDTH, HEIGHT);
    }
}
