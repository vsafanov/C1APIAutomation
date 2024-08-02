package oracle.com.c1apiautomation.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import javafx.beans.property.*;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;

import javafx.scene.control.cell.TextFieldTreeTableCell;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import oracle.com.c1apiautomation.MainApplication;
import oracle.com.c1apiautomation.Utils;
import oracle.com.c1apiautomation.helpers.CheckBoxTreeTableCell;
import oracle.com.c1apiautomation.helpers.ContextMenuFactory;
import oracle.com.c1apiautomation.helpers.ImageTreeTableCell;
import oracle.com.c1apiautomation.helpers.RequestTypeTreeTableCell;
import oracle.com.c1apiautomation.model.*;
import oracle.com.c1apiautomation.model.Module;

import java.io.File;
import java.io.IOException;
import java.util.List;

import static org.controlsfx.tools.Utils.getWindow;

public class MainController {

    public TreeTableView ttvContainer;
    public MenuBar menuBar;
    public MenuItem v1;


    public void initialize() throws IOException {

        // Load data
        Root rootData = Utils.readJson("C:\\Users\\VSAFANOV\\Documents\\My Super Deals Private Folder\\Sandbox\\JavaFX\\C1TestAPIMicroservices.json");
//        Root rootData = Utils.readJson("C:\\temp\\C1TestAPIMicroservices.json");

//       v1.setOnAction(e->handleLoadFromJson());



        // Add a column for checkboxes
        TreeTableColumn<Object, Boolean> checkBoxColumn = new TreeTableColumn<>("Run Test");
        checkBoxColumn.setPrefWidth(85);

        checkBoxColumn.setCellValueFactory(param -> {
            Object value = param.getValue().getValue();
            return value instanceof SelectableBase ? ((SelectableBase) value).getSelected() : new SimpleBooleanProperty(false);

//            return switch (value) {
//                case Microservice microservice -> microservice.getSelected();
//                case Module module -> module.getSelected();
//                case BaseTestCase baseTestCase -> baseTestCase.getSelected();
//                case null, default -> new SimpleBooleanProperty(false);
//            };
        });
        checkBoxColumn.setCellFactory(CheckBoxTreeTableCell.forTreeTableColumn());

        var microserviceColumn = Utils.createStringColumn("Microservice", Microservice.class, Microservice::getName, 100);
        microserviceColumn.setCellFactory(TextFieldTreeTableCell.forTreeTableColumn());

        var moduleColumn = Utils.createStringColumn("Module", Module.class, Module::getName, 120); // Adjust accordingly
        moduleColumn.setCellFactory(TextFieldTreeTableCell.forTreeTableColumn());
//        moduleColumn.setOnEditCommit(event -> {
//            TreeItem<Object> currentItem = event.getTreeTablePosition().getTreeItem();
//            ((Module) currentItem.getValue()).setName(event.getNewValue());
//        });


        // Define the image column
        TreeTableColumn<Object, String> imageColumn = new TreeTableColumn<>("...");
        imageColumn.setCellValueFactory(param -> new SimpleStringProperty("")); // Dummy value
        imageColumn.setCellFactory(column -> new ImageTreeTableCell());

        var idColumn = Utils.createStringColumn("Test Case ID", BaseTestCase.class, BaseTestCase::getId, 150);
        var titleColumn = Utils.createStringColumn("Title", BaseTestCase.class, BaseTestCase::getTitle, 100);
        var descriptionColumn = Utils.createStringColumn("Description", BaseTestCase.class, BaseTestCase::getDescription, 100);

        var requestTypeColumn = Utils.createStringColumn("Method", BaseTestCase.class, BaseTestCase::getRequestType, 50);
        requestTypeColumn.setCellFactory(column -> new RequestTypeTreeTableCell());

        var requestServiceUrlColumn = Utils.createStringColumn("Service Url", BaseTestCase.class, BaseTestCase::getServiceUrl, 150);
        var requestRequestParamsColumn = Utils.createStringColumn("Request Params", BaseTestCase.class, BaseTestCase::getRequestParams, 150);
        var expectedResponseCodeColumn = Utils.createStringColumn("Response Code", BaseTestCase.class, BaseTestCase::getExpectedResponseCode, 100);
        var userNameColumn = Utils.createStringColumn("User Name", BaseTestCase.class, BaseTestCase::getUserName, 100);
        var passwordColumn = Utils.createStringColumn("Password", BaseTestCase.class, BaseTestCase::getPassword, 100);

        ttvContainer.getColumns().addAll(checkBoxColumn, microserviceColumn, moduleColumn, imageColumn, idColumn,
                titleColumn, descriptionColumn, requestTypeColumn, requestServiceUrlColumn,
                requestRequestParamsColumn, expectedResponseCodeColumn, userNameColumn, passwordColumn);

        //disable s sorting
        for (var column : ttvContainer.getColumns()) {
            ((TreeTableColumn<?, ?>) column).setSortable(false);
        }

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

//        var microservices = rootData.getMicroservices();
        // Populate tree items
        for (Microservice microservice : rootData.getMicroservices()) {
//            Microservice microservice = item.getMicroservice();
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
        var contextMenu = new ContextMenuFactory(ttvContainer);
        contextMenu.CreateContextMenu();

        ttvContainer.setTableMenuButtonVisible(true);

//        ttvContainer.getColumns().add(microserviceColumn);

        ttvContainer.setShowRoot(false);
//        ttvContainer.setShowRoot(true);

    }

    @FXML
    private void handleLoadFromJson() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("JSON Files", "*.json"));
        File file = fileChooser.showOpenDialog(null);

        if (file != null) {
//            loadJsonFileIntoTreeTableView(file);
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
        controller.setBaseTestCase(baseTestCase, ttvContainer, selectedItem, stage);
        stage.show();


//ttvContainer.refresh();
        ObjectWriter ow = new ObjectMapper().writer().withDefaultPrettyPrinter();
        String json = ow.writeValueAsString(baseTestCase);

        System.out.println("Double-clicked on: " + baseTestCase.getSelected());
        System.out.println(json);
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