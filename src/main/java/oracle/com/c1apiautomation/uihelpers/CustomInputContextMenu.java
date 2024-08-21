package oracle.com.c1apiautomation.uihelpers;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TextInputControl;
import javafx.scene.input.Clipboard;

public class CustomInputContextMenu {

    public static void addMenuItems(TextInputControl textInputControl, MenuItem... customItems) {
        // Create or retrieve the existing context menu
        ContextMenu contextMenu = textInputControl.getContextMenu();
        if (contextMenu == null) {
            contextMenu = new ContextMenu();
            textInputControl.setContextMenu(contextMenu);
        }

        // Create standard menu items
        MenuItem cutItem = new MenuItem("Cut");
        MenuItem copyItem = new MenuItem("Copy");
        MenuItem pasteItem = new MenuItem("Paste");
        MenuItem undoItem = new MenuItem("Undo");
        MenuItem redoItem = new MenuItem("Redo");
        MenuItem selectAllItem = new MenuItem("Select All");

        // Add standard items to context menu
        contextMenu.getItems().addAll(undoItem, redoItem, cutItem, copyItem, pasteItem, selectAllItem);

        // Add custom items to context menu
        contextMenu.getItems().addAll(customItems);

        // Set actions for the menu items
        cutItem.setOnAction(event -> textInputControl.cut());
        copyItem.setOnAction(event -> textInputControl.copy());
        pasteItem.setOnAction(event -> textInputControl.paste());
        undoItem.setOnAction(event -> textInputControl.undo());
        redoItem.setOnAction(event -> textInputControl.redo());
        selectAllItem.setOnAction(event -> textInputControl.selectAll());

        // Update item states dynamically
        textInputControl.setOnContextMenuRequested(event -> {
            boolean hasSelection = !textInputControl.getSelectedText().isEmpty();
            cutItem.setDisable(!hasSelection);
            copyItem.setDisable(!hasSelection);

            Clipboard clipboard = Clipboard.getSystemClipboard();
            pasteItem.setDisable(!clipboard.hasString());

            undoItem.setDisable(!textInputControl.isUndoable());
            redoItem.setDisable(!textInputControl.isRedoable());
        });
    }
}
