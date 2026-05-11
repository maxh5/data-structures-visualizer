package dsv.main;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

import java.awt.Desktop;
import java.io.IOException;
import java.net.URI;

public class MainController
 {

    @FXML
    private VBox menuSidebar;

     @FXML
    private BorderPane rootPane;

    @FXML
    private StackPane structureHost;

    private void showInCenter(Parent view) {
        structureHost.getChildren().setAll(view);
    }

    @FXML
    public void showStack(ActionEvent event) {
        try {
            Parent stackView = FXMLLoader.load(
                    getClass().getResource("../stack/stack-view.fxml")
            );

            showInCenter(stackView);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    public void showQueue(ActionEvent event) {
        try {
            Parent queueView = FXMLLoader.load(
                    getClass().getResource("../queue/queue-view.fxml")
            );

            showInCenter(queueView);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    public void showTree(ActionEvent event) {
        try {
            Parent treeView = FXMLLoader.load(
                    getClass().getResource("../tree/tree-view.fxml")
            );

            showInCenter(treeView);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    public void showSet(ActionEvent event) {
        try {
            Parent setView = FXMLLoader.load(
                    getClass().getResource("../set/set-view.fxml")
            );

            showInCenter(setView);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    public void showArrayList(ActionEvent event) {
        try {
            Parent arrayListView = FXMLLoader.load(
                    getClass().getResource("../arrayList/arrayList-view.fxml")
            );

            showInCenter(arrayListView);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    public void showLinkedList(ActionEvent event) {
        try {
            Parent linkedListView = FXMLLoader.load(
                    getClass().getResource("../linkedList/linkedList-view.fxml")
            );

            showInCenter(linkedListView);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    public void showMap(ActionEvent event) {
        try {
            Parent mapView = FXMLLoader.load(
                    getClass().getResource("../map/map-view.fxml")
            );

            showInCenter(mapView);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void openLinkedIn(ActionEvent event) {
        try {
            if (!Desktop.isDesktopSupported()) return;
            Desktop.getDesktop().browse(new URI("https://linkedin.com/in/maxmholden/"));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
