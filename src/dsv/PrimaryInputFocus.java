package dsv;

import javafx.application.Platform;
import javafx.scene.control.TextField;

public final class PrimaryInputFocus {

    private PrimaryInputFocus() {}

    public static void focusAndSelect(TextField field) {
        if (field == null || field.isDisabled()) {
            return;
        }
        Platform.runLater(() -> {
            if (!field.isDisabled()) {
                field.requestFocus();
                field.selectAll();
            }
        });
    }
}
