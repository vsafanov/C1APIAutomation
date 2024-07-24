package oracle.com.c1apiautomation;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeTableView;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;
import java.util.Objects;

public class HelloApplication extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("hello-view.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 1400, 600);
        stage.setTitle("C1 API Test Automation!");
        scene.getStylesheets().add("/app.css");
//        scene.getStylesheets().add("path/stylesheet.css");

        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }


}