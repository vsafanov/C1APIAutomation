package oracle.com.c1apiautomation.controllers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import javafx.beans.property.*;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;

import javafx.scene.control.TreeTableColumn.CellEditEvent;
import javafx.scene.control.cell.TextFieldTreeTableCell;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import oracle.com.c1apiautomation.MainApplication;
import oracle.com.c1apiautomation.utils.Util;
import oracle.com.c1apiautomation.uihelpers.*;
import oracle.com.c1apiautomation.model.*;
import oracle.com.c1apiautomation.model.Module;
import oracle.com.c1apiautomation.utils.UserPreferences;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class MainController {

    public TreeTableView ttvContainer;
    public MenuBar menuBar;
    public MenuItem miLoadJson;
    public VBox root;
    public Label lblEnv;
    Root rootData;
    Environment selectedEnvironment;


    public void initialize() throws IOException {
        handleLoadFromJson();

    }

    private String getSelectedEnvironment() {
        // Assuming 'menuBar' is your MenuBar instance
        Menu environmentsMenu = menuBar.getMenus().stream()
                .filter(menu -> "Environments".equals(menu.getText()))
                .findFirst()
                .orElse(null);

        if (environmentsMenu != null) {
            // Find the selected RadioMenuItem
            RadioMenuItem selectedItem = environmentsMenu.getItems().stream()
                    .filter(item -> item instanceof RadioMenuItem)
                    .map(item -> (RadioMenuItem) item)
                    .filter(RadioMenuItem::isSelected)
                    .findFirst()
                    .orElse(null);

            if (selectedItem != null) {
                return selectedItem.getText();
//                System.out.println("Selected item text: " + selectedText);
            } else {

                System.out.println("No item is selected.");
                return "";
            }
        } else {

            System.out.println("Menu 'Environments' not found.");
            return "";
        }

    }

    private void createEnvironmentMenus() {
        var envMenu = menuBar.getMenus().getLast().getItems();
        var selectedText = getSelectedEnvironment();
        envMenu.clear();
        ToggleGroup toggleGroup = new ToggleGroup();

        lblEnv.getStyleClass().add("label-env-inactive");
        for (Environment env : rootData.getEnvironments()) {
            RadioMenuItem mi = new RadioMenuItem(env.getName());
            if(mi.getText().equals(selectedText))
            {
                mi.setSelected(true);
            }
            mi.setToggleGroup(toggleGroup);
            mi.setOnAction(e -> {
                selectedEnvironment = env;
                lblEnv.getStyleClass().removeAll("label-env-inactive");
                lblEnv.getStyleClass().add("label-env-active");
                lblEnv.setText(env.getName());

                //Has to recreate ContextMenu after environment changed
                var contextMenu = new ContextMenuFactory(ttvContainer, selectedEnvironment);
                contextMenu.CreateContextMenu();
            });
            envMenu.add(mi);
        }
        var newSelectedText = getSelectedEnvironment();
        if(newSelectedText.isEmpty())
        {
            lblEnv.getStyleClass().add("label-env-inactive");
            lblEnv.getStyleClass().removeAll("label-env-active");
            lblEnv.setText("No Environment.");
            selectedEnvironment = null;
        }
        SeparatorMenuItem smi = new SeparatorMenuItem();
        envMenu.add(smi);

        MenuItem config = new MenuItem("Configure",ImageFactory.getImageView("configure1.png"));
        config.setOnAction(this::ConfigureEnvironment);
        envMenu.add(config);
    }

    private void ConfigureEnvironment(ActionEvent event) {
        var mi = ((MenuItem) event.getTarget()).getParentPopup();
        Scene scene = mi.getScene();

        var envController = new EnvironmentController(scene, rootData.getEnvironments());
        try {
            String environmentName = selectedEnvironment != null ? selectedEnvironment.getName() : null;
            envController.setMainController(this);
            envController.OpenEnvironmentDialog(environmentName);

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void UpdateEnvMenu() {
        System.out.println(rootData.getEnvironments().size());
//        menuBar.getMenus().getLast().getItems().stream().filter(s-> s instanceof RadioMenuItem).map(s-> ((RadioMenuItem) s)).forEach(radioMenuItem -> radioMenuItem.setSelected(false));
        createEnvironmentMenus();
    }

    private void clearTreeTableView() {
        if (ttvContainer.getRoot() != null) {
            ttvContainer.getRoot().getChildren().clear();
            ttvContainer.getColumns().clear();
            ttvContainer.setRoot(null);
        }
    }

    public void LoadTreeTableView(String filePath) throws IOException {

        //clear all treetableview data before load new one
        clearTreeTableView();

        // Load data
        rootData = Util.readJson(filePath);

        // Add a column for checkboxes
        TreeTableColumn<Object, Boolean> checkBoxColumn = new TreeTableColumn<>("Run Test");
        checkBoxColumn.setPrefWidth(85);

        checkBoxColumn.setCellValueFactory(param -> {
            Object value = param.getValue().getValue();
            return value instanceof SelectableBase ? ((SelectableBase) value).getSelected() : new SimpleBooleanProperty(false);
        });
        checkBoxColumn.setCellFactory(CheckBoxTreeTableCell.forTreeTableColumn());

        var microserviceColumn = Util.createStringColumn("Microservice", Microservice.class, Microservice::getName, 100);
        microserviceColumn.setCellFactory(TextFieldTreeTableCell.forTreeTableColumn());
        microserviceColumn.setOnEditCommit(this::CommitEdit);

        var moduleColumn = Util.createStringColumn("Module", Module.class, Module::getName, 120); // Adjust accordingly
        moduleColumn.setCellFactory(TextFieldTreeTableCell.forTreeTableColumn());
        // Handle updating the Module object when the cell is edited
        moduleColumn.setOnEditCommit(this::CommitEdit);

        // Define the image column
        TreeTableColumn<Object, String> imageColumn = new TreeTableColumn<>("...");
        imageColumn.setCellValueFactory(param -> new SimpleStringProperty("")); // Dummy value
        imageColumn.setCellFactory(column -> new ImageTreeTableCell());

        var idColumn = Util.createStringColumn("Test Case ID", BaseTestCase.class, BaseTestCase::getId, 150);
        var titleColumn = Util.createStringColumn("Title", BaseTestCase.class, BaseTestCase::getTitle, 100);
        var descriptionColumn = Util.createStringColumn("Description", BaseTestCase.class, BaseTestCase::getDescription, 100);

        var requestTypeColumn = Util.createStringColumn("Method", BaseTestCase.class, BaseTestCase::getRequestType, 60);
        requestTypeColumn.setCellFactory(column -> new RequestTypeTreeTableCell());

        var requestServiceUrlColumn = Util.createStringColumn("Service Url", BaseTestCase.class, BaseTestCase::getServiceUrl, 150);
        var authColumn = Util.createStringColumn("Auth Type", BaseTestCase.class, BaseTestCase::getUseAuthentication, 100);
        var requestRequestParamsColumn = Util.createStringColumn("Payload", BaseTestCase.class, BaseTestCase::getPayload, 150);
        var expectedResponseCodeColumn = Util.createStringColumn("Response Code", BaseTestCase.class, BaseTestCase::getExpectedResponseCode, 100);
        var expectedResponseColumn = Util.createStringColumn("Expected Response", BaseTestCase.class, BaseTestCase::getExpectedResponse, 150);

        var inputColumn = Util.createStringColumn("Input Vars", BaseTestCase.class, BaseTestCase::getInput, 100);
//        var userNameColumn = Utils.createStringColumn("User Name", BaseTestCase.class, BaseTestCase::getUserName, 100);
//        var passwordColumn = Utils.createStringColumn("Password", BaseTestCase.class, BaseTestCase::getPassword, 100);

        ttvContainer.getColumns().addAll(checkBoxColumn, microserviceColumn, moduleColumn, imageColumn, idColumn,
                titleColumn, descriptionColumn, requestTypeColumn, requestServiceUrlColumn,authColumn,
                requestRequestParamsColumn, expectedResponseCodeColumn, expectedResponseColumn,inputColumn);

        //disable s sorting
        for (var column : ttvContainer.getColumns()) {
            ((TreeTableColumn<?, ?>) column).setSortable(false);
        }

        // Set the row height by creating a custom TreeTableRow factory
        ttvContainer.setRowFactory(tv -> {
            TreeTableRow<String> row = new TreeTableRow<>();
            row.setPrefHeight(30); // Set your desired row height here
            return row;
        });

        // Add double-click event handler
//        ttvContainer.setOnMouseClicked(event -> {
//            if (event.getClickCount() == 2) {
//                var selectedItem = (TreeItem<Object>) ttvContainer.getSelectionModel().getSelectedItem();
//                if (selectedItem != null && selectedItem.getValue() instanceof BaseTestCase) {
//                    BaseTestCase baseTestCase = (BaseTestCase) selectedItem.getValue();
//                    try {
//                        handleDoubleClick(baseTestCase, selectedItem);
//                    } catch (IOException e) {
//                        throw new RuntimeException(e);
//                    }
//                }
//            }
//        });

        // Populate TreeTableView with data
        TreeItem<Object> rootItem = new TreeItem<>(rootData);
        ttvContainer.setRoot(rootItem);

        // Populate tree items
        for (Microservice microservice : rootData.getMicroservices()) {
            TreeItem<Object> microserviceItem = new TreeItem<>(microservice);
            // Add listener to update child nodes when parent node is checked/unchecked
            addCheckBoxListener(microservice, microserviceItem);

            for (Module module : microservice.getModules()) {
                TreeItem<Object> moduleItem = new TreeItem<>(module);
                // Add listener to update child nodes when parent node is checked/unchecked
                addCheckBoxListener(module, moduleItem);

                for (PreReq preReq : module.getPreReqs()) {
                    TreeItem<Object> preReqItem = new TreeItem<>(preReq);
                    moduleItem.getChildren().add(preReqItem);
                }
                for (TestCase testCase : module.getTestCases()) {
                    TreeItem<Object> testCaseItem = new TreeItem<>(testCase);
                    moduleItem.getChildren().add(testCaseItem);
                }
                microserviceItem.getChildren().add(moduleItem);
            }
            rootItem.getChildren().add(microserviceItem);
        }

//        ttvContainer.setRoot(rootItem);
        ttvContainer.setEditable(true);

        //add context menu
        var contextMenu = new ContextMenuFactory(ttvContainer, selectedEnvironment);
        contextMenu.CreateContextMenu();

        ttvContainer.setTableMenuButtonVisible(true);

        ttvContainer.setShowRoot(false);
//        ttvContainer.setShowRoot(true);

    }

    private void CommitEdit(CellEditEvent<Object, String> event) {
        TreeItem<Object> treeItem = event.getRowValue();
        if (treeItem.getValue() instanceof Module module) {
            module.setName(event.getNewValue());  // Update the Module's name property
        }
        if (treeItem.getValue() instanceof Microservice microservice) {
            microservice.setName(event.getNewValue());  // Update the Module's name property
        }
    }

    @FXML
    private void handleLoadFromJson() throws IOException {
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("JSON Files", "*.json"));
        fileChooser.setTitle("Select Jason API Test Project");
        fileChooser.setInitialDirectory(new File("C:\\Users\\VSAFANOV\\Documents\\My Super Deals Private Folder\\Sandbox\\JavaFX"));
        File file = fileChooser.showOpenDialog(null);

        if (file != null) {
            LoadTreeTableView(file.getPath());
            createEnvironmentMenus();
        }
    }

    private void addCheckBoxListener(SelectableBase parent, TreeItem<Object> parentItem) {
        try {
            parent.getSelected().addListener((obs, oldVal, newVal) -> {
                for (TreeItem<Object> childItem : parentItem.getChildren()) {

                    SelectableBase childValue = (SelectableBase) childItem.getValue();
                    childValue.getSelected().set(newVal);
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /********** THIS IS GENERIC METHOD - WORKS ******************/
//    private <T> void addCheckBoxListener(T parent, TreeItem<Object> parentItem) {
//        try {
//            Method selectedPropertyMethod = parent.getClass().getMethod("getSelected");
//            BooleanProperty selectedProperty = (BooleanProperty) selectedPropertyMethod.invoke(parent);
//
//            selectedProperty.addListener((obs, oldVal, newVal) -> {
//                for (TreeItem<Object> childItem : parentItem.getChildren()) {
//                    Object childValue = childItem.getValue();
//                    try {
//                        Method childSelectedPropertyMethod = childValue.getClass().getMethod("getSelected");
//                        BooleanProperty childSelectedProperty = (BooleanProperty) childSelectedPropertyMethod.invoke(childValue);
//                        childSelectedProperty.set(newVal);
//                    } catch (Exception e) {
//                        e.printStackTrace();
//                    }
//                }
//            });
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }
//    @FXML
    private void handleDoubleClick(BaseTestCase baseTestCase, TreeItem<Object> selectedItem) throws IOException {
        // Handle the double-click event on a BaseTestCase item
        FXMLLoader fxmlLoader = new FXMLLoader(MainApplication.class.getResource("form-view.fxml"));

        Parent parent = fxmlLoader.load();

        Stage stage = new Stage();
        Scene scene = new Scene(parent);

        stage.setTitle("Edit Form");
        scene.getStylesheets().add("/app.css");
        stage.setScene(scene);

        // Get the controller and set the BaseTestCase data
        FormController controller = fxmlLoader.getController();
//        controller.setBaseTestCase(baseTestCase, ttvContainer, selectedItem, stage);
        stage.show();


//ttvContainer.refresh();
        ObjectWriter ow = new ObjectMapper().writer().withDefaultPrettyPrinter();
        String json = ow.writeValueAsString(baseTestCase);

        System.out.println("Double-clicked on: " + baseTestCase.getSelected());
        System.out.println(json);
    }

    public void handleLoadTheme(ActionEvent actionEvent) {
        var theme = ((MenuItem) actionEvent.getTarget()).getId();
        var prefs = UserPreferences.getInstance();

        switch (theme) {
            case "miDark":
                root.getScene().getStylesheets().add(UserPreferences.DARK_THEME);
                prefs.setString(UserPreferences.USER_THEME_KEY, UserPreferences.DARK_THEME);
                break;
            case "miLight":
                root.getScene().getStylesheets().removeIf(s -> s.equals(UserPreferences.DARK_THEME));
                prefs.setString(UserPreferences.USER_THEME_KEY, UserPreferences.LIGHT_THEME);
                break;
        }
    }

    public void handleSaveToFromJson(ActionEvent actionEvent) throws JsonProcessingException {
//        ObjectMapper mapper = new ObjectMapper();
        ObjectMapper mapper = JsonConfig.createObjectMapper();
        var json = mapper.writeValueAsString(rootData);
            File file = new File("output.json");
        try (FileWriter writer = new FileWriter(file)) {
            writer.write(json);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
//        System.out.println(json);
    }


//    private void addCheckBoxListener(Module parent, TreeItem<Object> parentItem) {
//        parent.getSelected().addListener((obs, oldVal, newVal) -> {
//            for (TreeItem<Object> childItem : parentItem.getChildren()) {
//                if (childItem.getValue() instanceof BaseTestCase) {
//                    ((BaseTestCase) childItem.getValue()).setSelected(newVal);
//                }
//            }
//        });
//    }

//    private void addCheckBoxListener(Microservice parent, TreeItem<Object> parentItem) {
//        parent.getSelected().addListener((obs, oldVal, newVal) -> {
//            for (TreeItem<Object> childItem : parentItem.getChildren()) {
//                Object childValue = childItem.getValue();
//                if (childValue instanceof Module) {
//                    ((Module) childValue).setSelected(newVal);
//                } else if (childValue instanceof BaseTestCase) {
//                    ((BaseTestCase) childValue).setSelected(newVal);
//                }
//                if (!childItem.getChildren().isEmpty()) {
//                    for (TreeItem<Object> grandChildItem : childItem.getChildren()) {
//                        Object grandChildValue = grandChildItem.getValue();
//                        if (grandChildValue instanceof BaseTestCase) {
//                            ((BaseTestCase) grandChildValue).setSelected(newVal);
//                        }
//                    }
//                }
//            }
//        });
//    }


}