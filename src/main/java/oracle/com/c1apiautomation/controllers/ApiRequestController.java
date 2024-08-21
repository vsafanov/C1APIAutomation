package oracle.com.c1apiautomation.controllers;

import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import oracle.com.c1apiautomation.utils.Util;
import oracle.com.c1apiautomation.controls.ExpandableColoredTextField;
import oracle.com.c1apiautomation.uihelpers.ColoredComboBoxCellFactory;
import oracle.com.c1apiautomation.model.Environment;
import oracle.com.c1apiautomation.model.TestCase;
import oracle.com.c1apiautomation.apifactory.ApiClientFactory;
import oracle.com.c1apiautomation.apifactory.ApiService;
import oracle.com.c1apiautomation.apifactory.ContentType;
import oracle.com.c1apiautomation.apifactory.HttpStatus;
import org.fxmisc.richtext.InlineCssTextArea;

import java.io.IOException;
import java.net.http.HttpHeaders;
import java.net.http.HttpResponse;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class ApiRequestController {

    public ComboBox cmbContentType;
    public DialogPane dlgRunApiRequest;
    public ComboBox cmbAuth;
    public TextArea taBody;
    public TextField txtUsername;
    public TextField txtPwd;
    public TextArea txtToken;
    public ComboBox cmbMethod;
    public TextField txtUrl;
    public HBox paneToken;
    public GridPane paneBasicAuth;
    public TextArea taResponse;
    public ExpandableColoredTextField customUrl;
    public InlineCssTextArea taInline;
    private Scene scene;
    private Environment selectedEnvironment;

    public ApiRequestController(Scene scene, Environment selectedEnvironment) {
        this.scene = scene;
        this.selectedEnvironment = selectedEnvironment;
    }

    public ApiRequestController() {

    }

    // Setter method for selectedEnvironment
    public void setSelectedEnvironment(Environment selectedEnvironment) {
        this.selectedEnvironment = selectedEnvironment;
    }

    public void initialize() {

        if (cmbContentType != null) {
//            var text = new Pair<>(ContentType.TEXT_PLAIN.getName(), ContentType.TEXT_PLAIN.getValue());

            cmbContentType.getItems().addAll(ContentType.TEXT_PLAIN.getValue(), ContentType.APPLICATION_JSON.getValue(), ContentType.APPLICATION_XML.getValue());

//            var requestTypes = FXCollections.observableArrayList("POST", "GET", "PUT", "PATCH", "DELETE");

            //create colored combo box for cmbMethod
            HashMap<String, Color> colorMap = new HashMap<>();
            colorMap.put("POST", Color.DARKORCHID);
            colorMap.put("GET", Color.DARKGREEN);
            colorMap.put("PUT", Color.ORANGE);
            colorMap.put("PATCH", Color.PURPLE);
            colorMap.put("DELETE", Color.CORAL);
            cmbMethod.setItems(FXCollections.observableArrayList(colorMap.keySet()));

            cmbMethod.setCellFactory(new ColoredComboBoxCellFactory<>(colorMap, true));
            cmbMethod.setButtonCell(new ListCell<String>() {
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

            var auth = FXCollections.observableArrayList("No Auth", "Basic Auth", "Bearer Auth");
            cmbAuth.setItems(auth);
        }
    }

    public void OpenRequestDialog(TestCase testCase) throws IOException {
        var dlg = new Dialog();
        dlg.setResizable(true);
        dlg.setTitle("Test Case Id: " + testCase.getId());
        var fxmlLoader = Util.OpenDialog(dlg, "apirequest-view.fxml",scene);
        var controller = (ApiRequestController) fxmlLoader.getController();
        controller.setRequestModel(testCase);
        controller.setSelectedEnvironment(this.selectedEnvironment);
        dlg.showAndWait();
        if (dlg.getResult() == ButtonType.OK) {

        }

    }

    private void setRequestModel(TestCase testCase) {
        cmbMethod.setValue(testCase.getRequestType());
        var selectedAuth = switch (testCase.getUseAuthentication().toLowerCase()) {
            case "basic" -> "Basic Auth";
            case "bearer" -> "Bearer Auth";
            default -> "No Auth";
        };
        cmbAuth.setValue(selectedAuth);
        updateAuthPanel(selectedAuth);
        cmbContentType.setValue(testCase.getHeader());
        taBody.setText(testCase.getPayload());
        Util.formatJson(taBody);
//        txtUrl.setText(testCase.getServiceUrl());
        customUrl.setFullText(testCase.getServiceUrl());
        txtUsername.setText(testCase.getUserName());
        txtPwd.setText(testCase.getPassword());

        customUrl.setStyle("-fx-font-weight: 700");
    }

    public void RunRequest(ActionEvent actionEvent) {
        // Using Basic Auth

        ApiClientFactory clientFactory = new ApiClientFactory();
//        clientFactory.setBaseUrl("http://localhost:5185/api");
        selectedEnvironment.getVars().setProperty("test","test");

//        var baseUrl = selectedEnvironment.getVars().getProperties().get("baseUrl");
//        String fullUrl = customUrl.getText().replace("{{baseUrl}}", baseUrl);

        var parsedUrl = Util.replaceVarPlaceholder(customUrl.getText(),selectedEnvironment.getVars().getProperties());

        if (cmbAuth.getValue().equals("Bearer Auth")) {
            var parsedToken = Util.replaceVarPlaceholder(txtToken.getText(),selectedEnvironment.getVars().getProperties());
            clientFactory.setJwtToken(parsedToken);
        }
        if (cmbAuth.getValue().equals("Basic Auth")) {
            if (txtUsername.getText().isEmpty() || txtPwd.getText().isEmpty()) {
                Alert alert = new Alert(Alert.AlertType.WARNING);
                alert.setHeaderText("");
                alert.setContentText("Username and Password are Required");
                alert.showAndWait();
                if (alert.getResult() == ButtonType.OK) {
                    return;
                }
            }
            clientFactory.setBasicAuth(txtUsername.textProperty().getValue(), txtPwd.textProperty().getValue());
        }

        ApiService apiService = new ApiService(clientFactory);

        HttpResponse<String> response = null;
        try {
            //            response = apiService.sendPostRequest("/register", null, ContentType.TEXT_PLAIN.getValue());
            response = switch (cmbMethod.getValue().toString()) {
                case "POST" ->
                        apiService.sendPostRequest(parsedUrl, taBody.getText(), cmbContentType.getValue().toString());
                case "GET" -> apiService.sendGetRequest(parsedUrl, cmbContentType.getValue().toString());
                case "PUT" -> apiService.sendPutRequest(parsedUrl, taBody.getText());
                case "DELETE" -> apiService.sendDeleteRequest(parsedUrl, cmbContentType.getValue().toString());
                default -> response;
            };
            var header = "";
            if (response.statusCode() >= 400) {
                taResponse.setStyle("-fx-text-fill: red");
                header =  "\n\n" + getHeadersText(response.headers()) + "\n";

            } else {
                taResponse.setStyle("-fx-text-fill: green");
            }

            var text = "Status: " + response.statusCode() + " "
                    + HttpStatus.getDescription(response.statusCode())
                    + header
                    + " \n\n" + response.body();

            taResponse.setText(text);
            Util.formatJson(taResponse);
            System.out.println("Status Code: " + response.statusCode());
            System.out.println("Response Body: " + response.body());

        } catch (Exception e) {
            var error = e.getMessage() == null ? e.toString() : e.getMessage();
            System.out.println(error);
            taResponse.setText(error);
            taResponse.setStyle("-fx-text-fill: red");
            throw new RuntimeException(e);
        }

    }

    private String getHeadersText(HttpHeaders headers) {
        StringBuilder headersText = new StringBuilder();

        for (Map.Entry<String, List<String>> entry : headers.map().entrySet()) {
            String headerName = entry.getKey();
            List<String> headerValues = entry.getValue();

            // Join multiple values for the same header
            String headerValue = String.join(", ", headerValues);

            // Append the header name and value to the StringBuilder
            headersText.append(headerName).append(": ").append(headerValue).append("\n");
        }

        return headersText.toString();
    }

    public void onAuthChanged(ActionEvent actionEvent) {
        System.out.println(actionEvent.getSource());
        var value = ((ComboBox) actionEvent.getSource()).getValue();
        updateAuthPanel(value);
    }

    private void updateAuthPanel(Object value) {
        paneBasicAuth.setVisible(false);
        paneToken.setVisible(false);
        if (value.equals("Basic Auth")) {
            paneBasicAuth.setVisible(true);
        }
        if (value.equals("Bearer Auth")) {
            paneToken.setVisible(true);
        }
    }
}
