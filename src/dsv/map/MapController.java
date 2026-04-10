package dsv.map;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

import java.util.*;

public class MapController {

    private static final int BUCKET_COUNT = 8;

    // HashMap structure: array of lists
    private final List<List<Entry>> table = new ArrayList<>();

    @FXML private HBox bucket0;
    @FXML private HBox bucket1;
    @FXML private HBox bucket2;
    @FXML private HBox bucket3;
    @FXML private HBox bucket4;
    @FXML private HBox bucket5;
    @FXML private HBox bucket6;
    @FXML private HBox bucket7;

    @FXML
    private TextField keyField;

    @FXML
    private TextField valueField;

    @FXML
    private Label valueLabel;

    // ---- ENTRY CLASS ----
    private static class Entry {
        String key;
        String value;

        Entry(String k, String v) {
            key = k;
            value = v;
        }
    }
    // ----------------------

    @FXML
    public void initialize() {
        for (int i = 0; i < BUCKET_COUNT; i++) {
            table.add(new ArrayList<>());
        }
    }

    // Hash function
    private int getIndex(String key) {
        return Math.abs(key.hashCode()) % BUCKET_COUNT;
    }

    // Get correct bucket UI
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

    @FXML
    void add(ActionEvent event) {
        String key = keyField.getText();
        String value = valueField.getText();

        if (key.isEmpty() || value.isEmpty()) {
            valueLabel.setText("Enter key and value.");
            return;
        }

        int index = getIndex(key);
        List<Entry> bucket = table.get(index);

        // Check if key exists → update
        for (Entry e : bucket) {
            if (e.key.equals(key)) {
                e.value = value;
                refreshUI();
                valueLabel.setText("Updated key: " + key);
                return;
            }
        }

        // Otherwise insert
        bucket.add(new Entry(key, value));
        refreshUI();

        valueLabel.setText("Added (" + key + " → " + value + ") at bucket " + index);
    }

    @FXML
    void remove(ActionEvent event) {
        String key = keyField.getText();

        int index = getIndex(key);
        List<Entry> bucket = table.get(index);

        Iterator<Entry> it = bucket.iterator();

        while (it.hasNext()) {
            Entry e = it.next();
            if (e.key.equals(key)) {
                it.remove();
                refreshUI();
                valueLabel.setText("Removed key: " + key);
                return;
            }
        }

        valueLabel.setText("Key not found.");
    }

    @FXML
    void search(ActionEvent event) {
        String key = keyField.getText();

        int index = getIndex(key);
        List<Entry> bucket = table.get(index);

        for (Entry e : bucket) {
            if (e.key.equals(key)) {
                valueLabel.setText("Found: " + e.value + " (bucket " + index + ")");
                return;
            }
        }

        valueLabel.setText("Key not found.");
    }

    @FXML
    void clear(ActionEvent event) {
        for (List<Entry> bucket : table) {
            bucket.clear();
        }

        refreshUI();
        valueLabel.setText("Map cleared.");
    }

    private void refreshUI() {
        for (int i = 0; i < BUCKET_COUNT; i++) {
            HBox uiBucket = getBucketUI(i);
            uiBucket.getChildren().clear();

            for (Entry e : table.get(i)) {
                uiBucket.getChildren().add(createNode(e));
            }
        }
    }

    private StackPane createNode(Entry e) {
        Rectangle rect = new Rectangle(80, 40);
        rect.setFill(Color.LIGHTGREEN);
        rect.setStroke(Color.BLACK);

        Label label = new Label(e.key + " → " + e.value);

        StackPane stack = new StackPane(rect, label);
        return stack;
    }
}