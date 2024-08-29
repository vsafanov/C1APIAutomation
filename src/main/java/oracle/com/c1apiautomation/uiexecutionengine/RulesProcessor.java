package oracle.com.c1apiautomation.uiexecutionengine;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import oracle.com.c1apiautomation.apifactory.HttpStatus;
import oracle.com.c1apiautomation.model.BaseTestCase;
import oracle.com.c1apiautomation.model.Vars;
import oracle.com.c1apiautomation.utils.Util;

import java.net.http.HttpHeaders;
import java.net.http.HttpResponse;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

//Expected Response
// 1.   Expected Response - should get response as is:  text, message, n/a, "" and compare with expected text
//      Input Field -  should have { "{{var}}": "response" } - to assign "response" to variable "var"
//      Input Field -  if you want to assign response as text to variable 'token' - should be  { "{{token}}": "response" }, or just {{token}}
// 2.   Expected Response - should get response as json, if it json type, and compare with coming response
//        Options:
//          {"name":"Alice", "age":30}
//          {"name":"Alice", "age":30, "date":"ignore"}   - the property 'date' should be removed/ignore from cpmparision with  coming response
//      Input Field  - should be in this case to save/store variables:
//          {"{{name}}":"name", "{{age}}":"age"}


public class RulesProcessor {

    //    BaseTestCase baseTestCase;
    Vars globalVars;
    Vars runtimeVars;
    private static final ObjectMapper objectMapper = new ObjectMapper();

    public RulesProcessor(Vars globalVars, Vars runtimeVars) {
//        this.baseTestCase = baseTestCase;
        this.globalVars = globalVars;
        this.runtimeVars = runtimeVars;
    }

    public TestResponse Run(BaseTestCase baseTestCase, TestResponse testResponse, HttpResponse<String> response) throws JsonProcessingException {
//        var testResponse = new TestResponse();
        testResponse.setExpectedCode(baseTestCase.getExpectedResponseCode());
        testResponse.setExpectedResponse(baseTestCase.getExpectedResponse());
        testResponse.setTestCaseId(baseTestCase.getId());
        testResponse.setTestCaseTitle(baseTestCase.getTitle());
        testResponse.setSkipValidation(baseTestCase.isSkipValidation());

        testResponse.setReturnedCode(String.valueOf(response != null ? response.statusCode() : null));
        testResponse.setCodePassed(testResponse.getExpectedCode().equals(testResponse.getReturnedCode()));

        var returnedBody = response != null ? response.body() : null;
        if (returnedBody != null) {
            returnedBody = Util.replaceVarPlaceholder(returnedBody, runtimeVars.getProperties());
            returnedBody = Util.replaceVarPlaceholder(returnedBody, globalVars.getProperties());
        }
        testResponse.setReturnedResponseBody(returnedBody);

        var expectedResponse = baseTestCase.getExpectedResponse();
        if (expectedResponse != null) {
            expectedResponse = Util.replaceVarPlaceholder(expectedResponse, runtimeVars.getProperties());
            expectedResponse = Util.replaceVarPlaceholder(expectedResponse, globalVars.getProperties());
        }
        testResponse.setExpectedResponse(expectedResponse);

        //Compare response body with expectedResponse
        if (isValidJson(testResponse.getExpectedResponse()) && isValidJson(testResponse.getReturnedResponseBody())) {
            testResponse.setResponsePassed(compareJsons(testResponse.getExpectedResponse(), testResponse.getReturnedResponseBody()));
        } else {
            testResponse.setResponsePassed(testResponse.getExpectedResponse().equals(testResponse.getReturnedResponseBody()));
        }

        //Handle Input fields to create variables if needed


//
//        var header = "\n\n" + getHeadersText(response.headers()) + "\n";
//
//        var text = "Status: " + response.statusCode() + " "
//                + HttpStatus.getDescription(response.statusCode())
//                + header
//                + " \n\n" + response.body();

        return testResponse;
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

    public static boolean isValidJson(String jsonString) {
        try {
            // Try to parse the string as a JSON object
            objectMapper.readTree(jsonString);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public Boolean compareJsons(String json1, String json2) throws JsonProcessingException {
//        String json1 = "{\"name\":\"Alice\", \"age\":30, \"date\":\"2023-01-01\", \"ignore\":\"true\"}";
//        String json2 = "{\"name\":\"Alice\", \"age\":30, \"date\":\"2024-01-01\", \"ignore\":\"true\"}";


        JsonNode tree1 = objectMapper.readTree(json1);
        JsonNode tree2 = objectMapper.readTree(json2);

        // Remove properties with the value "ignore" from both JSON nodes
        removePropertiesWithValue(tree1, "ignore");
        removePropertiesWithValue(tree2, "ignore");

        // Use JsonNode.equals() for comparison
        boolean areEqual = tree1.equals(tree2);
        System.out.println("Are the JSONs equal (ignoring properties with value \"ignore\")? " + areEqual);
        return areEqual;
    }

    private static void removePropertiesWithValue(JsonNode node, String value) {
        for (Iterator<Map.Entry<String, JsonNode>> it = node.fields(); it.hasNext(); ) {
            Map.Entry<String, JsonNode> entry = it.next();
            if (entry.getValue().textValue().equals(value)) {
                it.remove();
            } else if (entry.getValue().isObject()) {
                removePropertiesWithValue(entry.getValue(), value);
            }
        }
    }

    public void populateRuntimeVars(String body, String inputJson) throws Exception {
        if (inputJson.isEmpty()){ //|| body.isEmpty()) {
            return;
        }
        // Parse the input JSON to determine the source and vars
        JsonNode inputNode = objectMapper.readTree(inputJson);
        String source = inputNode.get("source").asText();
        String[] vars = inputNode.get("vars").asText().replace(" ", "").split(",");

        // Switch statement based on the value of "from"
        switch (source.toLowerCase()) {
            case "response":
                JsonNode responseNode = objectMapper.readTree(body);
                for (String key : vars) {
                    if (responseNode.has(key)) {
                        String value = responseNode.get(key).asText(null);  // null if the value is missing or null in JSON
                        runtimeVars.getProperties().put(key, value);
                    }
                }
                break;

            case "body":
                // Add logic to handle "body"
                // e.g., parse the body JSON and extract values
                if(vars.length > 0) {
                    runtimeVars.getProperties().put(vars[0], body);
                }
                break;

            case "db":
                // Add logic to handle "db"
                // execute sql query on database and extract values
                String sql = inputNode.get("sql").asText();
                //get json from sql execution
                /*
                {
                    "studyId" : "100",
                    "studyName" : "DesignerAUT38",
                    "tenantId" : "123",
                 }
                 */
//                var json = ExecuteQuery(sql);
//                addVarFromJson(json);
                break;

            case "text":
                // Add logic to handle "text"
                // e.g., process a plain text source and extract values
                break;

            default:
                throw new IllegalArgumentException("Unsupported source: " + source);
        }
    }

    public void addVarFromJson(String jsonString) throws JsonProcessingException {
        // Create ObjectMapper instance
        ObjectMapper objectMapper = new ObjectMapper();

        // Parse the JSON string into a JsonNode
        JsonNode jsonNode = objectMapper.readTree(jsonString);

        // Iterate over the fields in the JSON node
        jsonNode.fields().forEachRemaining(field -> {
            String key = field.getKey();
            String value = field.getValue().asText();

            // Replace the placeholder with the actual value
//            String actualValue = Util.replaceVarPlaceholder(value,runtimeVars.getProperties());
//            String actualValue = value.replace("{{body}}", "my body value");

            // Put the entry into the HashMap
            runtimeVars.setProperty(key, value);
        });
    }

    public void parseJson(String json) throws JsonProcessingException {
//        String json = "{\"person\":{\"firstName\":\"Alice\", \"lastName\":\"Smith\"}, \"address\":{\"city\":\"New York\"}}";


        JsonNode node = objectMapper.readTree(json);

        String firstName = findValue(node, "firstName");
        String city = findValue(node, "city");

        System.out.println("firstName: " + firstName);
        System.out.println("city: " + city);
    }

    private static String findValue(JsonNode node, String propertyName) {
        if (node.has(propertyName)) {
            return node.get(propertyName).asText();
        }

        for (JsonNode child : node) {
            String value = findValue(child, propertyName);
            if (value != null) {
                return value;
            }
        }

        return null;
    }
}



