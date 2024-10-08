package oracle.com.c1apiautomation.uihelpers;

import javafx.scene.control.TreeTableCell;
import javafx.scene.control.TreeItem;
import oracle.com.c1apiautomation.model.BaseTestCase;

public class RequestTypeTreeTableCell extends TreeTableCell<Object, String> {

    @Override
    protected void updateItem(String item, boolean empty) {

        super.updateItem(item, empty);
        if (empty || item == null) {
            setText(null);
            setStyle("");
        } else {
            setText(item);
            // Access the item in the cell directly
            // TreeItem<Object> treeItem = getTreeTableRow().getTreeItem();  !!OBSOLETE
            TreeItem<Object> treeItem = getTreeTableView().getTreeItem(getIndex());
            if (treeItem != null && treeItem.getValue() instanceof BaseTestCase testCase) {
                String style = switch (testCase.getRequestType().toUpperCase()) {
                    case "POST" -> "-fx-text-fill: darkorchid;";
                    case "GET" -> "-fx-text-fill: green;";
                    case "PUT" -> "-fx-text-fill: orange;";
                    case "PATCH" -> "-fx-text-fill: purple;";
                    case "DELETE" -> "-fx-text-fill: coral;";
                    default -> "";
                };
                setStyle(style + "-fx-font-weight: bold;");
            } else {
                setStyle(""); // Clear any previous styles
            }
        }
    }
}

