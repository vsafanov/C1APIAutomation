package oracle.com.c1apiautomation;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import oracle.com.c1apiautomation.helpers.ImageFactory;

import java.io.IOException;

public class MainApplication extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(MainApplication.class.getResource("main-view.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 1400, 800);
        stage.setTitle("API Test Studio!");   //API Test Admin, APITestManager
        stage.getIcons().add(ImageFactory.getImageView("title.png").getImage());
        //Engine to run tests, possible names: API Test Runner, API Test Engine, API Test Processor
        stage.titleProperty();
        scene.getStylesheets().add("/app.css");
        scene.getStylesheets().add("/DarkTheme.css");
//        scene.getStylesheets().add("path/stylesheet.css");

        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }


}