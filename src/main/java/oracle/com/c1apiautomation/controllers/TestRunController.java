package oracle.com.c1apiautomation.controllers;

import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import oracle.com.c1apiautomation.model.Environment;
import oracle.com.c1apiautomation.model.TestCase;
import oracle.com.c1apiautomation.model.Vars;
import oracle.com.c1apiautomation.uiexecutionengine.ApiExecutionEngine;
import oracle.com.c1apiautomation.utils.Util;

import java.io.IOException;

public class TestRunController {

    private final Scene scene;
    private final Environment selectedEnvironment;

    public TestRunController(Scene scene, Environment selectedEnvironment) {
        this.scene = scene;
        this.selectedEnvironment = selectedEnvironment;
    }

    public void OpenRequestDialog(TestCase testCase) throws IOException {
        var dlg = new Dialog();
        dlg.setResizable(true);
        dlg.setTitle("Test Result for: " + testCase.getId());

        //RUN TEST AND GET TestResponse to show in dialog
        ApiExecutionEngine exec= new ApiExecutionEngine(selectedEnvironment);
        var testResponse =  exec.RunTest(testCase);

        Alert alert = new Alert(testResponse.getTestPassed()? Alert.AlertType.INFORMATION: Alert.AlertType.ERROR);
        alert.setHeaderText("");
        alert.setContentText(testResponse.getTestPassed()?"Test Pass Successfully!": "Test Fail!\n" + (testResponse.getError() == null? "" :testResponse.getError()));
        alert.showAndWait();
        if (alert.getResult() == ButtonType.OK) {
            return;
        }
        var fxmlLoader = Util.OpenDialog(dlg, "apirequest-view.fxml",scene);
        var controller = (ApiRequestController) fxmlLoader.getController();
//        controller.setRequestModel(testCase);
//        controller.setSelectedEnvironment(this.selectedEnvironment);
        dlg.showAndWait();
        if (dlg.getResult() == ButtonType.OK) {

        }
    }
}
