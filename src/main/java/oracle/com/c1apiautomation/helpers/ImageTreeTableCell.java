package oracle.com.c1apiautomation.helpers;

import javafx.scene.control.ContentDisplay;
import javafx.scene.control.Tooltip;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeTableCell;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.util.Duration;
import oracle.com.c1apiautomation.MainApplication;
import oracle.com.c1apiautomation.model.PreReq;
import oracle.com.c1apiautomation.model.TestCase;
import javafx.scene.layout.StackPane;

import java.util.Objects;

public class ImageTreeTableCell extends TreeTableCell<Object, String> {
    private final ImageView imageView = new ImageView();
    private final Image setupImage = new Image(Objects.requireNonNull(MainApplication.class.getResourceAsStream("images/settings1.png")));
    private final Image testImage = new Image(Objects.requireNonNull(MainApplication.class.getResourceAsStream("images/test1.png")));
    private final StackPane stackPane = new StackPane();

    public ImageTreeTableCell() {
        imageView.setFitWidth(16);
        imageView.setFitHeight(16);
        setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
        setStyle("-fx-alignment: center;");

//        stackPane.getChildren().add(imageView);
//        stackPane.setStyle("-fx-alignment: center;");
//        setGraphic(stackPane);

        setGraphic(imageView);
    }

    @Override
    protected void updateItem(String item, boolean empty) {
        super.updateItem(item, empty);

        if (empty || item == null) {
            imageView.setImage(null);
            setText(null);
        } else {

            TreeItem<Object> treeItem = getTreeTableView().getTreeItem(getIndex());

            if (treeItem.getValue() instanceof PreReq) {
                imageView.setImage(setupImage);
                setTooltip(new Tooltip("PreReq Item"));
            } else if (treeItem.getValue() instanceof TestCase) {
                imageView.setImage(testImage);
                setTooltip(new Tooltip("Test Case Item"));
            } else {
                imageView.setImage(null);
            }
        }
    }
}
