package oracle.com.c1apiautomation;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TextArea;
import javafx.stage.Stage;
import oracle.com.c1apiautomation.model.Vars;
import oracle.com.c1apiautomation.uihelpers.ImageFactory;
import oracle.com.c1apiautomation.utils.UserPreferences;
import oracle.com.c1apiautomation.utils.ExceptionHandler;

import java.io.IOException;
import java.util.Optional;

public class MainApplication extends Application {

    private static Vars vars;

    public static Vars getVars() {
        return vars;
    }
    @Override
    public void start(Stage stage) throws IOException {
        final String ANSI_RED = "\u001B[31m";
        vars = new Vars();  // Initialize the Vars instance

        // Set the global exception handler
        Thread.setDefaultUncaughtExceptionHandler(new Thread.UncaughtExceptionHandler() {
            @Override
            public void uncaughtException(Thread t, Throwable e) {
                // Handle the exception here
                System.out.println("Uncaught exception: " + ANSI_RED  + e.getMessage());
                showAlert(Alert.AlertType.ERROR, "Exception", ExceptionHandler.getFilteredStackTrace(e,"oracle.com.c1apiautomation"));
            }
        });

        FXMLLoader fxmlLoader = new FXMLLoader(MainApplication.class.getResource("main-view.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 1400, 800);
        stage.setTitle("API Test Studio!");   //API Test Admin, APITestManager
        stage.getIcons().add(ImageFactory.getImageView("title.png").getImage());
        //Engine to run tests, possible names: API Test Runner, API Test Engine, API Test Processor
        stage.titleProperty();
        scene.getStylesheets().add("/app.css");

        UserPreferences prefs = UserPreferences.getInstance();
        var userTheme =  prefs.getString(UserPreferences.USER_THEME_KEY, UserPreferences.DARK_THEME);
        if(!userTheme.isEmpty()) {
            scene.getStylesheets().add(userTheme);
        }
//        scene.getStylesheets().add("path/stylesheet.css");

        stage.setScene(scene);
        stage.show();
    }

    private Optional<ButtonType> showAlert(Alert.AlertType type, String title, String content) {
        Alert alert = new Alert(type);
        alert.setTitle(title);

        TextArea area = new TextArea(content);
        area.setWrapText(true);
        area.setEditable(false);

        alert.getDialogPane().setContent(area);
        alert.setResizable(true);

        return alert.showAndWait();
    }


    public static void main(String[] args) {
        launch();
    }


}