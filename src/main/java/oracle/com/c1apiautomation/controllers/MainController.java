package oracle.com.c1apiautomation.controllers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import javafx.beans.property.*;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;

import javafx.scene.control.TreeTableColumn.CellEditEvent;
import javafx.scene.control.cell.TextFieldTreeTableCell;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.VBox;
import javafx.stage.DirectoryChooser;
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
import java.util.stream.Collectors;

public class MainController {

    public TreeTableView ttvContainer;
    public MenuBar menuBar;
    public MenuItem miLoadJson;
    public VBox root;
    public Label lblEnv;
    public TextField txtSearch;
//    public Image imgSaveToJson;
    public ImageView imgViewSaveToJson;
    Root rootData;
    Environment selectedEnvironment;
    private String rootFolder = "C:\\Users\\VSAFANOV\\Documents\\My Super Deals Private Folder\\Sandbox\\JavaFX";
    private Vars runtimeVars = MainApplication.getVars();

    public void initialize() throws IOException {
        var img = ImageFactory.getImageView(ImageResource.ICON_SAVE).getImage();
        imgViewSaveToJson.setImage(img);
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

    private void addContextMenu() {
        var contextMenu = new ContextMenuFactory(ttvContainer, selectedEnvironment);
        contextMenu.CreateContextMenu();
    }

    private void createEnvironmentMenus() {
        var envMenu = menuBar.getMenus().getLast().getItems();
        var selectedText = getSelectedEnvironment();
        envMenu.clear();
        ToggleGroup toggleGroup = new ToggleGroup();

        lblEnv.getStyleClass().add("label-env-inactive");
        for (Environment env : rootData.getEnvironments()) {
            RadioMenuItem mi = new RadioMenuItem(env.getName());
            if (mi.getText().equals(selectedText)) {
                mi.setSelected(true);
            }
            mi.setToggleGroup(toggleGroup);
            mi.setOnAction(e -> {
                selectedEnvironment = env;
                lblEnv.getStyleClass().removeAll("label-env-inactive");
                lblEnv.getStyleClass().add("label-env-active");
                lblEnv.setText(env.getName());

                //Has to recreate ContextMenu after environment changed
                addContextMenu();
            });
            envMenu.add(mi);
        }
        var newSelectedText = getSelectedEnvironment();
        if (newSelectedText.isEmpty()) {
            lblEnv.getStyleClass().add("label-env-inactive");
            lblEnv.getStyleClass().removeAll("label-env-active");
            lblEnv.setText("No Environment.");
            selectedEnvironment = null;
        }
        SeparatorMenuItem smi = new SeparatorMenuItem();
        envMenu.add(smi);

        MenuItem config = new MenuItem("Configure", ImageFactory.getImageView(ImageResource.ICON_CONFIGURE));
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
//rootData.getMicroservices().stream().map(f->f.getModules().stream().map(t->t.getPreReqs()))
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
        var authColumn = Util.createStringColumn("Auth", BaseTestCase.class, BaseTestCase::getUseAuthentication, 50);
        var requestRequestParamsColumn = Util.createStringColumn("Payload", BaseTestCase.class, BaseTestCase::getPayload, 150);
        var expectedResponseCodeColumn = Util.createStringColumn("Status Code", BaseTestCase.class, BaseTestCase::getExpectedResponseCode, 75);
        var expectedResponseColumn = Util.createStringColumn("Expected Response", BaseTestCase.class, BaseTestCase::getExpectedResponse, 150);

        var inputColumn = Util.createStringColumn("Input Vars", BaseTestCase.class, BaseTestCase::getInput, 100);
//        var userNameColumn = Utils.createStringColumn("User Name", BaseTestCase.class, BaseTestCase::getUserName, 100);
//        var passwordColumn = Utils.createStringColumn("Password", BaseTestCase.class, BaseTestCase::getPassword, 100);

        ttvContainer.getColumns().addAll(checkBoxColumn, microserviceColumn, moduleColumn, imageColumn, idColumn,
                titleColumn, descriptionColumn, requestTypeColumn, requestServiceUrlColumn, authColumn,
                requestRequestParamsColumn, expectedResponseCodeColumn, expectedResponseColumn, inputColumn);

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
        ttvContainer.setOnMouseClicked(event -> {
            var scene = ((Node) event.getSource()).getScene();
            if (event.getClickCount() == 2) {
                var selectedItem = (TreeItem) ttvContainer.getSelectionModel().getSelectedItem();
                if (selectedItem != null && selectedItem.getValue() instanceof BaseTestCase baseTestCase) {
                    try {
                        var editMode = baseTestCase instanceof PreReq ? EditMode.EDIT_PREREQ : EditMode.EDIT_TESTCASE;
//                        handleDoubleClick(baseTestCase, selectedItem);
                        var formController = new FormController(scene);
                        formController.EditBaseTest(baseTestCase, ttvContainer, selectedItem, editMode);
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                }
            }
        });

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
        addContextMenu();

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

    @FXML //! must keep it @FXML
    private void handleLoadFromJson() throws IOException {
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("JSON Files", "*.json"));
        fileChooser.setTitle("Select Jason API Test Project");
        fileChooser.setInitialDirectory(new File(rootFolder));
        File file = fileChooser.showOpenDialog(null);

        if (file != null) {
            LoadTreeTableView(file.getPath());
            menuBar.getMenus().getLast().getItems().clear();
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
//        FXMLLoader fxmlLoader = new FXMLLoader(MainApplication.class.getResource("form-view.fxml"));
//
//        Parent parent = fxmlLoader.load();
//
//        Stage stage = new Stage();
//        Scene scene = new Scene(parent);
//
//        stage.setTitle("Edit Form");
//        scene.getStylesheets().add("/app.css");
//        stage.setScene(scene);
//
//        // Get the controller and set the BaseTestCase data
//        FormController controller = fxmlLoader.getController();
////        controller.setBaseTestCase(baseTestCase, ttvContainer, selectedItem, stage);
//        stage.show();


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

    public void handleSaveToJson(ActionEvent actionEvent) throws JsonProcessingException {

        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Choose Save Location and Filename");
        FileChooser.ExtensionFilter jsonFilter = new FileChooser.ExtensionFilter("JSON Files (*.json)", "*.json");
        fileChooser.getExtensionFilters().add(jsonFilter);
        fileChooser.setInitialFileName("ApiTest.json"); // Set default filename
        fileChooser.setInitialDirectory(new File(rootFolder));

        File selectedFile = fileChooser.showSaveDialog(null);

        if (selectedFile != null) {
            String filename = selectedFile.getName();
            if (!filename.endsWith(".json")) {
                selectedFile = new File(selectedFile.getParent(), filename + ".json"); // Update selectedFile
            }
            try {
                ObjectMapper mapper = JsonConfig.createObjectMapper();
                var json = mapper.writeValueAsString(rootData);

                FileWriter writer = new FileWriter(selectedFile);
                writer.write(json);
                writer.close();

                System.out.println("JSON data saved successfully to " + selectedFile.getAbsolutePath());
            } catch (IOException e) {
                e.printStackTrace();
                System.err.println("Error saving JSON data: " + e.getMessage());
            }
        }

//        ObjectMapper mapper = JsonConfig.createObjectMapper();
//        var json = mapper.writeValueAsString(rootData);
//            File file = new File("output.json");
//        try (FileWriter writer = new FileWriter(file)) {
//            writer.write(json);
//        } catch (IOException e) {
//            throw new RuntimeException(e);
//        }
//        System.out.println(json);
    }

    private boolean filterTree(TreeItem<String> root, String searchTerm) {
        if (searchTerm == null || searchTerm.isEmpty()) {
            // Show all nodes and expand everything if search term is empty
            for (TreeItem<String> child : root.getChildren()) {
//                child.setVisible(true);  NOT WORKING!
                filterTree(child, searchTerm);
            }
            return true;
        }

        boolean found = false;

        // Recursively filter children
        for (TreeItem<String> child : root.getChildren()) {
            boolean childFound = filterTree(child, searchTerm);
            found |= childFound; // Aggregate result to see if this branch should be visible
        }

        // Check if the current node matches the search term
        boolean matches = root.getValue().toLowerCase().contains(searchTerm.toLowerCase());

        // Set visibility: visible if it matches or any child matches
//        root.getParent().getChildren().removeIf(item -> !matches && !found);

        return matches || found;
    }


    public void searchText(ActionEvent actionEvent) {
//        var d =  rootData.getMicroservices().stream().map(f -> f.getModules().stream().map(t -> t.getPreReqs().stream().filter(p -> p.contains(txtSearch.getText()))));
        rootData.getMicroservices().remove(2);
//        TreeItem<Object> rootItem = new TreeItem<>(d);
//        ttvContainer.setRoot(rootItem);
        ttvContainer.refresh();
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