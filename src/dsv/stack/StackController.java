package dsv.stack;

import dsv.PrimaryInputFocus;
import javafx.animation.FadeTransition;
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;
import javafx.util.Duration;

import java.util.Stack;

public class StackController {

    private final Stack<Integer> stack = new Stack<>();
    private Rectangle peekedRectangle;

    @FXML
    private VBox stackContainer;

    @FXML
    private ScrollPane stackScrollPane;

    @FXML
    private StackPane stackContentRoot;

    @FXML
    private TextField valueField;

    @FXML
    private Label valueLabel;

    @FXML
    public void initialize() {
        stackContentRoot.minHeightProperty().bind(
                Bindings.createDoubleBinding(
                        () -> {
                            double viewportH = stackScrollPane.getViewportBounds().getHeight();
                            double stackH = stackContainer.prefHeight(-1);
                            if (viewportH <= 0) {
                                return Math.max(stackH, stackContainer.getHeight());
                            }
                            return Math.max(viewportH, stackH);
                        },
                        stackScrollPane.viewportBoundsProperty(),
                        stackContainer.heightProperty(),
                        stackContainer.getChildren()));
        PrimaryInputFocus.focusAndSelect(valueField);
    }

    /**
     * Pins the base of the stack (oldest block, bottom of the VBox) to the bottom of the viewport.
     * Smaller vvalue scrolls the view toward the top of the pile (newer pancakes).
     */
    private void scrollToShowBase() {
        Platform.runLater(() -> stackScrollPane.setVvalue(1.0));
    }

    @FXML
    void clear(ActionEvent event) {
        try {
            resetPeekHighlight();
            stack.clear();
            stackContainer.getChildren().clear();
            scrollToShowBase();
            valueLabel.setText("Stack cleared.");
        } finally {
            PrimaryInputFocus.focusAndSelect(valueField);
        }
    }

    @FXML
    void peek(ActionEvent event) {
        try {
            resetPeekHighlight();
            if (stack.isEmpty()) {
                valueLabel.setText("Stack is empty. Cannot peek.");
                return;
            }

            StackPane item = (StackPane) stackContainer.getChildren().get(0);
            Rectangle rect = (Rectangle) item.getChildren().get(0);
            rect.setStyle("-fx-fill: yellow; -fx-stroke: black;");
            peekedRectangle = rect;

            valueLabel.setText("Top of stack: " + stack.peek());
        } finally {
            PrimaryInputFocus.focusAndSelect(valueField);
        }
    }

    @FXML
    void pop(ActionEvent event) {
        try {
            resetPeekHighlight();
            if (stack.isEmpty()) {
                valueLabel.setText("Stack is empty. Cannot pop.");
                return;
            }

            int removed = stack.pop();
            StackPane item = (StackPane) stackContainer.getChildren().get(0);

            // Fade out animation
            FadeTransition fadeOut = new FadeTransition(Duration.millis(500), item);
            fadeOut.setFromValue(1);
            fadeOut.setToValue(0);
            fadeOut.setOnFinished(e -> {
                stackContainer.getChildren().remove(item);
                scrollToShowBase();
            });
            fadeOut.play();

            valueLabel.setText("Value popped: " + removed);
        } finally {
            PrimaryInputFocus.focusAndSelect(valueField);
        }
    }

    @FXML
    void push(ActionEvent event) {
        try {
            resetPeekHighlight();
            String input = valueField.getText().trim();

            if (input.isEmpty()) {
                valueLabel.setText("Please enter a value.");
                return;
            }
            try {
                int value = Integer.parseInt(input);
                stack.push(value);
                StackPane item = createStackItem(value);
                item.setOpacity(0);
                // VBox lays out first child at top, last at bottom: addFirst keeps newest on top, oldest at base.
                stackContainer.getChildren().addFirst(item);
                valueField.clear();

                // Fade in animation
                FadeTransition fadeIn = new FadeTransition(Duration.millis(500), item);
                fadeIn.setFromValue(0);
                fadeIn.setToValue(1);
                fadeIn.play();

                scrollToShowBase();
                valueLabel.setText("Value pushed: " + value);
            } catch (NumberFormatException e) {
                valueLabel.setText("Please enter an integer.");
            }
        } finally {
            PrimaryInputFocus.focusAndSelect(valueField);
        }
    }

    private StackPane createStackItem(int value) {
        Rectangle rect = new Rectangle(200, 100);
        rect.setArcWidth(10);
        rect.setArcHeight(10);
        rect.setStyle("-fx-fill: lightblue; -fx-stroke: black;");

        Text text = new Text(String.valueOf(value));

        StackPane item = new StackPane(rect, text);
        item.setPrefSize(200, 100);
        item.setMinSize(200, 100);
        item.setMaxSize(200, 100);
        return item;
    }

    private void resetPeekHighlight() {
        if (peekedRectangle != null) {
            peekedRectangle.setStyle("-fx-fill: lightblue; -fx-stroke: black;");
            peekedRectangle = null;
        }
    }

}
