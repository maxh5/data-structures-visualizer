package dsv.stack;

import javafx.animation.FadeTransition;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
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
    private TextField valueField;

    @FXML
    private Label valueLabel;

    @FXML
    void clear(ActionEvent event) {
        resetPeekHighlight();
        stack.clear();
        stackContainer.getChildren().clear();
        valueLabel.setText("Stack cleared.");
    }

    @FXML
    void peek(ActionEvent event) {
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
    }

    @FXML
    void pop(ActionEvent event) {
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
        fadeOut.setOnFinished(e -> stackContainer.getChildren().remove(item));
        fadeOut.play();

        valueLabel.setText("Value popped: " + removed);
    }

    @FXML
    void push(ActionEvent event) {
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
            stackContainer.getChildren().addFirst(item);
            valueField.clear();

            // Fade in animation
            FadeTransition fadeIn = new FadeTransition(Duration.millis(500), item);
            fadeIn.setFromValue(0);
            fadeIn.setToValue(1);
            fadeIn.play();

            valueLabel.setText("Value pushed: " + value);
        } catch (NumberFormatException e) {
            valueLabel.setText("Please enter an integer.");
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
