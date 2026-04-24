package dsv.linkedList;

import javafx.animation.FadeTransition;
import javafx.animation.ParallelTransition;
import javafx.animation.SequentialTransition;
import javafx.animation.TranslateTransition;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.shape.Circle;
import javafx.scene.text.Text;
import javafx.util.Duration;

import java.util.LinkedList;

public class LinkedListController {

    private final LinkedList<Integer> linkedList = new LinkedList<>();
    private Circle highlightedCircle;

    @FXML
    private HBox linkedListContainer;

    @FXML
    private TextField valueField;

    @FXML
    private TextField indexField;

    @FXML
    private Label valueLabel;

    @FXML
    public void initialize() {
        // Start with empty list (just Null pointer)
        linkedListContainer.getChildren().add(createNullNode());
        valueLabel.setText("LinkedList initialized.");
    }

    @FXML
    void clear(ActionEvent event) {
        resetHighlight();
        linkedList.clear();
        linkedListContainer.getChildren().clear();
        linkedListContainer.getChildren().add(createNullNode());
        valueLabel.setText("LinkedList cleared.");
    }

    @FXML
    void add(ActionEvent event) {
        resetHighlight();
        String input = valueField.getText().trim();

        if (input.isEmpty()) {
            valueLabel.setText("Please enter a value.");
            return;
        }

        try {
            int value = Integer.parseInt(input);
            linkedList.add(value);

            // Create new node and arrow
            StackPane newNode = createNode(value);
            Text newArrow = createArrow();

            // Setup for fade in
            newNode.setOpacity(0);
            newArrow.setOpacity(0);

            // Add to container before the null node
            int insertIndex = linkedListContainer.getChildren().size() - 1;
            linkedListContainer.getChildren().add(insertIndex, newNode);
            linkedListContainer.getChildren().add(insertIndex + 1, newArrow);

            // Fade in animation
            ParallelTransition fade = new ParallelTransition(
                createFade(newNode, 0, 1),
                createFade(newArrow, 0, 1)
            );
            fade.play();

            valueField.clear();
            valueLabel.setText("Appended value: " + value);

        } catch (NumberFormatException e) {
            valueLabel.setText("Please enter a valid integer.");
        }
    }

    @FXML
    void insert(ActionEvent event) {
        resetHighlight();
        String valInput = valueField.getText().trim();
        String idxInput = indexField.getText().trim();

        if (valInput.isEmpty() || idxInput.isEmpty()) {
            valueLabel.setText("Please enter value and index.");
            return;
        }

        try {
            int value = Integer.parseInt(valInput);
            int index = Integer.parseInt(idxInput);

            if (index < 0 || index > linkedList.size()) {
                valueLabel.setText("Index out of bounds! Range: 0 to " + linkedList.size());
                return;
            }

            linkedList.add(index, value);

            StackPane newNode = createNode(value);
            Text newArrow = createArrow();

            // Set initial state for "move up" animation
            newNode.setTranslateY(60);
            newArrow.setTranslateY(60);
            newNode.setOpacity(0);
            newArrow.setOpacity(0);

            // Insert into the HBox (each logical node is 2 visual nodes: circle + arrow)
            int uiIndex = index * 2;
            linkedListContainer.getChildren().add(uiIndex, newNode);
            linkedListContainer.getChildren().add(uiIndex + 1, newArrow);

            // Shift right animation for elements that were moved
            ParallelTransition shiftAnim = new ParallelTransition();
            for (int i = uiIndex + 2; i < linkedListContainer.getChildren().size(); i++) {
                Node n = linkedListContainer.getChildren().get(i);
                TranslateTransition tt = new TranslateTransition(Duration.millis(400), n);
                // Assume node width (40) + arrow width (~20) + spacing (10s)
                // HBox naturally shifts them, so we start them negatively offset
                // Wait, if we just added them, HBox instantly shifts the right elements.
                // We should offset them to the left by the width of (node+arrow+spacing) and slide to 0.
                n.setTranslateX(-80); 
                tt.setToX(0);
                shiftAnim.getChildren().add(tt);
            }

            // Move up and fade in animation for new elements
            ParallelTransition appearAnim = new ParallelTransition();
            
            TranslateTransition moveUpNode = new TranslateTransition(Duration.millis(400), newNode);
            moveUpNode.setToY(0);
            TranslateTransition moveUpArrow = new TranslateTransition(Duration.millis(400), newArrow);
            moveUpArrow.setToY(0);

            appearAnim.getChildren().addAll(
                moveUpNode, moveUpArrow,
                createFade(newNode, 0, 1), createFade(newArrow, 0, 1)
            );

            // Play shift, then appear
            SequentialTransition seq = new SequentialTransition();
            if (!shiftAnim.getChildren().isEmpty()) {
                seq.getChildren().add(shiftAnim);
            }
            seq.getChildren().add(appearAnim);
            seq.play();

            valueField.clear();
            indexField.clear();
            valueLabel.setText("Inserted value " + value + " at index " + index);

        } catch (NumberFormatException e) {
            valueLabel.setText("Please enter valid integers.");
        }
    }

    @FXML
    void remove(ActionEvent event) {
        resetHighlight();
        String idxInput = indexField.getText().trim();

        if (idxInput.isEmpty()) {
            valueLabel.setText("Please enter an index to remove.");
            return;
        }

        try {
            int index = Integer.parseInt(idxInput);

            if (index < 0 || index >= linkedList.size()) {
                valueLabel.setText("Index out of bounds! Range: 0 to " + (linkedList.size() - 1));
                return;
            }

            int removedValue = linkedList.remove(index);
            int uiIndex = index * 2;

            Node nodeToRemove = linkedListContainer.getChildren().get(uiIndex);
            Node arrowToRemove = linkedListContainer.getChildren().get(uiIndex + 1);

            // Drop down and fade out
            TranslateTransition dropNode = new TranslateTransition(Duration.millis(400), nodeToRemove);
            dropNode.setToY(60);
            TranslateTransition dropArrow = new TranslateTransition(Duration.millis(400), arrowToRemove);
            dropArrow.setToY(60);

            ParallelTransition disappearAnim = new ParallelTransition(
                dropNode, dropArrow,
                createFade(nodeToRemove, 1, 0), createFade(arrowToRemove, 1, 0)
            );

            disappearAnim.setOnFinished(e -> {
                linkedListContainer.getChildren().removeAll(nodeToRemove, arrowToRemove);

                // HBox naturally shifts remaining elements left, let's offset and animate
                ParallelTransition slideLeft = new ParallelTransition();
                for (int i = uiIndex; i < linkedListContainer.getChildren().size(); i++) {
                    Node n = linkedListContainer.getChildren().get(i);
                    n.setTranslateX(80);
                    TranslateTransition tt = new TranslateTransition(Duration.millis(400), n);
                    tt.setToX(0);
                    slideLeft.getChildren().add(tt);
                }
                
                if (!slideLeft.getChildren().isEmpty()) {
                    slideLeft.play();
                }
            });

            disappearAnim.play();

            indexField.clear();
            valueLabel.setText("Removed value " + removedValue + " from index " + index);

        } catch (NumberFormatException e) {
            valueLabel.setText("Please enter a valid integer for index.");
        }
    }

    @FXML
    void search(ActionEvent event) {
        resetHighlight();
        String idxInput = indexField.getText().trim();

        if (idxInput.isEmpty()) {
            valueLabel.setText("Please enter an index to search for.");
            return;
        }

        try {
            int targetIndex = Integer.parseInt(idxInput);

            if (targetIndex < 0 || targetIndex >= linkedList.size()) {
                valueLabel.setText("Index out of bounds! Range: 0 to " + (linkedList.size() - 1));
                return;
            }

            SequentialTransition traversal = new SequentialTransition();
            
            for (int i = 0; i <= targetIndex; i++) {
                int uiIndex = i * 2;
                StackPane node = (StackPane) linkedListContainer.getChildren().get(uiIndex);
                Circle circle = (Circle) node.getChildren().get(0);
                
                // Highlight node
                FadeTransition flash = new FadeTransition(Duration.millis(300), circle);
                flash.setFromValue(1.0);
                flash.setToValue(0.4);
                flash.setCycleCount(2);
                flash.setAutoReverse(true);
                
                final int currentIndex = i;
                flash.setOnFinished(e -> {
                    if (currentIndex == targetIndex) {
                        circle.setStyle("-fx-fill: yellow; -fx-stroke: black; -fx-stroke-width: 2;");
                        highlightedCircle = circle;
                        valueLabel.setText("Found value " + linkedList.get(targetIndex) + " at index " + targetIndex);
                    }
                });
                
                traversal.getChildren().add(flash);
                
                // Highlight arrow (just pause for pacing)
                if (i < targetIndex) {
                    javafx.animation.PauseTransition pause = new javafx.animation.PauseTransition(Duration.millis(300));
                    traversal.getChildren().add(pause);
                }
            }
            
            valueLabel.setText("Searching for index " + targetIndex + "...");
            traversal.play();
            
        } catch (NumberFormatException e) {
            valueLabel.setText("Please enter a valid integer for index.");
        }
    }

    private StackPane createNode(int value) {
        Circle circle = new Circle(25);
        circle.setStyle("-fx-fill: lightgreen; -fx-stroke: black; -fx-stroke-width: 2;");

        Text text = new Text(String.valueOf(value));
        text.setStyle("-fx-font-size: 16; -fx-font-weight: bold;");

        return new StackPane(circle, text);
    }

    private Text createArrow() {
        Text arrow = new Text("→");
        arrow.setStyle("-fx-font-size: 30; -fx-font-weight: bold; -fx-fill: darkgray;");
        return arrow;
    }

    private Text createNullNode() {
        Text nullNode = new Text("∅");
        nullNode.setStyle("-fx-font-size: 30; -fx-font-weight: bold; -fx-fill: darkred;");
        return nullNode;
    }

    private FadeTransition createFade(Node node, double from, double to) {
        FadeTransition fade = new FadeTransition(Duration.millis(400), node);
        fade.setFromValue(from);
        fade.setToValue(to);
        return fade;
    }

    private void resetHighlight() {
        if (highlightedCircle != null) {
            highlightedCircle.setStyle("-fx-fill: lightgreen; -fx-stroke: black; -fx-stroke-width: 2;");
            highlightedCircle = null;
        }
    }
}
