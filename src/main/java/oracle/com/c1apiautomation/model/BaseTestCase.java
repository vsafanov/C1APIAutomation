package oracle.com.c1apiautomation.model;


import com.fasterxml.jackson.annotation.JsonIgnore;

import java.lang.reflect.Field;

public abstract class BaseTestCase extends SelectableBase  {
    //    private final BooleanProperty selected  = new SimpleBooleanProperty();
    private String id;
    private String title;
    private String description;
    private String requestType;
    private String serviceUrl;
    private String requestParams;
    private String expectedResponse;
    private String contentType;
    private String input;
    private boolean skipValidation;
    private String expectedResponseCode;
    private String useAuthentication;
    private String userName;
    private String password;
    private String dbQuery;
    private String expectedDBResult;
    private String tearDownSql;
    private String preReqSql;
    private String release;
    private String type;
    private String token = "{{token}}"; //set the default value, just for runtime, not being stored in json

    public BaseTestCase() {
    }

//    public BaseTestCase(String type) {
//        this.type = type;
//    }
//
//    public String getType() {
//        return type;
//    }
//
//    public void setType(String type) {
//        this.type = type;
//    }

    // Getters and Setters

    @JsonIgnore
    public String getToken() {
        return token;
    }

    @JsonIgnore
    public void setToken(String token) {
        this.token = token;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getRelease() {
        return release;
    }

    public void setRelease(String release) {
        this.release = release;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getRequestType() {
        return requestType;
    }

    public void setRequestType(String requestType) {
        this.requestType = requestType;
    }

    public String getServiceUrl() {
        return serviceUrl;
    }

    public void setServiceUrl(String serviceUrl) {
        this.serviceUrl = serviceUrl;
    }

    public String getPayload() {
        return requestParams;
    }

    public void setPayload(String requestParams) {
        this.requestParams = requestParams;
    }

    public String getExpectedResponse() {
        return expectedResponse;
    }

    public void setExpectedResponse(String expectedResponse) {
        this.expectedResponse = expectedResponse;
    }

    public String getContentType() {
        return contentType;
    }

    public void setContentType(String contentType) {
        this.contentType = contentType;
    }

    public String getInput() {
        return input;
    }

    public void setInput(String input) {
        this.input = input;
    }

    public boolean isSkipValidation() {
        return skipValidation;
    }

    public void setSkipValidation(boolean skipValidation) {
        this.skipValidation = skipValidation;
    }

    public String getExpectedResponseCode() {
        return expectedResponseCode;
    }

    public void setExpectedResponseCode(String expectedResponseCode) {
        this.expectedResponseCode = expectedResponseCode;
    }

    public String getUseAuthentication() {
        return useAuthentication;
    }

    public void setUseAuthentication(String useAuthentication) {
        this.useAuthentication = useAuthentication;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getDbQuery() {
        return dbQuery;
    }

    public void setDbQuery(String dbQuery) {
        this.dbQuery = dbQuery;
    }

    public String getExpectedDBResult() {
        return expectedDBResult;
    }

    public void setExpectedDBResult(String expectedDBResult) {
        this.expectedDBResult = expectedDBResult;
    }

    public String getTearDownSql() {
        return tearDownSql;
    }

    public void setTearDownSql(String tearDownSql) {
        this.tearDownSql = tearDownSql;
    }

    public String getPreReqSql() {
        return preReqSql;
    }

    public void setPreReqSql(String preReqSql) {
        this.preReqSql = preReqSql;
    }

    @Override
    public BaseTestCase clone() {
        BaseTestCase copy = (BaseTestCase) super.clone();
        // Deep copy mutable fields here, if needed
        copy.setSelected(this.isSelected()); // Clone the BooleanProperty value

        return copy;
    }

    public boolean contains(String searchString) {
        if (searchString == null || searchString.isEmpty()) {
            return false;
        }

        searchString = searchString.toLowerCase();

        for (Field field : this.getClass().getDeclaredFields()) {
            if (field.getType().equals(String.class)) {
                field.setAccessible(true); // To access private fields
                try {
                    String value = (String) field.get(this);
                    if (value != null && value.toLowerCase().contains(searchString)) {
                        return true;
                    }
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
            }
        }
        return false;
    }


//    public boolean isSelected() {
//        return selected.get();
//    }
//
//    public BooleanProperty getSelected() {
//        return selected;
//    }
//
//    public void setSelected(Boolean  selected) {
//        this.selected.set(selected);
//    }
}

