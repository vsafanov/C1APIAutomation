package oracle.com.c1apiautomation;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import javafx.beans.property.SimpleStringProperty;
import javafx.scene.control.TreeTableColumn;
//import javafx.scene.control.cell.CheckBoxTreeTableCell;
import javafx.util.Callback;
import oracle.com.c1apiautomation.model.Root;

import java.io.File;
import java.io.IOException;
import java.util.List;

public class Utils {
    public static List<Root> readJson(String filePath) throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        return objectMapper.readValue(new File(filePath), new TypeReference<List<Root>>() {
        });
    }

    public static <T> TreeTableColumn<Object, String> createStringColumn(String header, Class<T> type, Callback<T, String> propertyGetter, double width) {
        TreeTableColumn<Object, String> column = new TreeTableColumn<>(header);
        column.setPrefWidth(width);
        column.setCellValueFactory(param ->

                (type.isInstance(param.getValue().getValue()) ?
                        new SimpleStringProperty(propertyGetter.call(type.cast(param.getValue().getValue()))) :
                        new SimpleStringProperty("")));
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

