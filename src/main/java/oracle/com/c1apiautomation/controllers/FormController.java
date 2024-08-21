package oracle.com.c1apiautomation.controllers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.ContextMenuEvent;
import oracle.com.c1apiautomation.MainApplication;
import oracle.com.c1apiautomation.uihelpers.CustomInputContextMenu;
import oracle.com.c1apiautomation.uihelpers.ImageFactory;
import oracle.com.c1apiautomation.model.*;
import oracle.com.c1apiautomation.model.Module;
import oracle.com.c1apiautomation.apifactory.ContentType;
import oracle.com.c1apiautomation.apifactory.HttpStatus;
import oracle.com.c1apiautomation.utils.Util;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


public class FormController {

    //for msform-view.fxml
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
    public ComboBox cmdResponseCode;
    //    public CheckBox chkUseAuth;
    public ComboBox<String> cmbAuthType;
    public TextField txtUserName;
    //    public Button btnSaveAsNew;
    public DialogPane dlgEditBaseTest;
    public DialogPane dlgAddMs;
    public TextArea taPreReqSql;
    public TextArea taInput;
    public TextArea taDbQuery;
    public TextArea taExpectedDBRes;
    public TextArea taTearDownSql;
    public TextArea taPayload;
    public TextArea taExpectedRes;

    // msform-view
    public TextField txtMs;
    public TextField txtModule;


//    private BaseTestCase testCase;
//    private TreeTableView<Object> treeTableView;
//    private Stage stage;
//    private TreeItem<Object> selectedItem;

    //    private EditMode editMode;
    private Scene scene;

    public FormController(Scene scene) {
        this.scene = scene;
    }

    public FormController() {
    }

    public void initialize() {
        if (cmbRequestType != null) {
            var requestTypes = FXCollections.observableArrayList("POST", "GET", "PUT", "DELETE", "HEAD", "OPTIONS");
            cmbRequestType.setItems(requestTypes);
//            var responseCodes = FXCollections.observableArrayList("200", "201", "202", "204", "400", "403", "404", "500", "NA");
//            var responseCodes = FXCollections.observableHashMap();
//            responseCodes.putAll(HttpStatus.getCodes());
            cmdResponseCode.getItems().add("");
            for (Map.Entry<Integer, String> entry : HttpStatus.getCodes().entrySet()) {
                cmdResponseCode.getItems().add(entry);
            }

//            cmdResponseCode.setItems(HttpStatus.getCodes());
            var authTypes = FXCollections.observableArrayList("", "basic", "jwt");
            cmbAuthType.setItems(authTypes);

            MenuItem mi1 = new MenuItem("Format Json", ImageFactory.getImageView("format.png"));
            mi1.setOnAction(e -> Util.formatJson(taPayload));
            CustomInputContextMenu.addMenuItems(taPayload,mi1);

            MenuItem mi2 = new MenuItem("Format Json", ImageFactory.getImageView("format.png"));
            mi2.setOnAction(e -> Util.formatJson(taExpectedRes));
            CustomInputContextMenu.addMenuItems(taExpectedRes,mi2);
        }
    }



    private void setBaseTestCase(BaseTestCase model, EditMode editMode) {

        var x = Arrays.stream(ContentType.getAllValues()).map(ContentType::getValue).collect(Collectors.joining(","));
        System.out.println(x);
//        this.testCase = model;
//        this.treeTableView = ttvContainer;
        if (editMode == EditMode.EDIT_PREREQ || editMode == EditMode.EDIT_TESTCASE) {
//            this.selectedItem = selectedItem;
            txtId.setText(model.getId());
            txtTitle.setText(model.getTitle());
            txtDescription.setText(model.getDescription());
            cmbRequestType.setValue(model.getRequestType());
            txtServiceUrl.setText(model.getServiceUrl());
            txtHeader.setText(model.getHeader());

            var code = model.getExpectedResponseCode().isEmpty() ? "" : HttpStatus.getByCode(Integer.parseInt(model.getExpectedResponseCode()));
            cmdResponseCode.setValue(code);
            cmbAuthType.setValue(model.getUseAuthentication());
            txtUserName.setText(model.getUserName());
            txtPassword.setText(model.getPassword());
            chkSelected.setSelected(model.isSelected());
            chkSkipValidation.setSelected(model.isSkipValidation());
            taPreReqSql.setText(model.getPreReqSql());
            taPayload.setText(model.getPayload());
            Util.formatJson(taPayload);
            taExpectedRes.setText(model.getExpectedResponse());
            Util.formatJson(taExpectedRes);
            taInput.setText(model.getInput());
            taDbQuery.setText(model.getDbQuery());
            taExpectedDBRes.setText(model.getExpectedDBResult());
            taTearDownSql.setText(model.getTearDownSql());
        }
    }

    private void getBaseTestCase(BaseTestCase model, Type t) {
        if (model == null) {
            if (t instanceof TestCase) {
                model = new TestCase();
            } else {
                model = new PreReq();
            }
        }

        model.setId(txtId.getText());
        model.setTitle(txtTitle.getText());
        model.setDescription(txtDescription.getText());
        model.setRequestType(cmbRequestType.getValue());
        model.setServiceUrl(txtServiceUrl.getText());
        var code = cmdResponseCode.getValue() != "" ? ((Map.Entry<Integer, String>) cmdResponseCode.getValue()).getKey().toString() : "";
        model.setExpectedResponseCode(code);
        model.setUseAuthentication(cmbAuthType.getValue());
        model.setUserName(txtUserName.getText());
        model.setPassword(txtPassword.getText());
        model.setSelected(chkSelected.isSelected());
        model.setSkipValidation(chkSkipValidation.isSelected());
        model.setPreReqSql(taPreReqSql.getText());
        model.setPayload(taPayload.getText());
        model.setExpectedResponse(taExpectedRes.getText());
        model.setInput(taInput.getText());
        model.setDbQuery(taDbQuery.getText());
        model.setExpectedDBResult(taExpectedDBRes.getText());
        model.setTearDownSql(taExpectedRes.getText());

    }

    private FXMLLoader OpenDialog(Dialog dlg, String fxmlFile) throws IOException {

        FXMLLoader fxmlLoader = new FXMLLoader(MainApplication.class.getResource(fxmlFile));
        Parent parent = fxmlLoader.load();
        dlg.setDialogPane((DialogPane) parent);

        dlg.getDialogPane().getStylesheets().addAll(scene.getStylesheets());

        return fxmlLoader;
    }

    public void addMicroservice(TreeItem<Object> selectedItem) throws IOException {
//        var root = (Root) selectedItem.getParent().getValue();
        var dlg = new Dialog();
        dlg.setTitle("Add Microservice");
        var fxmlLoader = OpenDialog(dlg, "msform-view.fxml");
        var controller = (FormController) fxmlLoader.getController();
        dlg.showAndWait();
        if (dlg.getResult() == ButtonType.OK) {
            // Create and populate Microservice object
            var ms = new Microservice();
            ms.setName(controller.txtMs.getText());

            // Create and populate Module object
            Module module = createNewModule(controller.txtModule.getText());

            // Create TreeItems for Microservice, Module, TestCases, and PreReqs
            TreeItem<Object> msTreeItem = new TreeItem<>(ms);
            TreeItem<Object> moduleTreeItem = creteNewModelTreeItem(module);

            // Add the Module TreeItem to the Microservice TreeItem
            msTreeItem.getChildren().add(moduleTreeItem);

            // Insert the new Microservice TreeItem in the TreeView
            var index = selectedItem.getParent().getChildren().indexOf(selectedItem);
            selectedItem.getParent().getChildren().add(index + 1, msTreeItem);
        }
    }

    public void addModule(TreeItem<Object> selectedItem) throws IOException {
        var microservice = (Microservice) (selectedItem.getValue() instanceof Microservice ? selectedItem.getValue() : selectedItem.getParent().getValue());
        var dlg = new Dialog();
        dlg.setTitle("Add Module");
        var fxmlLoader = OpenDialog(dlg, "msform-view.fxml");
        var controller = (FormController) fxmlLoader.getController();

        controller.txtMs.setText(microservice.getName());
        controller.txtMs.setDisable(true);
        dlg.showAndWait();
        if (dlg.getResult() == ButtonType.OK) {

            // Create and populate Module object
            Module module = createNewModule(controller.txtModule.getText());

            // Create TreeItems for Microservice, Module, TestCases, and PreReqs
            TreeItem<Object> moduleTreeItem = creteNewModelTreeItem(module);

            // Insert the new Microservice TreeItem in the TreeView
            var index = selectedItem.getParent().getChildren().indexOf(selectedItem);
            selectedItem.getParent().getChildren().add(index + 1, moduleTreeItem);
        }
    }

    public void EditBaseTest(BaseTestCase baseTestCase, TreeTableView<Object> treeTableView, TreeItem<Object> selectedItem, EditMode editMode) throws IOException {
//        var selectedItem = (TreeItem<Object>) this.treeTableView.getSelectionModel().getSelectedItem();
//        var microservice = (Microservice) selectedItem.getParent().getValue();

        var dlg = new Dialog();
        Type type = BaseTestCase.class;
        if (editMode == EditMode.ADD_TESTCASE) {
            dlg.setTitle("New Test Case");
            type = TestCase.class;
        } else if (
                editMode == EditMode.ADD_PREREQ) {
            dlg.setTitle("New Prerequisite");
            type = PreReq.class;
        } else {
            dlg.setTitle("Edit " + baseTestCase.getId());
        }
        var fxmlLoader = OpenDialog(dlg, "form-view.fxml");
        var controller = (FormController) fxmlLoader.getController();

        controller.setBaseTestCase(baseTestCase, editMode);
        dlg.showAndWait();
        if (dlg.getResult() == ButtonType.OK) {
            controller.getBaseTestCase(baseTestCase, type);
            treeTableView.refresh();
        }

    }

    private TreeItem<Object> creteNewModelTreeItem(Module module) {
        TreeItem<Object> moduleTreeItem = new TreeItem<>(module);

        // Add TestCases and PreReqs to the Module TreeItem
        for (PreReq pr : module.getPreReqs()) {
            TreeItem<Object> preReqTreeItem = new TreeItem<>(pr);
            moduleTreeItem.getChildren().add(preReqTreeItem);
        }
        for (TestCase tc : module.getTestCases()) {
            TreeItem<Object> testCaseTreeItem = new TreeItem<>(tc);
            moduleTreeItem.getChildren().add(testCaseTreeItem);
        }
        return moduleTreeItem;
    }

    private Module createNewModule(String name) {
        // Create and populate Module object
        Module module = new Module();
        module.setName(name);

        // Create the list of Modules
        List<Module> modules = new ArrayList<>();

        List<PreReq> preReqs = new ArrayList<>();
        PreReq preReq = new PreReq();
        preReq.setId("PreReq1");  // Example name, you can customize
        preReqs.add(preReq);

        // Create a list of TestCases and PreReqs for the Module
        List<TestCase> testCases = new ArrayList<>();
        TestCase testCase = new TestCase();
        testCase.setId("TestCase1");  // Example name, you can customize
        testCases.add(testCase);

        // Assign TestCases and PreReqs to the Module
        module.setPreReqs(preReqs);
        module.setTestCases(testCases);

        return module;
    }


//    public void onSaveData(ActionEvent actionEvent) {
//        testCase.setTitle(txtTitle.getText());
//        treeTableView.refresh();
//        System.out.println(testCase.getTitle());
//        System.out.println(testCase instanceof PreReq);
//        stage.close();
//    }

//    public void addItemToModule(TreeItem<Object> baseTestCaseItem, BaseTestCase baseTestCase) {
//
//        if (selectedItem.getParent() != null) {
//            var currentModule = (Module) selectedItem.getParent().getValue();
//            if (baseTestCase instanceof PreReq) {
//                currentModule.getPreReqs().add((PreReq) baseTestCase);
//            }
//            if (baseTestCase instanceof TestCase) {
//                currentModule.getTestCases().add((TestCase) baseTestCase);
//            }
//            TreeItem<Object> newItem = new TreeItem<>(baseTestCase);
//            selectedItem.getParent().getChildren().add(newItem);
//            System.out.println(currentModule);
//
//            // Optionally expand the module item to show the new PreReq
//            selectedItem.getParent().setExpanded(true);
//        }
//    }
}
