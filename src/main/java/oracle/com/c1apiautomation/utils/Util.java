package oracle.com.c1apiautomation.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
//import javafx.scene.control.cell.CheckBoxTreeTableCell;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.util.Callback;
import oracle.com.c1apiautomation.MainApplication;
import oracle.com.c1apiautomation.model.Root;
import oracle.com.c1apiautomation.model.SelectableBase;
import oracle.com.c1apiautomation.uihelpers.ColoredComboBoxCellFactory;

import java.io.*;
import java.util.HashMap;
import java.util.Map;

public class Util {

    public static Root readJson(String filePath) throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        return objectMapper.readValue(new File(filePath), new TypeReference<Root>() {
        });
    }

    public static String formatJson(String input) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            ObjectWriter writer = mapper.writerWithDefaultPrettyPrinter();

            return writer.writeValueAsString(mapper.readValue(input, Object.class));
        } catch (JsonProcessingException e) {
            System.out.println(e.getMessage());
            return input;
        }
    }

    public static String replaceVarPlaceholder(String text, Map<String, String> placeholders) {
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

    public static FXMLLoader OpenDialog(Dialog dlg, String fxmlFile, Scene scene) throws IOException {

        FXMLLoader fxmlLoader = new FXMLLoader(MainApplication.class.getResource(fxmlFile));
        Parent parent = fxmlLoader.load();
        dlg.setDialogPane((DialogPane) parent);

        dlg.getDialogPane().getStylesheets().addAll(scene.getStylesheets());

        return fxmlLoader;
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
                if (empty || item == null || item.isEmpty()) {
                    setText(null);
                    setTooltip(null);
                } else {

                    setText(item);

                    if (this.getTableColumn().getText() != null && !this.getTableColumn().getText().isEmpty()) {
                        if (this.getTableColumn().getText().equalsIgnoreCase("payload")
                                || this.getTableColumn().getText().equalsIgnoreCase("expected response")
                                || this.getTableColumn().getText().equalsIgnoreCase("input vars")
                        ) {
                            item = Util.formatJson(item);
                        }
                    }
                    Tooltip tooltip = new Tooltip(item);
                    tooltip.setMaxWidth(250);
                    tooltip.setWrapText(true);
                    setTooltip(tooltip);
                }
            }
        });
        return column;
    }

    public static void loadCmbMethod(ComboBox comboBox)
    {
        //create colored combo box for cmbMethod
        HashMap<String, Color> colorMap = new HashMap<>();
        colorMap.put("GET", Color.DARKGREEN);
        colorMap.put("POST", Color.DARKORCHID);
        colorMap.put("PUT", Color.ORANGE);
        colorMap.put("PATCH", Color.PURPLE);
        colorMap.put("DELETE", Color.CORAL);
        comboBox.setItems(FXCollections.observableArrayList(colorMap.keySet()));

        comboBox.setCellFactory(new ColoredComboBoxCellFactory<>(colorMap, true));
        comboBox.setButtonCell(new ListCell<String>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                    setGraphic(null);
                } else {
                    Text text = new Text(item);
                    Color color = colorMap.getOrDefault(item, Color.BLACK);
                    text.setFill(color);
                    text.setStyle("-fx-font-weight: bold");
                    setGraphic(text);
                }
            }
        });
    }

    public static void addCheckBoxListener(SelectableBase parent, TreeItem<Object> parentItem) {
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

