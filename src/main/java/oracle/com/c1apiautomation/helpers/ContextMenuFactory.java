package oracle.com.c1apiautomation.helpers;

import com.fasterxml.jackson.core.JsonProcessingException;
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
    private Object clipboardContent;
    private TreeItem<Object> clipboardTreeItem;

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
                                createMenuItem("Copy", e -> CopyRecord(), "copy.png");
                                if (clipboardContent != null && (clipboardContent instanceof Microservice || clipboardContent instanceof Module)) {
                                    createMenuItem("Paste", e -> {
                                        try {
                                            PasteRecord();
                                        } catch (JsonProcessingException ex) {
                                            throw new RuntimeException(ex);
                                        }
                                    }, "paste.png");
                                }
                            }
                            case Module module -> {
                                createMenuItem("Add New Module", null, "addItem.png");
                                createMenuItem("Delete Module", e -> DeleteRecord(), "deleteItem.png");
                                createMenuItem("Add New Test", e -> EditRecord(), "addItem.png");
                                createMenuItem("Copy", e -> CopyRecord(), "copy.png");
                                if (clipboardContent != null && (clipboardContent instanceof BaseTestCase || clipboardContent instanceof Module)) {
                                    createMenuItem("Paste", e -> {
                                        try {
                                            PasteRecord();
                                        } catch (JsonProcessingException ex) {
                                            throw new RuntimeException(ex);
                                        }
                                    }, "paste.png");
                                }

                            }
                            case PreReq preReq -> {
                                createMenuItem("Add New PreReq", e -> EditRecord(), "addItem.png");
                                createMenuItem("Edit PreReq", e -> EditRecord(), "editItem.png");
                                createMenuItem("Delete PreReq", e -> DeleteRecord(), "deleteItem.png");

                                createMenuItem("Copy", e -> CopyRecord(), "copy.png");
                                if (clipboardContent != null && clipboardContent instanceof PreReq) {
                                    createMenuItem("Paste", e -> {
                                        try {
                                            PasteRecord();
                                        } catch (JsonProcessingException ex) {
                                            throw new RuntimeException(ex);
                                        }
                                    }, "paste.png");
                                }

                            }

                            case TestCase testCase -> {
                                createMenuItem("Add New Test Case", e -> EditRecord(), "addItem.png");
                                createMenuItem("Edit Test Case", e -> EditRecord(), "editItem.png");
                                createMenuItem("Delete Test Case", e -> DeleteRecord(), "deleteItem.png");

                                createMenuItem("Copy", e -> CopyRecord(), "copy.png");
                                if (clipboardContent != null && clipboardContent instanceof TestCase) {
                                    createMenuItem("Paste", e -> {
                                        try {
                                            PasteRecord();
                                        } catch (JsonProcessingException ex) {
                                            throw new RuntimeException(ex);
                                        }
                                    }, "paste.png");
                                }

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

    private void CopyRecord() {
        var selectedItem = (TreeItem<Object>) treeTableView.getSelectionModel().getSelectedItem();
        if (selectedItem != null && selectedItem.getValue() instanceof SelectableBase) {

            var selectedObject = selectedItem.getValue();
            Object clonedObject;
            switch (selectedObject)
            {
                case Microservice microservice -> {var temp = microservice.clone(); temp.setName(temp.getName() + " New"); clonedObject = temp; }
                case Module module -> {var temp = module.clone(); temp.setName(temp.getName() + " New"); clonedObject = temp;}
                default -> clonedObject = selectedObject;
            }

            TreeItem<Object> newTreeItem = new TreeItem<>(clonedObject);
            copyTreeItemChildren(selectedItem, newTreeItem);
            clipboardTreeItem = newTreeItem;
            clipboardContent = clonedObject;

            System.out.println(clipboardContent);
        }
    }

    private void PasteRecord() throws JsonProcessingException {
        var selectedItem = (TreeItem<Object>) treeTableView.getSelectionModel().getSelectedItem();
        if (selectedItem != null && (selectedItem.getValue() instanceof SelectableBase)) {

            if (clipboardContent instanceof Microservice) {

                if (selectedItem.getValue() instanceof Root) {
                    ((Root) selectedItem.getValue()).getMicroservices().add( (Microservice) clipboardContent);
                    selectedItem.getChildren().addLast(clipboardTreeItem);
                } else { //it's root
                    ((Root) selectedItem.getParent().getValue()).getMicroservices().add((Microservice) clipboardContent);
                    var index = selectedItem.getParent().getChildren().indexOf(selectedItem);
                    selectedItem.getParent().getChildren().add(index + 1, clipboardTreeItem);
                }
            }

            if (clipboardContent instanceof Module) {

                if (selectedItem.getValue() instanceof Microservice) {
                    ((Microservice) selectedItem.getValue()).getModules().add((Module) clipboardContent);
                    selectedItem.getChildren().addLast(clipboardTreeItem);
                } else { //it's module

                    ((Microservice) selectedItem.getParent().getValue()).getModules().add((Module) clipboardContent);
                    var index = selectedItem.getParent().getChildren().indexOf(selectedItem);
                    selectedItem.getParent().getChildren().add(index + 1, clipboardTreeItem);
                }
            }

            if (clipboardContent instanceof PreReq) {

                if (selectedItem.getValue() instanceof Module) {
                    ((Module) selectedItem.getValue()).getPreReqs().add((PreReq) clipboardContent);
                    selectedItem.getChildren().addFirst(clipboardTreeItem);
                } else { //it's module

                    ((Module) selectedItem.getParent().getValue()).getPreReqs().add((PreReq) clipboardContent);
                    var index = selectedItem.getParent().getChildren().indexOf(selectedItem);
                    selectedItem.getParent().getChildren().add(index + 1, clipboardTreeItem);
                }
            }

            if (clipboardContent instanceof TestCase) {

                if (selectedItem.getValue() instanceof Module) {
                    ((Module) selectedItem.getValue()).getTestCases().add((TestCase) clipboardContent);
                    selectedItem.getChildren().addLast(clipboardTreeItem);
                } else { //it's module

                    ((Module) selectedItem.getParent().getValue()).getTestCases().add((TestCase) clipboardContent);
                    var index = selectedItem.getParent().getChildren().indexOf(selectedItem);
                    selectedItem.getParent().getChildren().add(index + 1, clipboardTreeItem);
                }
            }
            clipboardTreeItem.setExpanded(true);
            treeTableView.refresh();

            clipboardTreeItem = null;
            clipboardContent = null;
        }
    }

    private void copyTreeItemChildren(TreeItem<Object> source, TreeItem<Object> destination) {
        for (TreeItem<Object> child : source.getChildren()) {
            Object childValue = child.getValue();

            // Check if the child is a Microservice
            if (childValue instanceof Microservice) {
                Microservice clonedMicroservice = ((Microservice) childValue).clone();
                TreeItem<Object> newChild = new TreeItem<>(clonedMicroservice);
                destination.getChildren().add(newChild);
                // Recursively copy any children of the Microservice node
                copyTreeItemChildren(child, newChild);
            }
            // Check if the child is a Module
            else if (childValue instanceof Module) {
                Module clonedModule = ((Module) childValue).clone();
                TreeItem<Object> newChild = new TreeItem<>(clonedModule);
                destination.getChildren().add(newChild);
                // Recursively copy any children of the Module node
                copyTreeItemChildren(child, newChild);
            }
            // Handle other types of objects
            else {
                TreeItem<Object> newChild = new TreeItem<>(childValue);
                destination.getChildren().add(newChild);
                copyTreeItemChildren(child, newChild);
            }
        }
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
