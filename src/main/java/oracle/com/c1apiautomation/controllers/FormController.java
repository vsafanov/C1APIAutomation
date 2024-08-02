package oracle.com.c1apiautomation.controllers;

import javafx.beans.Observable;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;
import oracle.com.c1apiautomation.model.BaseTestCase;
import oracle.com.c1apiautomation.model.PreReq;
import oracle.com.c1apiautomation.model.Module;
import oracle.com.c1apiautomation.model.TestCase;

import java.util.List;

public class FormController {

    @FXML
    public TextField txtTitle;
    @FXML
    public Button btnSave;
    @FXML
    public TextField txtId;
    public CheckBox chkSelected;
    public TextField txtDescription;
    public TextField txtServiceUrl;
    public TextField txtHeader;
    public TextField txtPassword;
    public ComboBox<String> cmbRequestType;
    public CheckBox chkSkipValidation;
    public ComboBox<String> cmdResponseCode;
    public CheckBox chkUseAuth;
    public TextField txtUserName;
    public Button btnSaveAsNew;

    private BaseTestCase testCase;
    private TreeTableView<Object> treeTableView;
    private  Stage stage;
    private TreeItem<Object> selectedItem;

    public void initialize(){
        var requestTypes = FXCollections.observableArrayList("POST","GET","PUT","DELETE","HEAD","OPTIONS");
        cmbRequestType.setItems(requestTypes);
//        cmbRequestType.setStyle("-fx-font-weight: bold");
        var responseCodes = FXCollections.observableArrayList("200","201","202","204","400","403","404","500","NA");
        cmdResponseCode.setItems(responseCodes);

    }

    public void setBaseTestCase(BaseTestCase baseTestCase, TreeTableView<Object> ttvContainer, TreeItem<Object> selectedItem, Stage stage) {
        this.testCase  = baseTestCase ;
        this.treeTableView = ttvContainer;
        this.stage = stage;
        this.selectedItem = selectedItem;


    }

    public void loadFormData() {}

    public void onSaveData(ActionEvent actionEvent) {
        testCase.setTitle(txtTitle.getText());
        treeTableView.refresh();
        System.out.println(testCase.getTitle());
        System.out.println(testCase instanceof PreReq);
        stage.close();
    }

    public void saveItemToModule(BaseTestCase baseTestCase)
    {

    }

    public void addItemToModule(TreeItem<Object> baseTestCaseItem, BaseTestCase baseTestCase) {

        if (selectedItem.getParent() != null) {
            var currentModule = (Module) selectedItem.getParent().getValue();
            if (baseTestCase instanceof PreReq) {
                currentModule.getPreReqs().add((PreReq) baseTestCase);
            }
            if (baseTestCase instanceof TestCase) {
                currentModule.getTestCases().add((TestCase) baseTestCase);
            }
            TreeItem<Object> newItem = new TreeItem<>(baseTestCase);
            selectedItem.getParent().getChildren().add(newItem);
            System.out.println(currentModule);

            // Optionally expand the module item to show the new PreReq
            selectedItem.getParent().setExpanded(true);
        }
    }
}
