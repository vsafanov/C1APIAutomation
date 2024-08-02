package oracle.com.c1apiautomation.helpers;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Modality;
import javafx.stage.Stage;
import oracle.com.c1apiautomation.MainApplication;
import oracle.com.c1apiautomation.controllers.FormController;
import oracle.com.c1apiautomation.model.*;
import oracle.com.c1apiautomation.model.Module;

import java.io.IOException;


public class ContextMenuFactory {

    private TreeTableView<Object> treeTableView;
    ContextMenu contextMenu;

    public ContextMenuFactory(TreeTableView<Object> treeTableView) {
        this.treeTableView = treeTableView;
        contextMenu = new ContextMenu();
    }

    public void CreateContextMenu() {

        treeTableView.setOnContextMenuRequested(event ->
                {
                    contextMenu.getItems().clear();
                    var selectedItem = ((TreeTableView<?>) event.getSource()).getSelectionModel().getSelectedItem();
                    if (selectedItem != null) {

//                        var m = contextMenu.getItems().stream().filter(x->x.getText().equals("Add Microservice")).count();

                        switch (selectedItem.getValue()) {
                            case Microservice microservice -> {
                                createMenuItem("Add New Microservice", null, "addItem.png");
                                createMenuItem("Delete Microservice", e -> DeleteRecord(), "deleteItem.png");
                                createMenuItem("Add New Module", null, "addItem.png");
                            }
                            case Module module -> {
                                createMenuItem("Add New Module", null, "addItem.png");
                                createMenuItem("Delete Module", e -> DeleteRecord(), "deleteItem.png");
                                createMenuItem("Add New Test", e -> EditRecord(), "addItem.png");


                            }
                            case BaseTestCase preReq -> {
                                createMenuItem("Add New Test", e -> EditRecord(), "addItem.png");
                                createMenuItem("Edit Test", e -> EditRecord(), "editItem.png");
                                createMenuItem("Delete Test", e -> DeleteRecord(), "deleteItem.png");
                            }
//                            case  TestCase microservice-> contextMenu.getItems().add( new MenuItem("Add TestCase"));
                            case null, default -> contextMenu.getItems().add(new MenuItem("Non Existing Type"));
                        }
                        System.out.println(selectedItem.getValue());
                    }

                    createInitContextMenu();
//                    contextMenu.show(treeTableView, event.getScreenX(), event.getScreenY());
                }
        );
        //init menu for first load, otherwise it's somehow not showing first time
        createInitContextMenu();

    }

    private void createInitContextMenu() {
        createMenuItem("Expand All", e -> expandTreeView(treeTableView.getRoot(), true), "expand.png");
        createMenuItem("Collapse All", e -> expandTreeView(treeTableView.getRoot(), false), "collapse.png");
        treeTableView.setContextMenu(contextMenu);
    }

    private void createMenuItem(String text, EventHandler<ActionEvent> event, String image) {

        var img = ImageFactory.getImageView(image);
        var mi = new MenuItem(text, img);
        mi.setOnAction(event);
        contextMenu.getItems().add(mi);
    }

    private void AddMenu(ContextMenu contextMenu, MenuItem[] menuItem) {
        for (MenuItem item : menuItem) {
            contextMenu.getItems().add(item);
        }
    }

    private void expandTreeView(TreeItem<?> item, Boolean expand) {
        if (item != null && !item.isLeaf()) {
//            item.setExpanded(expand); - wrong
            for (TreeItem<?> child : item.getChildren()) {
                expandTreeView(child, expand);
                child.setExpanded(expand);
            }
        }
    }

    private void EditRecord() {
        var selectedItem = (TreeItem<Object>) treeTableView.getSelectionModel().getSelectedItem();
        if (selectedItem != null && selectedItem.getValue() instanceof BaseTestCase) {
            BaseTestCase baseTestCase = (BaseTestCase) selectedItem.getValue();
            try {
                FXMLLoader fxmlLoader = new FXMLLoader(MainApplication.class.getResource("form-view.fxml"));

                Parent parent = fxmlLoader.load();

                Stage stage = new Stage();
                Scene scene = new Scene(parent);
                stage.setTitle("Edit Form");

                scene.getStylesheets().add("/app.css");
                stage.setScene(scene);

                // Get the controller and set the BaseTestCase data
                FormController controller = fxmlLoader.getController();
                controller.setBaseTestCase(baseTestCase, treeTableView, selectedItem, stage);
                stage.initModality(Modality.APPLICATION_MODAL);
                stage.show();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    private void DeleteRecord() {
        // create an alert
        Alert a = new Alert(Alert.AlertType.CONFIRMATION);
        a.setTitle("Confirmation");
        a.setHeaderText(null);
        a.setContentText("Are you sure you want to delete this record?");
//        a.setHeaderText("Alert");
//        a.showAndWait().ifPresent(response -> {
//            if (response != ButtonType.OK) {
//                return;
//            }
//        });
        a.showAndWait();
        if (a.getResult().getButtonData() != ButtonBar.ButtonData.OK_DONE) {
            return;
        }

        var selectedItem = (TreeItem<?>) treeTableView.getSelectionModel().getSelectedItem();

        SelectableBase baseItem = (SelectableBase) selectedItem.getValue();
        var parent = selectedItem.getParent();
        if (parent != null) {

            switch (baseItem) {
                case Microservice microservice -> {
                    ((Root) parent.getValue()).getMicroservices().remove(baseItem);
                }
                case Module module -> {
                    ((Microservice) parent.getValue()).getModules().remove(baseItem);
                }
                case PreReq preReq -> {
                    ((Module) parent.getValue()).getPreReqs().remove(baseItem);
                }
                case TestCase testCase -> {
                    ((Module) parent.getValue()).getTestCases().remove(baseItem);
                }

                default -> throw new IllegalStateException("Unexpected value: " + baseItem);
            }
//            selectedItem.getParent().getChildren().remove(selectedItem);
            selectedItem.getParent().getChildren().removeIf(item -> item.getValue().equals(baseItem));

            treeTableView.refresh();

            parent.setExpanded(true);
        }
    }
}
