package dsv.set;

import javafx.animation.FadeTransition;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.util.Duration;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class SetController {

    private final Set<Integer> set = new HashSet<>();
    private final Map<Integer, StackPane> nodeMap = new HashMap<>();

    @FXML
    private Pane setPane;

    @FXML
    private TextField valueField;

    @FXML
    private Label valueLabel;

    @FXML
    void add(ActionEvent event) {
        String input = valueField.getText();

        try {
            int value = Integer.parseInt(input);

            if (set.contains(value)) {
                valueLabel.setText("Value already exists in set.");
                return;
            }

            set.add(value);

            StackPane node = createNode(value);
            node.setOpacity(0);

            // Random placement
            double x = Math.random() * (setPane.getWidth() - 60);
            double y = Math.random() * (setPane.getHeight() - 60);

            node.setLayoutX(x);
            node.setLayoutY(y);

            setPane.getChildren().add(node);
            nodeMap.put(value, node);

            // Fade in animation
            FadeTransition fadeIn = new FadeTransition(Duration.millis(500), node);
            fadeIn.setFromValue(0);
            fadeIn.setToValue(1);
            fadeIn.play();

            valueLabel.setText("Added: " + value);

        } catch (NumberFormatException e) {
            valueLabel.setText("Invalid input.");
        }
    }

    @FXML
    void remove(ActionEvent event) {
        String input = valueField.getText();

        try {
            int value = Integer.parseInt(input);

            if (!set.contains(value)) {
                valueLabel.setText("Value not found.");
                return;
            }

            set.remove(value);

            StackPane node = nodeMap.get(value);
            nodeMap.remove(value);

            // Fade out animation
            FadeTransition fadeOut = new FadeTransition(Duration.millis(500), node);
            fadeOut.setFromValue(1);
            fadeOut.setToValue(0);
            fadeOut.setOnFinished(e -> setPane.getChildren().remove(node));
            fadeOut.play();

            valueLabel.setText("Removed: " + value);

        } catch (NumberFormatException e) {
            valueLabel.setText("Invalid input.");
        }
    }

    @FXML
    void search(ActionEvent event) {
        String input = valueField.getText();

        try {
            int value = Integer.parseInt(input);

            if (!set.contains(value)) {
                valueLabel.setText("Value not found.");
                return;
            }

            StackPane node = nodeMap.get(value);

            // Highlight node
            node.setStyle("-fx-border-color: red; -fx-border-width: 3;");

            valueLabel.setText("Found: " + value);

        } catch (NumberFormatException e) {
            valueLabel.setText("Invalid input.");
        }
    }

    @FXML
    void clear(ActionEvent event) {
        set.clear();
        nodeMap.clear();
        setPane.getChildren().clear();

        valueLabel.setText("Set cleared.");
    }

    private StackPane createNode(int value) {
        Circle circle = new Circle(25);
        circle.setFill(Color.LIGHTBLUE);
        circle.setStroke(Color.BLACK);

        Label label = new Label(String.valueOf(value));

        StackPane stack = new StackPane();
        stack.getChildren().addAll(circle, label);

        return stack;
    }
}