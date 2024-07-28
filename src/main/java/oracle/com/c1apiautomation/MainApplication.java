package oracle.com.c1apiautomation;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class MainApplication extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(MainApplication.class.getResource("main-view.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 1400, 600);
        stage.setTitle("API Test Automation!");
        stage.titleProperty();
        scene.getStylesheets().add("/app.css");
//        scene.getStylesheets().add("path/stylesheet.css");

        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }


}