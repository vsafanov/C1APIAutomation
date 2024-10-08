package oracle.com.c1apiautomation.uihelpers;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import oracle.com.c1apiautomation.MainApplication;
import oracle.com.c1apiautomation.controllers.*;
import oracle.com.c1apiautomation.model.*;
import oracle.com.c1apiautomation.model.Module;
import oracle.com.c1apiautomation.utils.Util;

import java.io.IOException;

public class ContextMenuFactory {

    private TreeTableView<Object> treeTableView;
    private final Environment selectedEnvironment;
    private final Vars runtimeVars = MainApplication.getVars();;
    ContextMenu contextMenu;
    private Object clipboardContent;
    private TreeItem<Object> clipboardTreeItem;

    public ContextMenuFactory(TreeTableView<Object> treeTableView, Environment selectedEnvironment) {
        this.treeTableView = treeTableView;
        this.selectedEnvironment = selectedEnvironment;
//        this.runtimeVars = runtimeVars;
        contextMenu = new ContextMenu();
        contextMenu.setStyle("-fx-padding:10");
    }

    public void CreateContextMenu() {

        treeTableView.setOnContextMenuRequested(event ->
                {
                    contextMenu.getItems().clear();
                    var selectedItem = ((TreeTableView<?>) event.getSource()).getSelectionModel().getSelectedItem();
                    if (selectedItem != null) {

                        switch (selectedItem.getValue()) {
                            case Microservice microservice -> {
                                createMenuItem("New Microservice", e -> EditRecord(e, EditMode.ADD_MICROSERVICE), ImageResource.ICON_ADD_NEW);
                                createMenuItem("Delete Microservice", e -> DeleteRecord(), ImageResource.ICON_DELETE);
                                createMenuItem("New Module", e -> EditRecord(e, EditMode.ADD_MODULE), ImageResource.ICON_ADD_NEW);
                                contextMenu.getItems().add(new SeparatorMenuItem());
                                createMenuItem("Copy", e -> CopyRecord(), ImageResource.ICON_COPY);
                                if (clipboardContent != null && (clipboardContent instanceof Microservice || clipboardContent instanceof Module)) {
                                    createMenuItem("Paste", e -> PasteRecord(), ImageResource.ICON_PASTE);
                                }
                            }
                            case Module module -> {
                                createMenuItem("New Module", e -> EditRecord(e, EditMode.ADD_MODULE), ImageResource.ICON_ADD_NEW);
                                createMenuItem("Delete Module", e -> DeleteRecord(), ImageResource.ICON_DELETE);
                                createMenuItem("New PreReq", e -> EditRecord(e, EditMode.ADD_PREREQ), ImageResource.ICON_ADD_NEW);
                                createMenuItem("New Test", e -> EditRecord(e, EditMode.ADD_TESTCASE), ImageResource.ICON_ADD_NEW);
                                contextMenu.getItems().add(new SeparatorMenuItem());
                                createMenuItem("Copy", e -> CopyRecord(), ImageResource.ICON_COPY);
                                if (clipboardContent != null && (clipboardContent instanceof BaseTestCase || clipboardContent instanceof Module)) {
                                    createMenuItem("Paste", e -> PasteRecord(), ImageResource.ICON_PASTE);
                                }

                            }
                            case PreReq preReq -> {
                                createMenuItem("New PreReq", e -> EditRecord(e, EditMode.ADD_PREREQ), ImageResource.ICON_ADD_NEW);
                                createMenuItem("Edit PreReq", e -> EditRecord(e, EditMode.EDIT_PREREQ), ImageResource.ICON_EDIT);
                                createMenuItem("Delete PreReq", e -> DeleteRecord(), ImageResource.ICON_DELETE);
                                contextMenu.getItems().add(new SeparatorMenuItem());
                                createMenuItem("Copy", e -> CopyRecord(), ImageResource.ICON_COPY);
                                if (clipboardContent != null && clipboardContent instanceof PreReq) {
                                    createMenuItem("Paste", e -> PasteRecord(), ImageResource.ICON_PASTE);
                                }
                            }

                            case TestCase testCase -> {
                                createMenuItem("New Test Case", e -> EditRecord(e, EditMode.ADD_TESTCASE), ImageResource.ICON_ADD_NEW);
                                createMenuItem("Edit Test Case", e -> EditRecord(e, EditMode.EDIT_TESTCASE), ImageResource.ICON_EDIT);
                                createMenuItem("Delete Test Case", e -> DeleteRecord(), ImageResource.ICON_DELETE);
                                createMenuItem("Run Request", e -> RunRequest(e), ImageResource.ICON_RUN_REQUEST);
                                createMenuItem("Run Test", e -> RunTest(e), ImageResource.ICON_RUN_TEST);
                                contextMenu.getItems().add(new SeparatorMenuItem());
                                createMenuItem("Copy", e -> CopyRecord(), ImageResource.ICON_COPY);
                                if (clipboardContent != null && clipboardContent instanceof TestCase) {
                                    createMenuItem("Paste", e -> PasteRecord(), ImageResource.ICON_PASTE);
                                }

                            }
                            case null, default -> contextMenu.getItems().add(new MenuItem("Non Existing Type"));
                        }
                        System.out.println(selectedItem.getValue());
                        contextMenu.getItems().add(new SeparatorMenuItem());
                    }
                    createInitContextMenu();
                }
        );
        //init menu for first load, otherwise it's somehow not showing first time
        createInitContextMenu();
    }

    private void RunTest(ActionEvent event) {
        if (selectedEnvironment == null) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setHeaderText("");
            alert.setContentText("Please select the Environment you want to run");
            alert.showAndWait();
            return;
        }
        var selectedItem = (TreeItem<Object>) treeTableView.getSelectionModel().getSelectedItem();
        var mi = ((MenuItem) event.getTarget()).getParentPopup();
        Scene scene = mi.getScene();

        var testRunController = new TestRunController(scene, selectedEnvironment);
        var testCase = (TestCase) selectedItem.getValue();
        try {
            testRunController.OpenRequestDialog(testCase);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void RunRequest(ActionEvent event) {

        if (selectedEnvironment == null) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setHeaderText("");
            alert.setContentText("Please select the Environment you want to run");
            alert.showAndWait();
//            if (alert.getResult() == ButtonType.OK) {
                return;
//            }
        }
        var selectedItem = (TreeItem<Object>) treeTableView.getSelectionModel().getSelectedItem();
        var mi = ((MenuItem) event.getTarget()).getParentPopup();
        Scene scene = mi.getScene();


        var apiRequestController = new ApiRequestController(scene, selectedEnvironment, runtimeVars);
        try {
            var testCase = (TestCase) selectedItem.getValue();
            apiRequestController.OpenRequestDialog(testCase);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void EditRecord(ActionEvent event, EditMode editMode) {
        var selectedItem = (TreeItem<Object>) treeTableView.getSelectionModel().getSelectedItem();
        var mi = ((MenuItem) event.getTarget()).getParentPopup();
        Scene scene = mi.getScene();
        var formController = new FormController(scene);
        try {
            switch (editMode) {
                case ADD_MICROSERVICE -> {
                    formController.addMicroservice(selectedItem);
                }
                case ADD_MODULE -> {
                    formController.addModule(selectedItem);
                }
                case ADD_PREREQ, ADD_TESTCASE, EDIT_PREREQ, EDIT_TESTCASE -> {
                    BaseTestCase baseTestCase = null;
                    if (selectedItem != null && selectedItem.getValue() instanceof BaseTestCase ) {
                        baseTestCase = (BaseTestCase) selectedItem.getValue();
                    }
                    formController.EditBaseTest(baseTestCase, treeTableView, selectedItem, editMode);
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

    private void CopyRecord() {
        var selectedItem = (TreeItem<Object>) treeTableView.getSelectionModel().getSelectedItem();
        if (selectedItem != null && selectedItem.getValue() instanceof SelectableBase) {

            var selectedObject = selectedItem.getValue();
            Object clonedObject;
            switch (selectedObject) {
                case Microservice microservice -> {
                    var temp = microservice.clone();
                    temp.setName(temp.getName() + " New");
                    clonedObject = temp;
                }
                case Module module -> {
                    var temp = module.clone();
                    temp.setName(temp.getName() + " New");
                    clonedObject = temp;
                }
                case TestCase testCase -> {
                    var temp = testCase.clone();
                    temp.setId(temp.getId() + " New");
                    clonedObject = temp;
                }
                case PreReq preReq -> {
                    var temp = preReq.clone();
                    temp.setId(temp.getId() + " New");
                    clonedObject = temp;
                }
                default -> clonedObject = selectedObject;
            }

            TreeItem<Object> newTreeItem = new TreeItem<>(clonedObject);
            //add event for Microservices and Modules root checkboxes
            if(clonedObject instanceof Module || clonedObject instanceof Microservice) {
                Util.addCheckBoxListener((SelectableBase) clonedObject, newTreeItem);
            }
            copyTreeItemChildren(selectedItem, newTreeItem);
            clipboardTreeItem = newTreeItem;
            clipboardContent = clonedObject;

            System.out.println(clipboardContent);
        }
    }

    private void PasteRecord() {
        var selectedItem = (TreeItem<Object>) treeTableView.getSelectionModel().getSelectedItem();
        if (selectedItem != null && (selectedItem.getValue() instanceof SelectableBase)) {

            if (clipboardContent instanceof Microservice) {

                if (selectedItem.getValue() instanceof Root) {
                    ((Root) selectedItem.getValue()).getMicroservices().add((Microservice) clipboardContent);
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
                //add event for Microservices checkboxes
                Util.addCheckBoxListener(clonedMicroservice, newChild);
                destination.getChildren().add(newChild);
                // Recursively copy any children of the Microservice node
                copyTreeItemChildren(child, newChild);
            }
            // Check if the child is a Module
            else if (childValue instanceof Module) {
                Module clonedModule = ((Module) childValue).clone();
                TreeItem<Object> newChild = new TreeItem<>(clonedModule);
                //add event for Module checkboxes
                Util.addCheckBoxListener(clonedModule, newChild);
                destination.getChildren().add(newChild);
                // Recursively copy any children of the Module node
                copyTreeItemChildren(child, newChild);
            }
            // Handle other types of objects
            else {
                BaseTestCase clonedTestCase = ((BaseTestCase) childValue).clone();
                TreeItem<Object> newChild = new TreeItem<>(clonedTestCase);
                destination.getChildren().add(newChild);
                copyTreeItemChildren(child, newChild);
            }
        }
    }

    private void createInitContextMenu() {
        createMenuItem("Expand All", e -> expandTreeView(treeTableView.getRoot(), true), ImageResource.ICON_EXPAND_ALL);
        createMenuItem("Collapse All", e -> expandTreeView(treeTableView.getRoot(), false), ImageResource.ICON_COLLAPSE_ALL);
        treeTableView.setContextMenu(contextMenu);
    }

    private void createMenuItem(String text, EventHandler<ActionEvent> event, String image) {

        ImageView img;
        MenuItem mi;
        if(image.isEmpty()) {
            mi = new MenuItem(text);
        }else{
            img = ImageFactory.getImageView(image);
            mi = new MenuItem(text, img);
        }
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
