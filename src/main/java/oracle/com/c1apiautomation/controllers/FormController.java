package oracle.com.c1apiautomation.controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeTableView;
import javafx.stage.Stage;
import oracle.com.c1apiautomation.model.BaseTestCase;
import oracle.com.c1apiautomation.model.PreReq;
import oracle.com.c1apiautomation.model.Module;
import oracle.com.c1apiautomation.model.TestCase;

public class FormController {

    @FXML
    public TextField txtTitle;
    @FXML
    public Button btnSave;

    private BaseTestCase testCase;
    private TreeTableView<Object> treeTableView;
    private  Stage stage;
    private TreeItem<Object> selectedItem;

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
