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

    @FXML
    private VBox menuSidebar;

     @FXML
    private BorderPane rootPane;

    @FXML
    public void showStack(ActionEvent event) {
        try {
            Parent stackView = FXMLLoader.load(
                    getClass().getResource("../stack/stack-view.fxml")
            );

            rootPane.setCenter(stackView);
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

            rootPane.setCenter(queueView);
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

            rootPane.setCenter(treeView);
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

            rootPane.setCenter(setView);
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

            rootPane.setCenter(arrayListView);
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

            rootPane.setCenter(linkedListView);
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

            rootPane.setCenter(mapView);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
