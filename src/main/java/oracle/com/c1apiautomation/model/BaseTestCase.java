package oracle.com.c1apiautomation.model;


public abstract class BaseTestCase extends SelectableBase {
//    private final BooleanProperty selected  = new SimpleBooleanProperty();
    private String id;
    private String title;
    private String description;
    private String requestType;
    private String serviceUrl;
    private String requestParams;
    private String expectedResponse;
    private String header;
    private String input;
    private boolean skipValidation;
    private String expectedResponseCode;
    private boolean useAuthentication;
    private String userName;
    private String password;
    private String dbQuery;
    private String expectedDBResult;
    private String tearDownSql;
    private String preReqSql;
    private String type;

    public BaseTestCase() {

    }

    public BaseTestCase(String type) {
        this.type = type;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    // Getters and Setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
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

    public String getRequestParams() {
        return requestParams;
    }

    public void setRequestParams(String requestParams) {
        this.requestParams = requestParams;
    }

    public String getExpectedResponse() {
        return expectedResponse;
    }

    public void setExpectedResponse(String expectedResponse) {
        this.expectedResponse = expectedResponse;
    }

    public String getHeader() {
        return header;
    }

    public void setHeader(String header) {
        this.header = header;
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

    public boolean isUseAuthentication() {
        return useAuthentication;
    }

    public void setUseAuthentication(boolean useAuthentication) {
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

