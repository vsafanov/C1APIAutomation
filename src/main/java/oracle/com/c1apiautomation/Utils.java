package oracle.com.c1apiautomation;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import javafx.beans.property.SimpleStringProperty;
import javafx.scene.control.OverrunStyle;
import javafx.scene.control.Tooltip;
import javafx.scene.control.TreeTableCell;
import javafx.scene.control.TreeTableColumn;
//import javafx.scene.control.cell.CheckBoxTreeTableCell;
import javafx.scene.text.Font;
import javafx.util.Callback;
import oracle.com.c1apiautomation.model.Root;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;

public class Utils {

    public static Root readJson(String filePath) throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        return objectMapper.readValue(new File(filePath), new TypeReference<Root>() {
        });
    }

    public static String replacePlaceholders(String text, Map<String, String> placeholders) {
        if (text == null || placeholders == null) {
            return text;
        }

        for (Map.Entry<String, String> entry : placeholders.entrySet()) {
            String key = entry.getKey();
            String value = entry.getValue();
            text = text.replace("{{" + key + "}}", value);
        }

        return text;
    }


    public static <T> TreeTableColumn<Object, String> createStringColumn(String header, Class<T> type, Callback<T, String> propertyGetter, double width) {
        TreeTableColumn<Object, String> column = new TreeTableColumn<>(header);
        column.setPrefWidth(width);
        column.setCellValueFactory(param ->

                (type.isInstance(param.getValue().getValue()) ?
                        new SimpleStringProperty(propertyGetter.call(type.cast(param.getValue().getValue()))) :
                        new SimpleStringProperty("")));

        //setup tooltip for every cell value
        column.setCellFactory(col -> new TreeTableCell<>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                    setTooltip(null);
                } else {
                    setText(item);
                    Tooltip tooltip = new Tooltip(item);
                    tooltip.setMaxWidth(250);
                    tooltip.setWrapText(true);
                    tooltip.setFont(Font.font(tooltip.getFont().getFamily(), 12));
//                    tooltip.setTextOverrun(OverrunStyle.CLIP);
                    setTooltip(tooltip);
                }
            }
        });
        return column;
    }

//    public static <T> TreeTableColumn<Object, Boolean> createBooleanColumn(String header, Class<T> type, Callback<T, Boolean> propertyGetter) {
//        TreeTableColumn<Object, Boolean> column = new TreeTableColumn<>(header);
//        column.setCellFactory(CheckBoxTreeTableCell.forTreeTableColumn(column));
//
//        column.setCellValueFactory(param ->
//                (type.isInstance(param.getValue().getValue()) ?
//                        new SimpleBooleanProperty(propertyGetter.call(type.cast(param.getValue().getValue()))) :
//                        new SimpleBooleanProperty(false)));
//        return column;
//    }
}

