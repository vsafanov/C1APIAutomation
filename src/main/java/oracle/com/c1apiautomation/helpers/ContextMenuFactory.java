package oracle.com.c1apiautomation.helpers;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeTableView;
import javafx.stage.Stage;
import oracle.com.c1apiautomation.MainApplication;
import oracle.com.c1apiautomation.controllers.FormController;
import oracle.com.c1apiautomation.model.BaseTestCase;
import oracle.com.c1apiautomation.model.Module;
import oracle.com.c1apiautomation.model.PreReq;
import oracle.com.c1apiautomation.model.TestCase;
import java.io.IOException;

public class ContextMenuFactory {

    public static ContextMenu CreateContextMenu  (TreeTableView treeTableView){

        var editMenu = new MenuItem("Edit Record");
        editMenu.setOnAction(actionEvent -> EditRecord(treeTableView));

        var deleteMenu = new MenuItem("Delete Record");
        deleteMenu.setOnAction(actionEvent -> DeleteRecord(treeTableView));

        var expandViewMenu = new MenuItem("Expand All");
        expandViewMenu.setOnAction(actionEvent -> {expandTreeView(treeTableView.getRoot(),true);;treeTableView.refresh();});

        var collapseViewMenu = new MenuItem("Collapse All");
        collapseViewMenu.setOnAction(actionEvent -> {expandTreeView(treeTableView.getRoot(),false);treeTableView.refresh();});

        // instantiate the root context menu
        ContextMenu contextMenu = new ContextMenu();

        contextMenu.getItems().add(0, new MenuItem("Add New"));
        contextMenu.getItems().add(1,editMenu);
        contextMenu.getItems().add(2, deleteMenu);
        contextMenu.getItems().add(3, expandViewMenu);
        contextMenu.getItems().add(4, collapseViewMenu);
        return contextMenu;
    }

    private static void expandTreeView(TreeItem<?> item, Boolean expand){
        if(item != null && !item.isLeaf()){
//            item.setExpanded(expand);
            for(TreeItem<?> child:item.getChildren()){
                expandTreeView(child, expand);
                child.setExpanded(expand);
            }
        }
    }



    private  static void EditRecord(TreeTableView<Object> treeTableView) {
        var selectedItem = (TreeItem<Object>) treeTableView.getSelectionModel().getSelectedItem();
        if (selectedItem != null && selectedItem.getValue() instanceof BaseTestCase) {
            BaseTestCase baseTestCase = (BaseTestCase) selectedItem.getValue();
            try {
                FXMLLoader fxmlLoader = new FXMLLoader(MainApplication.class.getResource("form-view.fxml"));

                Parent parent = fxmlLoader.load();

                Stage stage = new Stage();
                Scene scene = new Scene(parent, 800, 600);
                stage.setTitle("Edit Form");
                scene.getStylesheets().add("/app.css");
                stage.setScene(scene);

                // Get the controller and set the BaseTestCase data
                FormController controller = fxmlLoader.getController();
                controller.setBaseTestCase(baseTestCase, treeTableView, selectedItem, stage);
                stage.show();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    private  static void DeleteRecord(TreeTableView<Object> treeTableView) {
        var selectedItem = (TreeItem<Object>) treeTableView.getSelectionModel().getSelectedItem();
        if (selectedItem != null && selectedItem.getValue() instanceof BaseTestCase)
        {
            BaseTestCase baseTestCase = (BaseTestCase) selectedItem.getValue();
            var parent = selectedItem.getParent();
            if (parent != null) {
                var currentModule = (Module) selectedItem.getParent().getValue();
                if (baseTestCase instanceof PreReq) {
                    currentModule.getPreReqs().remove((PreReq) baseTestCase);
                }
                if (baseTestCase instanceof TestCase) {
                    currentModule.getTestCases().remove((TestCase) baseTestCase);
                }

//                selectedItem.getParent().getChildren().remove(selectedItem);
                selectedItem.getParent().getChildren().removeIf(item -> item.getValue().equals(baseTestCase));
//                System.out.println(currentModule);

                // Optionally expand the module item to show the new PreReq
                parent.setExpanded(true);
                treeTableView.refresh();
            }
        }
    }
}
