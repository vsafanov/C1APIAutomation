package oracle.com.c1apiautomation;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import javafx.beans.property.*;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.TreeTableColumn.CellDataFeatures;
import javafx.scene.control.cell.CheckBoxTreeCell;

import javafx.scene.control.cell.TreeItemPropertyValueFactory;

import java.awt.event.WindowStateListener;
import java.io.Console;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.Method;
import java.util.List;

public class HelloController {


    public TreeTableView ttvContainer;

    @FXML
    private Label welcomeText;

    @FXML
    protected void onHelloButtonClick() {
        welcomeText.setText("Welcome to JavaFX Application!");
    }



    public  void initialize() throws IOException {


        // Load data
        List<Root> rootData = JsonUtil.readJson("C:\\temp\\C1TestAPIMicroservices.json");

        // Add a column for checkboxes
        TreeTableColumn<Object, Boolean> checkBoxColumn = new TreeTableColumn<>("Run Test");
        checkBoxColumn.setPrefWidth(80);
        checkBoxColumn.setCellValueFactory(param -> {
            Object value = param.getValue().getValue();
            return value instanceof SelectableBase?((SelectableBase) value).getSelected():new SimpleBooleanProperty(false);

//            return switch (value) {
//                case Microservice microservice -> microservice.getSelected();
//                case Module module -> module.getSelected();
//                case BaseTestCase baseTestCase -> baseTestCase.getSelected();
//                case null, default -> new SimpleBooleanProperty(false);
//            };
        });
        checkBoxColumn.setCellFactory(CheckBoxTreeTableCell.forTreeTableColumn());

        var microserviceColumn = JsonUtil.createStringColumn("Microservice",Microservice.class, Microservice::getName,120);

        var moduleColumn = JsonUtil.createStringColumn("Module",Module.class, Module::getName, 120); // Adjust accordingly

        var idColumn = JsonUtil.createStringColumn("ID",BaseTestCase.class, BaseTestCase::getId,150);
        var titleColumn = JsonUtil.createStringColumn("Title", BaseTestCase.class,BaseTestCase::getTitle, 100);
        var descriptionColumn = JsonUtil.createStringColumn("Description",BaseTestCase.class, BaseTestCase::getDescription,100);
        var requestTypeColumn = JsonUtil.createStringColumn("Request Type", BaseTestCase.class,BaseTestCase::getRequestType,60);
        var requestServiceUrl = JsonUtil.createStringColumn("Service Url", BaseTestCase.class,BaseTestCase::getServiceUrl,150);
        var requestRequestParams = JsonUtil.createStringColumn("Request Params", BaseTestCase.class,BaseTestCase::getRequestParams,150);

        ttvContainer.getColumns().addAll(checkBoxColumn,microserviceColumn,moduleColumn,idColumn,titleColumn,descriptionColumn,requestTypeColumn,requestServiceUrl,requestRequestParams);

        for (var column : ttvContainer.getColumns()) {
//            ((TreeTableColumn<Object,String>) column).setPrefWidth(150);
        }

        // Add double-click event handler
        ttvContainer.setOnMouseClicked(event -> {
            if (event.getClickCount() == 2) {
                var selectedItem = (TreeItem<Object>) ttvContainer.getSelectionModel().getSelectedItem();
                if (selectedItem != null && selectedItem.getValue() instanceof BaseTestCase) {
                    BaseTestCase baseTestCase = (BaseTestCase) selectedItem.getValue();
                    try {
                        handleDoubleClick(baseTestCase);
                    } catch (JsonProcessingException e) {
                        throw new RuntimeException(e);
                    }
                }
            }
        });
        // Create root item

        // Define columns
//        TreeTableColumn<Object, String> microserviceColumn = new TreeTableColumn<>("Microservice");
//        microserviceColumn.setCellValueFactory(param -> {
//            if (param.getValue().getValue() instanceof Microservice) {
//                return new SimpleStringProperty(((Microservice) param.getValue().getValue()).getName());
//            } else {
//                return new SimpleStringProperty("");
//            }
//        });





        // Populate TreeTableView with data
        TreeItem<Object> rootItem = new TreeItem<>(new Object());
        ttvContainer.setRoot(rootItem);

        // Populate tree items
        for (Root item : rootData) {
            Microservice microservice = item.getMicroservice();
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


        ttvContainer.setRoot(rootItem);

//        ttvContainer.getColumns().add(microserviceColumn);

        ttvContainer.setShowRoot(false);
//        ttvContainer.setShowRoot(true);
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

    private void handleDoubleClick(BaseTestCase baseTestCase) throws JsonProcessingException {
        // Handle the double-click event on a BaseTestCase item
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