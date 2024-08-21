package oracle.com.c1apiautomation.uihelpers;

import javafx.scene.control.ContentDisplay;
import javafx.scene.control.Tooltip;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeTableCell;
import javafx.scene.image.ImageView;
import javafx.scene.text.Font;
import oracle.com.c1apiautomation.model.PreReq;
import oracle.com.c1apiautomation.model.TestCase;

public class ImageTreeTableCell extends TreeTableCell<Object, String> {
    private final ImageView imageView;

    public ImageTreeTableCell() {
        this.imageView = new ImageView();
        imageView.setFitWidth(16);
        imageView.setFitHeight(16);
        setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
        setStyle("-fx-alignment: center;");

        setGraphic(imageView);
    }

    @Override
    protected void updateItem(String item, boolean empty) {
        super.updateItem(item, empty);

        Tooltip tooltip = new Tooltip(item);
        tooltip.setFont(Font.font(tooltip.getFont().getFamily(), 12));

        if (empty || item == null) {
            imageView.setImage(null);
            setTooltip(null);
        } else {
            TreeItem<Object> treeItem = getTreeTableView().getTreeItem(getIndex());

            if (treeItem.getValue() instanceof PreReq) {
                imageView.setImage(ImageFactory.getImageView("settings1.png").getImage());

                tooltip.setText("PreReq Item");
                setTooltip(tooltip);

            } else if (treeItem.getValue() instanceof TestCase) {
                imageView.setImage(ImageFactory.getImageView("test1.png").getImage());
                tooltip.setText("Test Item");
                setTooltip(tooltip);

            } else {
                imageView.setImage(null);
                setTooltip(null);
            }
        }
    }
}
