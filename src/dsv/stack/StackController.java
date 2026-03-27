package dsv.stack;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;

import java.util.Stack;

public class StackController {

    private final Stack<Integer> stack = new Stack<>();

    @FXML
    private VBox stackContainer;

    @FXML
    private TextField valueField;

    @FXML
    private Label valueLabel;

    @FXML
    void clear(ActionEvent event) {
        stack.clear();
        stackContainer.getChildren().clear();
        valueLabel.setText("Stack cleared.");
    }

    @FXML
    void peek(ActionEvent event) {
        if (stack.isEmpty()) {
            valueLabel.setText("Stack is empty. Cannot peek.");
            return;
        }
        valueLabel.setText("Top of stack: " + stack.peek());
    }

    @FXML
    void pop(ActionEvent event) {
        if (stack.isEmpty()) {
            valueLabel.setText("Stack is empty. Cannot pop.");
            return;
        }

        int removed = stack.pop();
        stackContainer.getChildren().remove(0);
        valueLabel.setText("Value popped: " + removed);
    }

    @FXML
    void push(ActionEvent event) {
        String input = valueField.getText().trim();
 
        if (input.isEmpty()) {
            valueLabel.setText("Please enter a value.");
            return;
        }
        try {
            int value = Integer.parseInt(input);
            stack.push(value);
            stackContainer.getChildren().addFirst(createStackItem(value));
            valueField.clear();
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

}
