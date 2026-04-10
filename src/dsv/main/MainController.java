package dsv.main;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;

import java.io.IOException;

public class MainController
 {

    private boolean isOpen = true;

    @FXML
    private VBox menuSidebar;

     @FXML
    private BorderPane rootPane;

    @FXML
    void toggleMenu() {
        if (isOpen) {
            menuSidebar.setPrefWidth(0);
            menuSidebar.setVisible(false);
        } else {
            menuSidebar.setPrefWidth(150);
            menuSidebar.setVisible(true);
        }
        isOpen = !isOpen;
    }

    @FXML
    void showStack(ActionEvent event) throws IOException {
        Parent stackView = FXMLLoader.load(
                getClass().getResource("../stack/stack-view.fxml")
        );

        rootPane.setCenter(stackView);
    }

    @FXML
    void showQueue(ActionEvent event) throws IOException {
        Parent queueView = FXMLLoader.load(
                getClass().getResource("../queue/queue-view.fxml")
        );

        rootPane.setCenter(queueView);
    }

    @FXML
    void showTree(ActionEvent event) throws IOException {
        Parent treeView = FXMLLoader.load(
                getClass().getResource("../tree/tree-view.fxml")
        );

        rootPane.setCenter(treeView);
    }
}
