package oracle.com.c1apiautomation.controllers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import oracle.com.c1apiautomation.model.Vars;
import oracle.com.c1apiautomation.utils.Util;
import oracle.com.c1apiautomation.controls.ExpandableColoredTextField;
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
import java.util.List;
import java.util.Map;

//import static oracle.com.c1apiautomation.uiexecutionengine.RulesProcessor.objectMapper;


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
    private Vars runtimeVars;
    private String input;

    public ApiRequestController(Scene scene, Environment selectedEnvironment, Vars runtimeVars) {
        this.scene = scene;
        this.selectedEnvironment = selectedEnvironment;
        this.runtimeVars = runtimeVars;
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

//            cmbContentType.getItems().addAll(ContentType.TEXT_PLAIN.getValue(), ContentType.APPLICATION_JSON.getValue(), ContentType.APPLICATION_XML.getValue());
            cmbContentType.setItems(ContentType.getAllNames());
            //create colored combo box for cmbMethod
            Util.loadCmbMethod(cmbMethod);

            var auth = FXCollections.observableArrayList("No Auth", "Basic Auth", "Bearer Auth");
            cmbAuth.setItems(auth);
        }
    }

    public void OpenRequestDialog(TestCase testCase) throws IOException {
        var dlg = new Dialog();
        dlg.setResizable(true);
        dlg.setTitle("Test Case Id: " + testCase.getId());
        var fxmlLoader = Util.OpenDialog(dlg, "apirequest-view.fxml", scene);
        var controller = (ApiRequestController) fxmlLoader.getController();
        controller.setRequestModel(testCase);
        controller.setSelectedEnvironment(this.selectedEnvironment);
        controller.runtimeVars = this.runtimeVars;
        dlg.showAndWait();
        if (dlg.getResult() == ButtonType.OK) {

        }

    }

    private void setRequestModel(TestCase testCase) {
        input = testCase.getInput();
        txtToken.setText(testCase.getToken());
        cmbMethod.setValue(testCase.getRequestType());
        var selectedAuth = switch (testCase.getAuthType().toLowerCase()) {
            case "basic" -> "Basic Auth";
            case "bearer" -> "Bearer Auth";
            default -> "No Auth";
        };
        cmbAuth.setValue(selectedAuth);
        updateAuthPanel(selectedAuth);
        cmbContentType.setValue(testCase.getContentType());
        taBody.setText(Util.formatJson(testCase.getPayload()));
//        Util.formatJson(taBody);
//        txtUrl.setText(testCase.getServiceUrl());
        customUrl.setFullText(testCase.getServiceUrl());
        txtUsername.setText(testCase.getUserName());
        txtPwd.setText(testCase.getPassword());

        customUrl.setStyle("-fx-font-weight: 700");
    }

    public void RunRequest(ActionEvent actionEvent) {

        ApiClientFactory clientFactory = new ApiClientFactory();

        var parsedUrl = Util.replaceVarPlaceholder(customUrl.getText(), selectedEnvironment.getVars().getProperties());

        if (cmbAuth.getValue().equals("Bearer Auth")) {
//            var parsedToken = Util.replaceVarPlaceholder(txtToken.getText(),selectedEnvironment.getVars().getProperties());
            var parsedToken = Util.replaceVarPlaceholder(txtToken.getText(), selectedEnvironment.getVars().getProperties());
            parsedToken = Util.replaceVarPlaceholder(txtToken.getText(), runtimeVars.getProperties());
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


            if (!input.isEmpty()) {
                ObjectMapper objectMapper = new ObjectMapper();
                try {
                    JsonNode inputNode = objectMapper.readTree(input);
                    String source = inputNode.get("source").asText();
                    var vars = inputNode.get("vars").asText();
                    if ("body".equalsIgnoreCase(source) && response != null) {
                        runtimeVars.getProperties().put(vars, response.body());
                    }
                } catch (JsonProcessingException e) {
                    System.out.println("Input is not json" + e.getMessage());
                }
            }

            var header = "";
            if (response.statusCode() >= 400) {
                taResponse.setStyle("-fx-text-fill: red");
                header = "\n\n" + getHeadersText(response.headers()) + "\n";

            } else {
                taResponse.setStyle("-fx-text-fill: green");
            }

            var text = "Status: " + response.statusCode() + " "
                    + HttpStatus.getDescription(response.statusCode())
                    + header
                    + " \n\n" + response.body();

            taResponse.setText(Util.formatJson(text));
//            Util.formatJson(taResponse);
            System.out.println("Status Code: " + response.statusCode());
            System.out.println("Response Body: " + response.body());

        } catch (Exception e) {
            var error = e.getMessage() == null ? e.toString() : e.getMessage();
            System.out.println(error);
            taResponse.setText(error);
            taResponse.setStyle("-fx-text-fill: red");
//            throw new RuntimeException(e);
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
