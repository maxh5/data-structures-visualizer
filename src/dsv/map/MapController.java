package dsv.map;

import javafx.animation.*;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.util.Duration;

import java.util.*;

public class MapController {

    private static final int BUCKET_COUNT = 8;
    private static final double ANIMATION_SPEED = 0.6; // seconds

    private final List<List<Entry>> table = new ArrayList<>();
    private Rectangle highlightedRectangle;

    @FXML private HBox controlBox;

    @FXML private HBox bucket0;
    @FXML private HBox bucket1;
    @FXML private HBox bucket2;
    @FXML private HBox bucket3;
    @FXML private HBox bucket4;
    @FXML private HBox bucket5;
    @FXML private HBox bucket6;
    @FXML private HBox bucket7;

    @FXML private StackPane indexPane0;
    @FXML private StackPane indexPane1;
    @FXML private StackPane indexPane2;
    @FXML private StackPane indexPane3;
    @FXML private StackPane indexPane4;
    @FXML private StackPane indexPane5;
    @FXML private StackPane indexPane6;
    @FXML private StackPane indexPane7;

    @FXML private TextField keyField;
    @FXML private TextField valueField;
    @FXML private Label valueLabel;

    private static class Entry {
        String key;
        String value;
        Entry(String k, String v) {
            key = k;
            value = v;
        }
    }

    @FXML
    public void initialize() {
        for (int i = 0; i < BUCKET_COUNT; i++) {
            table.add(new ArrayList<>());
        }
    }

    private int getIndex(String key) {
        return Math.abs(key.hashCode()) % BUCKET_COUNT;
    }

    private HBox getBucketUI(int index) {
        return switch (index) {
            case 0 -> bucket0;
            case 1 -> bucket1;
            case 2 -> bucket2;
            case 3 -> bucket3;
            case 4 -> bucket4;
            case 5 -> bucket5;
            case 6 -> bucket6;
            case 7 -> bucket7;
            default -> null;
        };
    }

    private StackPane getIndexPane(int index) {
        return switch (index) {
            case 0 -> indexPane0;
            case 1 -> indexPane1;
            case 2 -> indexPane2;
            case 3 -> indexPane3;
            case 4 -> indexPane4;
            case 5 -> indexPane5;
            case 6 -> indexPane6;
            case 7 -> indexPane7;
            default -> null;
        };
    }

    private void lockUI() {
        if (controlBox != null) controlBox.setDisable(true);
    }
    private void unlockUI() {
        if (controlBox != null) controlBox.setDisable(false);
    }

    private void highlightIndexPane(int index, SequentialTransition seq) {
        StackPane pane = getIndexPane(index);
        
        PauseTransition highlight = new PauseTransition(Duration.seconds(ANIMATION_SPEED));
        highlight.setOnFinished(e -> pane.setBackground(new Background(new BackgroundFill(Color.LIGHTBLUE, CornerRadii.EMPTY, Insets.EMPTY))));
        
        PauseTransition pause = new PauseTransition(Duration.seconds(ANIMATION_SPEED));
        
        PauseTransition removeHighlight = new PauseTransition(Duration.seconds(0.1));
        removeHighlight.setOnFinished(e -> pane.setBackground(null));

        seq.getChildren().addAll(highlight, pause, removeHighlight);
    }

    @FXML
    void add(ActionEvent event) {
        String key = keyField.getText();
        String value = valueField.getText();

        if (key.isEmpty() || value.isEmpty()) {
            valueLabel.setText("Enter key and value.");
            return;
        }

        resetHighlight();
        lockUI();
        
        int index = getIndex(key);
        List<Entry> bucket = table.get(index);
        HBox uiBucket = getBucketUI(index);

        SequentialTransition seq = new SequentialTransition();

        // 1. Show Hash Calc
        PauseTransition startCalc = new PauseTransition(Duration.seconds(0.1));
        startCalc.setOnFinished(e -> valueLabel.setText("Hash(\"" + key + "\") % " + BUCKET_COUNT + " = " + index));
        seq.getChildren().add(startCalc);

        // 2. Highlight Bucket Index
        highlightIndexPane(index, seq);

        // 3. Traverse bucket visually
        for (int i = 0; i < bucket.size(); i++) {
            final int currI = i;
            final Entry e = bucket.get(i);
            
            PauseTransition highlightNode = new PauseTransition(Duration.seconds(ANIMATION_SPEED));
            highlightNode.setOnFinished(ev -> {
                StackPane item = (StackPane) uiBucket.getChildren().get(currI);
                Rectangle rect = (Rectangle) item.getChildren().get(0);
                rect.setFill(Color.ORANGE);
            });
            seq.getChildren().add(highlightNode);

            PauseTransition checkNode = new PauseTransition(Duration.seconds(ANIMATION_SPEED));
            checkNode.setOnFinished(ev -> {
                StackPane item = (StackPane) uiBucket.getChildren().get(currI);
                Rectangle rect = (Rectangle) item.getChildren().get(0);
                if (e.key.equals(key)) {
                    // Update value
                    e.value = value;
                    Label lbl = (Label) item.getChildren().get(1);
                    lbl.setText(key + " → " + value);
                    rect.setFill(Color.YELLOW);
                    highlightedRectangle = rect;
                    valueLabel.setText("Updated key: " + key);
                } else {
                    rect.setFill(Color.LIGHTGREEN);
                }
            });
            seq.getChildren().add(checkNode);

            if (e.key.equals(key)) {
                // Done.
                seq.setOnFinished(ev -> unlockUI());
                seq.play();
                return;
            }
        }

        // 4. If not found, add it
        PauseTransition addNode = new PauseTransition(Duration.seconds(ANIMATION_SPEED));
        addNode.setOnFinished(ev -> {
            bucket.add(new Entry(key, value));
            StackPane newNode = createNode(new Entry(key, value));
            newNode.setOpacity(0);
            uiBucket.getChildren().add(newNode);
            
            FadeTransition ft = new FadeTransition(Duration.seconds(ANIMATION_SPEED), newNode);
            ft.setToValue(1.0);
            ft.setOnFinished(e -> {
                valueLabel.setText("Added (" + key + " → " + value + ") at bucket " + index);
                unlockUI();
            });
            ft.play();
        });
        seq.getChildren().add(addNode);

        seq.play();
    }

    @FXML
    void remove(ActionEvent event) {
        String key = keyField.getText();
        if (key.isEmpty()) {
            valueLabel.setText("Enter key to remove.");
            return;
        }

        resetHighlight();
        lockUI();

        int index = getIndex(key);
        List<Entry> bucket = table.get(index);
        HBox uiBucket = getBucketUI(index);

        SequentialTransition seq = new SequentialTransition();

        PauseTransition startCalc = new PauseTransition(Duration.seconds(0.1));
        startCalc.setOnFinished(e -> valueLabel.setText("Hash(\"" + key + "\") % " + BUCKET_COUNT + " = " + index));
        seq.getChildren().add(startCalc);

        highlightIndexPane(index, seq);

        for (int i = 0; i < bucket.size(); i++) {
            final int currI = i;
            final Entry e = bucket.get(i);
            
            PauseTransition highlightNode = new PauseTransition(Duration.seconds(ANIMATION_SPEED));
            highlightNode.setOnFinished(ev -> {
                StackPane item = (StackPane) uiBucket.getChildren().get(currI);
                Rectangle rect = (Rectangle) item.getChildren().get(0);
                rect.setFill(Color.ORANGE);
            });
            seq.getChildren().add(highlightNode);

            if (e.key.equals(key)) {
                PauseTransition removeNode = new PauseTransition(Duration.seconds(ANIMATION_SPEED));
                removeNode.setOnFinished(ev -> {
                    StackPane item = (StackPane) uiBucket.getChildren().get(currI);
                    FadeTransition ft = new FadeTransition(Duration.seconds(ANIMATION_SPEED), item);
                    ft.setToValue(0);
                    ft.setOnFinished(event2 -> {
                        bucket.remove(currI);
                        uiBucket.getChildren().remove(currI);
                        valueLabel.setText("Removed key: " + key);
                        unlockUI();
                    });
                    ft.play();
                });
                seq.getChildren().add(removeNode);
                seq.play();
                return;
            } else {
                PauseTransition revertNode = new PauseTransition(Duration.seconds(ANIMATION_SPEED));
                revertNode.setOnFinished(ev -> {
                    StackPane item = (StackPane) uiBucket.getChildren().get(currI);
                    Rectangle rect = (Rectangle) item.getChildren().get(0);
                    rect.setFill(Color.LIGHTGREEN);
                });
                seq.getChildren().add(revertNode);
            }
        }

        PauseTransition notFound = new PauseTransition(Duration.seconds(0.1));
        notFound.setOnFinished(e -> {
            valueLabel.setText("Key not found.");
            unlockUI();
        });
        seq.getChildren().add(notFound);

        seq.play();
    }

    @FXML
    void search(ActionEvent event) {
        String key = keyField.getText();
        if (key.isEmpty()) {
            valueLabel.setText("Enter key to search.");
            return;
        }

        resetHighlight();
        lockUI();

        int index = getIndex(key);
        List<Entry> bucket = table.get(index);
        HBox uiBucket = getBucketUI(index);

        SequentialTransition seq = new SequentialTransition();

        PauseTransition startCalc = new PauseTransition(Duration.seconds(0.1));
        startCalc.setOnFinished(e -> valueLabel.setText("Hash(\"" + key + "\") % " + BUCKET_COUNT + " = " + index));
        seq.getChildren().add(startCalc);

        highlightIndexPane(index, seq);

        for (int i = 0; i < bucket.size(); i++) {
            final int currI = i;
            final Entry e = bucket.get(i);
            
            PauseTransition highlightNode = new PauseTransition(Duration.seconds(ANIMATION_SPEED));
            highlightNode.setOnFinished(ev -> {
                StackPane item = (StackPane) uiBucket.getChildren().get(currI);
                Rectangle rect = (Rectangle) item.getChildren().get(0);
                rect.setFill(Color.ORANGE);
            });
            seq.getChildren().add(highlightNode);

            if (e.key.equals(key)) {
                PauseTransition foundNode = new PauseTransition(Duration.seconds(ANIMATION_SPEED));
                foundNode.setOnFinished(ev -> {
                    StackPane item = (StackPane) uiBucket.getChildren().get(currI);
                    Rectangle rect = (Rectangle) item.getChildren().get(0);
                    rect.setFill(Color.YELLOW);
                    highlightedRectangle = rect;
                    valueLabel.setText("Found: " + e.value + " (bucket " + index + ")");
                    unlockUI();
                });
                seq.getChildren().add(foundNode);
                seq.play();
                return;
            } else {
                PauseTransition revertNode = new PauseTransition(Duration.seconds(ANIMATION_SPEED));
                revertNode.setOnFinished(ev -> {
                    StackPane item = (StackPane) uiBucket.getChildren().get(currI);
                    Rectangle rect = (Rectangle) item.getChildren().get(0);
                    rect.setFill(Color.LIGHTGREEN);
                });
                seq.getChildren().add(revertNode);
            }
        }

        PauseTransition notFound = new PauseTransition(Duration.seconds(0.1));
        notFound.setOnFinished(e -> {
            valueLabel.setText("Key not found.");
            unlockUI();
        });
        seq.getChildren().add(notFound);

        seq.play();
    }

    @FXML
    void clear(ActionEvent event) {
        resetHighlight();
        for (List<Entry> bucket : table) {
            bucket.clear();
        }

        for (int i = 0; i < BUCKET_COUNT; i++) {
            HBox uiBucket = getBucketUI(i);
            uiBucket.getChildren().clear();
        }
        valueLabel.setText("Map cleared.");
    }

    private StackPane createNode(Entry e) {
        Rectangle rect = new Rectangle(80, 40);
        rect.setFill(Color.LIGHTGREEN);
        rect.setStroke(Color.BLACK);

        Label label = new Label(e.key + " → " + e.value);

        StackPane stack = new StackPane(rect, label);
        return stack;
    }

    private void resetHighlight() {
        if (highlightedRectangle != null) {
            highlightedRectangle.setFill(Color.LIGHTGREEN);
            highlightedRectangle = null;
        }
    }
}