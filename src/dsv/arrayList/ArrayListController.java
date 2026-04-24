package dsv.arrayList;

import javafx.animation.FadeTransition;
import javafx.animation.ParallelTransition;
import javafx.animation.TranslateTransition;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;
import javafx.util.Duration;

import java.util.ArrayList;

public class ArrayListController {

    private final ArrayList<Integer> arrayList = new ArrayList<>();
    private int capacity = 5; // Initial capacity
    private Rectangle highlightedRectangle;

    @FXML
    private HBox arrayListContainer;

    @FXML
    private TextField valueField;

    @FXML
    private TextField indexField;

    @FXML
    private Label valueLabel;

    @FXML
    public void initialize() {
        // Initialize empty visual slots
        for (int i = 0; i < capacity; i++) {
            arrayListContainer.getChildren().add(createEmptySlot(i));
        }
    }

    @FXML
    void clear(ActionEvent event) {
        resetHighlight();
        arrayList.clear();
        arrayListContainer.getChildren().clear();
        for (int i = 0; i < capacity; i++) {
            arrayListContainer.getChildren().add(createEmptySlot(i));
        }
        valueLabel.setText("ArrayList cleared.");
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
            
            if (arrayList.size() >= capacity) {
                resizeAndAdd(value);
            } else {
                int addIndex = arrayList.size();
                arrayList.add(value);
                
                VBox slot = (VBox) arrayListContainer.getChildren().get(addIndex);
                updateSlotWithValue(slot, value);
                
                // Fade in animation
                StackPane stackPane = (StackPane) slot.getChildren().get(0);
                Text text = (Text) stackPane.getChildren().get(1);
                text.setOpacity(0);
                
                FadeTransition fade = new FadeTransition(Duration.millis(300), text);
                fade.setFromValue(0.0);
                fade.setToValue(1.0);
                fade.play();

                valueField.clear();
                valueLabel.setText("Value " + value + " appended at index " + addIndex);
            }
        } catch (NumberFormatException e) {
            valueLabel.setText("Please enter an integer for the value.");
        }
    }

    @FXML
    void insert(ActionEvent event) {
        resetHighlight();
        String valInput = valueField.getText().trim();
        String idxInput = indexField.getText().trim();

        if (valInput.isEmpty() || idxInput.isEmpty()) {
            valueLabel.setText("Please enter both value and index.");
            return;
        }

        try {
            int value = Integer.parseInt(valInput);
            int index = Integer.parseInt(idxInput);

            if (index < 0 || index > arrayList.size()) {
                valueLabel.setText("Index out of bounds! Valid range: 0 to " + arrayList.size());
                return;
            }

            if (arrayList.size() >= capacity) {
                // If we need to resize, just resize first, then we can insert (simplified no-animation for resize step)
                resize();
            }

            arrayList.add(index, value);
            
            // Animate shift right
            ParallelTransition shiftAnimations = new ParallelTransition();
            for (int i = index; i < arrayList.size() - 1; i++) {
                VBox slot = (VBox) arrayListContainer.getChildren().get(i);
                StackPane stackPane = (StackPane) slot.getChildren().get(0);
                Text text = (Text) stackPane.getChildren().get(1);
                
                if (!text.getText().isEmpty()) {
                    TranslateTransition translate = new TranslateTransition(Duration.millis(300), text);
                    translate.setByX(70); // Width (60) + spacing (10)
                    shiftAnimations.getChildren().add(translate);
                }
            }

            shiftAnimations.setOnFinished(e -> {
                // Reset all translations and update text content to match underlying array
                refreshVisuals();
                
                // Fade in new element
                VBox slot = (VBox) arrayListContainer.getChildren().get(index);
                StackPane stackPane = (StackPane) slot.getChildren().get(0);
                Text text = (Text) stackPane.getChildren().get(1);
                text.setOpacity(0);
                
                FadeTransition fade = new FadeTransition(Duration.millis(300), text);
                fade.setFromValue(0.0);
                fade.setToValue(1.0);
                fade.play();
            });

            if (shiftAnimations.getChildren().isEmpty()) {
                refreshVisuals();
            } else {
                shiftAnimations.play();
            }

            valueField.clear();
            indexField.clear();
            valueLabel.setText("Value " + value + " inserted at index " + index);

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

            if (index < 0 || index >= arrayList.size()) {
                valueLabel.setText("Index out of bounds! Valid range: 0 to " + (arrayList.size() - 1));
                return;
            }

            int removedValue = arrayList.remove(index);

            // Animate fade out of the removed element
            VBox removedSlot = (VBox) arrayListContainer.getChildren().get(index);
            StackPane removedStackPane = (StackPane) removedSlot.getChildren().get(0);
            Text removedText = (Text) removedStackPane.getChildren().get(1);
            
            FadeTransition fadeOut = new FadeTransition(Duration.millis(300), removedText);
            fadeOut.setFromValue(1.0);
            fadeOut.setToValue(0.0);

            fadeOut.setOnFinished(e -> {
                // Animate shift left for remaining elements
                ParallelTransition shiftAnimations = new ParallelTransition();
                for (int i = index; i < arrayList.size(); i++) {
                    VBox slot = (VBox) arrayListContainer.getChildren().get(i + 1); // Get text from next slot
                    StackPane stackPane = (StackPane) slot.getChildren().get(0);
                    Text text = (Text) stackPane.getChildren().get(1);
                    
                    if (!text.getText().isEmpty()) {
                        TranslateTransition translate = new TranslateTransition(Duration.millis(300), text);
                        translate.setByX(-70); // Width (60) + spacing (10)
                        shiftAnimations.getChildren().add(translate);
                    }
                }

                shiftAnimations.setOnFinished(e2 -> {
                    refreshVisuals();
                });

                if (shiftAnimations.getChildren().isEmpty()) {
                    refreshVisuals();
                } else {
                    shiftAnimations.play();
                }
            });
            
            fadeOut.play();
            
            indexField.clear();
            valueLabel.setText("Removed value " + removedValue + " from index " + index);

        } catch (NumberFormatException e) {
            valueLabel.setText("Please enter a valid integer for index.");
        }
    }

    @FXML
    void get(ActionEvent event) {
        resetHighlight();
        String idxInput = indexField.getText().trim();

        if (idxInput.isEmpty()) {
            valueLabel.setText("Please enter an index to get.");
            return;
        }

        try {
            int index = Integer.parseInt(idxInput);

            if (index < 0 || index >= arrayList.size()) {
                valueLabel.setText("Index out of bounds!");
                return;
            }

            int val = arrayList.get(index);
            
            VBox slot = (VBox) arrayListContainer.getChildren().get(index);
            StackPane stackPane = (StackPane) slot.getChildren().get(0);
            Rectangle rect = (Rectangle) stackPane.getChildren().get(0);
            
            rect.setStyle("-fx-fill: yellow; -fx-stroke: black;");
            highlightedRectangle = rect;

            valueLabel.setText("Value at index " + index + " is " + val);
            
        } catch (NumberFormatException e) {
            valueLabel.setText("Please enter a valid integer for index.");
        }
    }

    private void resize() {
        int oldCapacity = capacity;
        capacity *= 2;
        
        for (int i = oldCapacity; i < capacity; i++) {
            VBox newSlot = createEmptySlot(i);
            newSlot.setOpacity(0);
            arrayListContainer.getChildren().add(newSlot);
            
            FadeTransition fade = new FadeTransition(Duration.millis(500), newSlot);
            fade.setFromValue(0.0);
            fade.setToValue(1.0);
            fade.play();
        }
    }

    private void resizeAndAdd(int value) {
        int oldCapacity = capacity;
        capacity *= 2;
        
        ParallelTransition fades = new ParallelTransition();
        for (int i = oldCapacity; i < capacity; i++) {
            VBox newSlot = createEmptySlot(i);
            newSlot.setOpacity(0);
            arrayListContainer.getChildren().add(newSlot);
            
            FadeTransition fade = new FadeTransition(Duration.millis(500), newSlot);
            fade.setFromValue(0.0);
            fade.setToValue(1.0);
            fades.getChildren().add(fade);
        }
        
        fades.setOnFinished(e -> {
            int addIndex = arrayList.size();
            arrayList.add(value);
            VBox slot = (VBox) arrayListContainer.getChildren().get(addIndex);
            updateSlotWithValue(slot, value);
            
            valueField.clear();
            valueLabel.setText("Resized to " + capacity + ". Appended " + value + " at index " + addIndex);
        });
        
        fades.play();
    }

    private void refreshVisuals() {
        for (int i = 0; i < capacity; i++) {
            VBox slot = (VBox) arrayListContainer.getChildren().get(i);
            StackPane stackPane = (StackPane) slot.getChildren().get(0);
            Text text = (Text) stackPane.getChildren().get(1);
            
            text.setTranslateX(0); // Reset any translation
            text.setOpacity(1.0); // Reset opacity
            
            if (i < arrayList.size()) {
                text.setText(String.valueOf(arrayList.get(i)));
            } else {
                text.setText("");
            }
        }
    }

    private VBox createEmptySlot(int index) {
        Rectangle rect = new Rectangle(60, 60);
        rect.setArcWidth(10);
        rect.setArcHeight(10);
        rect.setStyle("-fx-fill: lightblue; -fx-stroke: black;");

        Text text = new Text("");
        text.setStyle("-fx-font-size: 16;");

        StackPane stackPane = new StackPane(rect, text);
        
        Label indexLabel = new Label(String.valueOf(index));
        
        VBox slot = new VBox(5, stackPane, indexLabel);
        slot.setAlignment(Pos.CENTER);
        return slot;
    }

    private void updateSlotWithValue(VBox slot, int value) {
        StackPane stackPane = (StackPane) slot.getChildren().get(0);
        Text text = (Text) stackPane.getChildren().get(1);
        text.setText(String.valueOf(value));
    }

    private void resetHighlight() {
        if (highlightedRectangle != null) {
            highlightedRectangle.setStyle("-fx-fill: lightblue; -fx-stroke: black;");
            highlightedRectangle = null;
        }
    }
}
