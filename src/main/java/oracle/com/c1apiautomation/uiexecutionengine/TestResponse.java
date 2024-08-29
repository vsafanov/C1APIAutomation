package oracle.com.c1apiautomation.uiexecutionengine;

import oracle.com.c1apiautomation.model.Vars;

public class TestResponse {
    private String testCaseId;
    private String testCaseTitle;
    private String expectedCode;
    private String returnedCode;
    private Boolean isCodePassed;
    private String expectedResponse;
    private String returnedResponseBody;
    private Boolean isResponsePassed;
    private Boolean isTestPassed;
    private Vars runtimeVars;
    private String payload;
    private Boolean skipValidation;

    public TestResponse() {
    }


    public String getTestCaseId() {
        return testCaseId;
    }

    public Boolean getSkipValidation() {
        return skipValidation;
    }

    public void setSkipValidation(Boolean skipValidation) {
        skipValidation = skipValidation;
    }


    public void setTestCaseId(String testCaseId) {
        this.testCaseId = testCaseId;
    }

    public String getTestCaseTitle() {
        return testCaseTitle;
    }

    public void setTestCaseTitle(String testCaseTitle) {
        this.testCaseTitle = testCaseTitle;
    }

    public String getExpectedCode() {
        return expectedCode;
    }

    public void setExpectedCode(String expectedCode) {
        this.expectedCode = expectedCode;
    }

    public String getReturnedCode() {
        return returnedCode;
    }

    public void setReturnedCode(String returnedCode) {
        this.returnedCode = returnedCode;
    }

    public Boolean getCodePassed() {
        return isCodePassed;
    }

    public void setCodePassed(Boolean codePassed) {
        isCodePassed = codePassed;
    }

    public String getExpectedResponse() {
        return expectedResponse;
    }

    public void setExpectedResponse(String expectedResponse) {
        this.expectedResponse = expectedResponse;
    }

    public String getReturnedResponseBody() {
        return returnedResponseBody;
    }

    public void setReturnedResponseBody(String returnedResponseBody) {
        this.returnedResponseBody = returnedResponseBody;
    }

    public Boolean getResponsePassed() {
        return isResponsePassed;
    }

    public void setResponsePassed(Boolean responsePassed) {
        isResponsePassed = responsePassed;
    }

    public Boolean getTestPassed() {
        return isTestPassed;
    }

    public void setTestPassed(Boolean testPassed) {
        isTestPassed = testPassed;
    }

    public Vars getRuntimeVars() {
        return runtimeVars;
    }

    public void setRuntimeVars(Vars runtimeVars) {
        this.runtimeVars = runtimeVars;
    }


    public String getPayload() {
        return payload;
    }

    public void setPayload(String payload) {
        this.payload = payload;
    }
}
