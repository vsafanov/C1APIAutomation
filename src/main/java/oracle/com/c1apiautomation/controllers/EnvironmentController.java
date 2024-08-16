package oracle.com.c1apiautomation.controllers;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.layout.GridPane;
import oracle.com.c1apiautomation.MainApplication;
import oracle.com.c1apiautomation.Utils;
import oracle.com.c1apiautomation.helpers.ImageFactory;
import oracle.com.c1apiautomation.model.Environment;
import oracle.com.c1apiautomation.model.PropertyItem;
import oracle.com.c1apiautomation.model.Vars;
import oracle.com.c1apiautomation.testapifactory.ContentType;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

public class EnvironmentController {

    public TableView<PropertyItem> tblEnv;
    public TableColumn<PropertyItem, String> colName;
    public TableColumn<PropertyItem, String> colValue;
    public ButtonType btnCancel;
    public ComboBox cmbEnv;
    public Button btnDelete;
    public Button btnCopy;
    public Button btnRename;

    private Scene scene;
    public MenuBar menuBarEnv;
    List<Environment> environments;
    ObservableList<PropertyItem> items;
    private Map<String, String> originalProperties;
    private Environment selectedEnvironment;
    private MainController mainController;

    public EnvironmentController() {
    }

    public EnvironmentController(Scene scene, List<Environment> environments) {
        this.scene = scene;
        this.environments = environments;
    }

    public void setMainController(MainController mainController) {
        this.mainController = mainController;
    }

    private void createImageButton(Button btn, String image, String tooltip) {
        btn.setText("");
        var img = ImageFactory.getImageView(image);
        btn.setTooltip(new Tooltip(tooltip));
        btn.setGraphic(img);
        btn.setPrefWidth(img.getFitWidth());
        btn.setPrefHeight(img.getFitHeight());
    }

    public void initialize() {

        //init buttons
        createImageButton(btnCopy, "copygreen.png", "Copy Environment");
        createImageButton(btnDelete, "deletegreen.png", "Delete Environment");
        createImageButton(btnRename, "renamegreen.png", "Rename Environment");

        //getClass().getResource("/oracle/com/c1apiautomation/images/copy.png")  - ! option for controller use
        colName.setCellValueFactory(new PropertyValueFactory<>("name"));
        colValue.setCellValueFactory(new PropertyValueFactory<>("value"));

        // Bind the second column to take the remaining width of the table
        colValue.prefWidthProperty().bind(tblEnv.widthProperty().subtract(colName.getPrefWidth()) );

        // Enable cell editing
        tblEnv.setEditable(true);
        colName.setCellFactory(TextFieldTableCell.forTableColumn());
        colValue.setCellFactory(TextFieldTableCell.forTableColumn());

        // Commit edit handlers for name and value columns
        colName.setOnEditCommit(event -> updateKey(event.getRowValue().getName(), event.getNewValue()));
        colValue.setOnEditCommit(event -> updateValue(event.getRowValue().getName(), event.getNewValue()));

        // Context menu for adding and removing items
        ContextMenu contextMenu = new ContextMenu();
        MenuItem addItem = new MenuItem("Add");
        MenuItem removeItem = new MenuItem("Remove");

        addItem.setOnAction(e -> addEntry());
        removeItem.setOnAction(e -> removeEntry(tblEnv.getSelectionModel().getSelectedItem()));

        contextMenu.getItems().addAll(addItem, removeItem);
        tblEnv.setContextMenu(contextMenu);
    }

    private Environment getEnvironmentByName(String name) {
        return name == null ?
                environments.getFirst() :
                environments.stream().filter(e -> e.getName().equals(name)).findFirst().orElse(null);
    }

    public void OpenEnvironmentDialog(String name) throws IOException {
        var dlg = new Dialog();
        dlg.setTitle("ENVIRONMENTS");
        var fxmlLoader = Utils.OpenDialog(dlg, "environment-view.fxml", scene);
        var controller = (EnvironmentController) fxmlLoader.getController();

        controller.environments = environments;
        var env = getEnvironmentByName(name);
        controller.selectedEnvironment = env;
        controller.originalProperties = new HashMap<>(env.getVars().getProperties());
        //init cmbEnv with values
        if (environments != null) {
            controller.cmbEnv.getItems().clear();
            var names = environments.stream().map(Environment::getName).collect(Collectors.toList());
            controller.cmbEnv.setItems(FXCollections.observableArrayList(names));
            controller.cmbEnv.setValue(env.getName());

            controller.loadEnvTable(name);
        }

        // Attach event to the Cancel button
        Button cancelButton = (Button) dlg.getDialogPane().lookupButton(controller.btnCancel);
        cancelButton.setOnAction(event -> {
            // Call the method to cancel changes
            controller.cancelChanges();
            dlg.close();
        });
        dlg.showAndWait();
        if (dlg.getResult() == ButtonType.OK) {
            mainController.UpdateEnvMenu();

        }
    }

    private void cancelChanges() {
        if (originalProperties != null && selectedEnvironment != null) {
            Vars vars = selectedEnvironment.getVars();
            vars.getProperties().clear();
            vars.getProperties().putAll(originalProperties);
        }
    }

    private void loadEnvTable(String name) {

        selectedEnvironment = getEnvironmentByName(name);
        if (selectedEnvironment != null) {

            Vars vars = selectedEnvironment.getVars();
            Map<String, String> properties = vars.getProperties();

            items = FXCollections.observableArrayList();

            for (Map.Entry<String, String> entry : properties.entrySet()) {
                items.add(new PropertyItem(entry.getKey(), entry.getValue()));
            }

            tblEnv.setItems(items);
        }
    }

    private void addEntry() {
        PropertyItem newItem = new PropertyItem("", "");
        items.add(newItem);
//        updateHashMap(key, value);

        // Select the newly added item
        tblEnv.getSelectionModel().select(newItem);

        // Focus on the newly added item
        int rowIndex = items.size() - 1;
        tblEnv.scrollTo(rowIndex);
        tblEnv.getFocusModel().focus(rowIndex, colName); // Focus on the key column of the new row
        tblEnv.edit(rowIndex, colName); // Start editing the key column
    }

    private void updateValue(String key, String newValue) {
        PropertyItem item = findItemByKey(key);
        if (item != null) {
            item.setValue(newValue);
            selectedEnvironment.getVars().getProperties().put(key, newValue);
//            updateHashMap(key, newValue);
        }
    }

    private void updateKey(String oldKey, String newKey) {
        PropertyItem item = findItemByKey(oldKey);
        if (item != null) {
            // Update the HashMap with the new key
            String value = item.getValue();
            var properties = selectedEnvironment.getVars().getProperties();
            properties.remove(oldKey); // Remove the old key
            properties.put(newKey, value); // Add the new key-value pair

            // Update the TableView
            item.setName(newKey);
            tblEnv.refresh();
        }
    }

    private void removeEntry(PropertyItem item) {
        if (item != null) {
            items.remove(item);
            selectedEnvironment.getVars().getProperties().remove(item.getName());
        }
    }

    private PropertyItem findItemByKey(String key) {
        Optional<PropertyItem> result = items.stream().filter(i -> i.getName().equals(key)).findFirst();
        return result.orElse(null);
    }

    public void onEnvChanged(ActionEvent actionEvent) {
        var name = ((ComboBox) actionEvent.getSource()).getValue();
        if (name == null) name = selectedEnvironment.getName();
        cmbEnv.setValue(name);
        loadEnvTable(name.toString());
    }

    public void onDeleteEnv(ActionEvent actionEvent) {
        Alert a = new Alert(Alert.AlertType.CONFIRMATION);
        a.setTitle("Confirmation");
        a.setHeaderText(null);
        a.setContentText("Are you sure you want to delete " + selectedEnvironment.getName() + " ?");
        a.showAndWait();
        if (a.getResult().getButtonData() != ButtonBar.ButtonData.OK_DONE) {
            return;
        }

        if (selectedEnvironment != null) {
            var curName = selectedEnvironment.getName();
            environments.remove(selectedEnvironment);
            selectedEnvironment = environments.getFirst(); // Select the first environment after deletion
            cmbEnv.getItems().remove(curName);   //this will call onEnvChanged
        }
    }

    public void onCopyEnv(ActionEvent actionEvent) {
//        var dialog = createDialog("Copy Environment", "New Environment Name");
        var dialog = createInputDialog("Copy Environment","New Environment Name",selectedEnvironment.getName() + "- Copy");
        Optional<String> result = dialog.showAndWait();

        result.ifPresent(name -> {
            if (selectedEnvironment != null) {
                Environment copiedEnv = new Environment();
                copiedEnv.setName(name);
                copiedEnv.setVars(selectedEnvironment.getVars());
                environments.add(copiedEnv);
                selectedEnvironment = copiedEnv;
                cmbEnv.getItems().add(copiedEnv.getName());
                cmbEnv.setValue(copiedEnv.getName());
            }
        });
    }

    public void onRenameEnv(ActionEvent actionEvent) {
        if (selectedEnvironment != null) {
            var dialog = createInputDialog("Rename Environment","Environment Name",selectedEnvironment.getName());

            Optional<String> result = dialog.showAndWait();
            result.ifPresent(newName -> {

                cmbEnv.getItems().set(cmbEnv.getItems().indexOf(selectedEnvironment.getName()), newName);
                selectedEnvironment.setName(newName);
                cmbEnv.setValue(newName);
            });
        }
    }
    private Dialog<String> createInputDialog(String title, String labelText, String textboxText ){
        TextInputDialog dialog = new TextInputDialog(textboxText);
        dialog.setTitle(title);
        dialog.setHeaderText(null);
        dialog.setContentText(labelText);
        return dialog;
    }

    private Dialog<String> createDialog(String title, String labelText) {
        // Create the dialog
        Dialog<String> dialog = new Dialog<>();
        dialog.setTitle(title);

        // Set the button types
        ButtonType okButtonType = new ButtonType("OK", ButtonBar.ButtonData.OK_DONE);
        ButtonType cancelButtonType = new ButtonType("Cancel", ButtonBar.ButtonData.CANCEL_CLOSE);
        dialog.getDialogPane().getButtonTypes().addAll(okButtonType, cancelButtonType);

        // Create the label and text field
        Label label = new Label(labelText);
        TextField textField = new TextField();

        // Create a GridPane for layout
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.add(label, 0, 0);
        grid.add(textField, 1, 0);

        dialog.getDialogPane().setContent(grid);

        // Convert the result to a string when the OK button is clicked
        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == okButtonType) {
                return textField.getText();
            }
            return null;
        });

        return dialog;
    }



//    private void updateHashMap(String key, String value) {
//        Vars vars = environments.getFirst().getVars(); // Assuming the first environment is used
//        vars.getProperties().put(key, value);
//
//        selectedEnvironment.getVars().getProperties().put(key,value);
//    }

}
