package oracle.com.c1apiautomation;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.Property;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.scene.control.*;
import javafx.util.Callback;

public class CheckBoxTreeTableCell<S> extends TreeTableCell<S, Boolean> {

    private final CheckBox checkBox;

    public CheckBoxTreeTableCell() {
        this.checkBox = new CheckBox();
        setGraphic(checkBox);
        setContentDisplay(ContentDisplay.GRAPHIC_ONLY);

        checkBox.selectedProperty().addListener((obs, oldVal, newVal) -> {
            TreeTableRow<S> row = getTreeTableRow();
            if (row != null) {
                TreeItem<S> item = row.getTreeItem();
                if (item != null && item.getValue() != null) {
                    Object value = item.getValue();
                    if (value instanceof Microservice) {
                        ((Microservice) value).setSelected(newVal);
                    } else if (value instanceof Module) {
                        ((Module) value).setSelected(newVal);
                    } else if (value instanceof BaseTestCase) {
                        ((BaseTestCase) value).setSelected(newVal);
                    }
                }
            }
        });
    }


    @Override
    protected void updateItem(Boolean item, boolean empty) {
        super.updateItem(item, empty);
        if (empty) {
            setGraphic(null);
        } else {
            setGraphic(checkBox);
            checkBox.setSelected(item != null && item);
        }
    }

    private void checkChildren(TreeItem<S> parent, boolean check) {
        if (parent != null && parent.getChildren() != null) {
            for (TreeItem<S> child : parent.getChildren()) {
                if (child != null) {
                        System.out.println( child.leafProperty().getValue());
                    if(child.getValue() instanceof  Microservice)
                    {
                        ((SimpleBooleanProperty) child.getValue()).set(check);
                    }
                    checkChildren(child, check);
                }
            }
        }
    }

    public static <S> Callback<TreeTableColumn<S, Boolean>, TreeTableCell<S, Boolean>> forTreeTableColumn() {
        return param -> new CheckBoxTreeTableCell<>();
    }
}
