package dsv.queue;

import dsv.PrimaryInputFocus;
import javafx.animation.FadeTransition;
import javafx.animation.ParallelTransition;
import javafx.animation.TranslateTransition;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;
import javafx.util.Duration;

import java.util.Queue;
import java.util.LinkedList;

public class QueueController {
    
    private final Queue<Integer> queue = new LinkedList<>();
    private Rectangle peekedRectangle;

    @FXML
    private HBox queueContainer;

    @FXML
    private TextField valueField;

    @FXML
    private Label valueLabel;

    @FXML
    public void initialize() {
        PrimaryInputFocus.focusAndSelect(valueField);
    }

    @FXML
    void clear(ActionEvent event) {
        try {
            resetPeekHighlight();
            queue.clear();
            queueContainer.getChildren().clear();
            valueLabel.setText("Queue cleared.");
        } finally {
            PrimaryInputFocus.focusAndSelect(valueField);
        }
    }

    @FXML
    void dequeue(ActionEvent event) {
        try {
            resetPeekHighlight();
            if (queue.isEmpty()) {
                valueLabel.setText("Queue is empty. Cannot dequeue.");
                return;
            }

            int removed = queue.poll();
        
        // Get the rightmost element (last in the HBox since we add new items at index 0)
        int lastIndex = queueContainer.getChildren().size() - 1;
        StackPane removedItem = (StackPane) queueContainer.getChildren().get(lastIndex);
        
        // Create fade out animation for the removed item
        FadeTransition fade = new FadeTransition(Duration.millis(300), removedItem);
        fade.setFromValue(1.0);
        fade.setToValue(0.0);
        
        // Create shift animations for remaining items (move right to fill the gap)
        ParallelTransition shiftAnimations = new ParallelTransition();
        
        for (int i = 0; i < lastIndex; i++) {
            StackPane item = (StackPane) queueContainer.getChildren().get(i);
            TranslateTransition translate = new TranslateTransition(Duration.millis(300), item);
            translate.setByX(130); // 120 item width + 10 spacing
            shiftAnimations.getChildren().add(translate);
        }
        
        // Combine fade and shift animations
        ParallelTransition allAnimations = new ParallelTransition(fade, shiftAnimations);
        allAnimations.setOnFinished(finishEvent -> {
            // Remove the item after animation completes
            queueContainer.getChildren().remove(removedItem);

            // Reset translate transforms for remaining items
            for (int i = 0; i < queueContainer.getChildren().size(); i++) {
                StackPane item = (StackPane) queueContainer.getChildren().get(i);
                item.setTranslateX(0);
            }
            updateHeadIndicator();
        });
        
        allAnimations.play();
        valueLabel.setText("Value dequeued: " + removed);
        } finally {
            PrimaryInputFocus.focusAndSelect(valueField);
        }
    }

    @FXML
    void enqueue(ActionEvent event) {
        try {
            resetPeekHighlight();
            String input = valueField.getText().trim();

            if (input.isEmpty()) {
                valueLabel.setText("Please enter a value.");
                return;
            }

            try {
                int value = Integer.parseInt(input);
                queue.add(value);

                StackPane newItem = createQueueItem(value);
                newItem.setOpacity(0); // Start invisible
                queueContainer.getChildren().add(0, newItem);

                // Fade in animation
                FadeTransition fade = new FadeTransition(Duration.millis(300), newItem);
                fade.setFromValue(0.0);
                fade.setToValue(1.0);
                fade.play();

                valueField.clear();
                valueLabel.setText("Value enqueued: " + value);
                updateHeadIndicator();
            } catch (NumberFormatException e) {
                valueLabel.setText("Please enter an integer.");
            }
        } finally {
            PrimaryInputFocus.focusAndSelect(valueField);
        }
    }

    private StackPane createQueueItem(int value) {
        Rectangle rect = new Rectangle(120, 60);
        rect.setArcWidth(10);
        rect.setArcHeight(10);
        rect.setStyle("-fx-fill: lightblue; -fx-stroke: black;");

        Text text = new Text(String.valueOf(value));

        Label headMarker = new Label("\u2605");
        headMarker.getStyleClass().add("queue-head-marker");
        headMarker.setStyle("-fx-text-fill: #c9a227; -fx-font-size: 16px;");
        headMarker.setMouseTransparent(true);
        headMarker.setVisible(false);
        headMarker.setManaged(false);
        Tooltip.install(headMarker, new Tooltip("Head of queue (front) — next to dequeue"));

        StackPane item = new StackPane(rect, text, headMarker);
        StackPane.setAlignment(rect, Pos.CENTER);
        StackPane.setAlignment(text, Pos.CENTER);
        StackPane.setAlignment(headMarker, Pos.TOP_CENTER);
        headMarker.setTranslateY(-6);

        item.setPrefSize(120, 72);
        item.setMinSize(120, 72);
        item.setMaxSize(120, 72);
        return item;
    }

    /** Head (front) is the rightmost cell in this visualization. */
    private void updateHeadIndicator() {
        int n = queueContainer.getChildren().size();
        for (int i = 0; i < n; i++) {
            StackPane pane = (StackPane) queueContainer.getChildren().get(i);
            Label marker = (Label) pane.lookup(".queue-head-marker");
            if (marker == null) {
                continue;
            }
            boolean isHead = (i == n - 1);
            marker.setVisible(isHead);
            marker.setManaged(isHead);
        }
    }

    @FXML
    void peek(ActionEvent event) {
        try {
            resetPeekHighlight();
            if (queue.isEmpty()) {
                valueLabel.setText("Queue is empty. Cannot peek.");
                return;
            }

            int lastIndex = queueContainer.getChildren().size() - 1;
            StackPane item = (StackPane) queueContainer.getChildren().get(lastIndex);
            Rectangle rect = (Rectangle) item.getChildren().get(0);
            rect.setStyle("-fx-fill: yellow; -fx-stroke: black;");
            peekedRectangle = rect;

            valueLabel.setText("Front of queue: " + queue.peek());
        } finally {
            PrimaryInputFocus.focusAndSelect(valueField);
        }
    }

    private void resetPeekHighlight() {
        if (peekedRectangle != null) {
            peekedRectangle.setStyle("-fx-fill: lightblue; -fx-stroke: black;");
            peekedRectangle = null;
        }
    }

}
