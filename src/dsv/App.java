
package dsv;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

public class App extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception{
        Parent root = FXMLLoader.load(getClass().getResource("main/main-view.fxml"));
        primaryStage.setTitle("Data Structures Visualizer");
        
        var iconResource = getClass().getResource("/dsv/resources/icon.png");
        if (iconResource != null) {
            primaryStage.getIcons().add(new Image(iconResource.toExternalForm()));
        }
        
        primaryStage.setScene(new Scene(root, 1024, 768));
        primaryStage.setMinWidth(800);
        primaryStage.setMinHeight(600);

        primaryStage.show();
    }


    public static void main(String[] args) {
        launch(args);
    }
}
