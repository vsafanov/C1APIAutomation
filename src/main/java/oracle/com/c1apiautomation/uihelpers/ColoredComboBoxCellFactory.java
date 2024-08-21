package oracle.com.c1apiautomation.uihelpers;

import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.util.Callback;

import java.util.HashMap;
import java.util.Map;

public class ColoredComboBoxCellFactory<T> implements Callback<ListView<T>, ListCell<T>> {

    private final Map<T, Color> colorMap;
    private final boolean boldText;

    public ColoredComboBoxCellFactory() {
        this(new HashMap<>(), false);
    }

    public ColoredComboBoxCellFactory(Map<T, Color> colorMap) {
        this(colorMap, false);
    }

    public ColoredComboBoxCellFactory(Map<T, Color> colorMap, boolean boldText) {
        this.colorMap = colorMap;
        this.boldText = boldText;
    }

    @Override
    public ListCell<T> call(ListView<T> param) {
        return new ListCell<>() {
            @Override
            protected void updateItem(T item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                    setGraphic(null);
                } else {
                    Text text = new Text(item.toString());
                    Color color = colorMap.getOrDefault(item, Color.BLACK);
                    text.setFill(color);
                    if (boldText) {
                        text.setFont(Font.font(null, FontWeight.BOLD, text.getFont().getSize()));
                    }
                    setGraphic(text);
                }
            }
        };
    }
}
