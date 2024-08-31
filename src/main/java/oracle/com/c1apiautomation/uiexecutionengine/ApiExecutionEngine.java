package oracle.com.c1apiautomation.uiexecutionengine;

import oracle.com.c1apiautomation.MainApplication;
import oracle.com.c1apiautomation.apifactory.ApiClientFactory;
import oracle.com.c1apiautomation.apifactory.ApiService;
import oracle.com.c1apiautomation.model.BaseTestCase;
import oracle.com.c1apiautomation.model.Environment;
import oracle.com.c1apiautomation.model.Vars;
import oracle.com.c1apiautomation.utils.Util;

import java.net.http.HttpResponse;

public class ApiExecutionEngine {
    private final Vars runtimeVars = MainApplication.getVars();
    ;
    private final Vars globalVars;

    public ApiExecutionEngine(Environment selectedEnvironment) {
        this.globalVars = selectedEnvironment.getVars();
    }

    public TestResponse RunTest(BaseTestCase baseTestCase) {
        var testResponse = new TestResponse();

        var globalVarsProperties = globalVars.getProperties();
        var runtimeVarsProperties = runtimeVars.getProperties();

        ApiClientFactory clientFactory = new ApiClientFactory();

        //replace placeholder for Url
        var parsedUrl = Util.replaceVarPlaceholder(baseTestCase.getServiceUrl(), runtimeVarsProperties);
        parsedUrl = Util.replaceVarPlaceholder(baseTestCase.getServiceUrl(), globalVarsProperties);
        System.out.println("parsedUrl:" + parsedUrl);

        //replace placeholder for Token
        if (baseTestCase.getUseAuthentication().equalsIgnoreCase("bearer")) {
            var token = runtimeVars.getByKey("token", true) == null ?
                    globalVars.getByKey("token", true) :
                    runtimeVars.getByKey("token", true);
            System.out.println("token: " + token);
            clientFactory.setJwtToken(token);
        }

        //replace placeholder for User/Password
        if (baseTestCase.getUseAuthentication().equalsIgnoreCase("basic")) {
            var userName = baseTestCase.getUserName();
            var password = baseTestCase.getPassword();
            userName = Util.replaceVarPlaceholder(userName, runtimeVarsProperties);
            userName = Util.replaceVarPlaceholder(userName, globalVarsProperties);

            password = Util.replaceVarPlaceholder(password, runtimeVarsProperties);
            password = Util.replaceVarPlaceholder(password, globalVarsProperties);
            System.out.println("basic user credentials: " + userName + "/" + password);
            if (userName.isEmpty() || password.isEmpty()) {
                System.out.println("Warning: basic user credentials are empty! ");
                testResponse.setError("User credentials are empty for basic authentication!");
                return testResponse;
            }
            clientFactory.setBasicAuth(userName, password);
        }

        //replace placeholder for Payload
        var payload = baseTestCase.getPayload();
        payload = Util.replaceVarPlaceholder(payload, runtimeVarsProperties);
        payload = Util.replaceVarPlaceholder(payload, globalVarsProperties);
        System.out.println("Payload: " + payload);

        testResponse.setPayload(payload);

        ApiService apiService = new ApiService(clientFactory);

        HttpResponse<String> response = null;
        try {

            response = switch (baseTestCase.getRequestType()) {
                case "POST" -> apiService.sendPostRequest(parsedUrl, payload, baseTestCase.getContentType());
                case "GET" -> apiService.sendGetRequest(parsedUrl, baseTestCase.getContentType());
                case "PUT" -> apiService.sendPutRequest(parsedUrl, payload);
                case "DELETE" -> apiService.sendDeleteRequest(parsedUrl, baseTestCase.getContentType());
                default -> throw new UnsupportedOperationException(baseTestCase.getRequestType());
            };

//            runtimeVars.setProperty("body", response.body());
            RulesProcessor proc = new RulesProcessor(globalVars, runtimeVars);
            var result = proc.Run(baseTestCase, testResponse, response);
            System.out.println(result);

            return result;
            //Call outside parser/business rules processor
//            testResponse.setReturnedResponse(response != null ? response.body() : null);
//            testResponse.setReturnedCode(String.valueOf(response != null ? response.statusCode() : null));

//            var header = "\n\n" + getHeadersText(response.headers()) + "\n";
//
//            var text = "Status: " + response.statusCode() + " "
//                    + HttpStatus.getDescription(response.statusCode())
//                    + header
//                    + " \n\n" + response.body();

//            System.out.println("Response : " + text);

        } catch (Exception e) {
            var error = e.getMessage() == null ? e.toString() : e.getMessage();
            System.out.println(error);
            testResponse.setError(e.getMessage());
            return testResponse;
//            throw new RuntimeException(e);
        }
    }

}
