package oracle.com.c1apiautomation.testapifactory;

import org.jetbrains.annotations.Nullable;

import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class ApiService {

    private final ApiClientFactory clientFactory;

    public ApiService(ApiClientFactory clientFactory) {
        this.clientFactory = clientFactory;
    }


    public HttpResponse<String> sendGetRequest(String endpoint) throws Exception {
        return sendGetRequest(endpoint, null);
    }

    public HttpResponse<String> sendGetRequest(String endpoint, String contentType) throws Exception {
        HttpRequest request = clientFactory.createRequest(endpoint, contentType).GET().build();
        return clientFactory.sendRequest(request);
    }

    public HttpResponse<String> sendPostRequest(String endpoint, String jsonBody) throws Exception {
       return sendPostRequest(endpoint, jsonBody, null);
    }

    public HttpResponse<String> sendPostRequest(String endpoint, String jsonBody, @Nullable String contentType) throws Exception {
        HttpRequest.BodyPublisher bodyPublisher = (jsonBody == null || jsonBody.isEmpty())
                ? HttpRequest.BodyPublishers.noBody()
                : HttpRequest.BodyPublishers.ofString(jsonBody);

        HttpRequest request = clientFactory.createRequest(endpoint, contentType)
                .POST(bodyPublisher)
                .build();

        return clientFactory.sendRequest(request);
    }

    public HttpResponse<String> sendPutRequest(String endpoint, String jsonBody) throws Exception {
        return sendPutRequest(endpoint, jsonBody, null);
    }

    public HttpResponse<String> sendPutRequest(String endpoint, String jsonBody, @Nullable String contentType) throws Exception {
        HttpRequest request = clientFactory.createRequest(endpoint, contentType)
                .PUT(HttpRequest.BodyPublishers.ofString(jsonBody))
                .build();
        return clientFactory.sendRequest(request);
    }

    public HttpResponse<String> sendDeleteRequest(String endpoint, String contentType) throws Exception {
        HttpRequest request = clientFactory.createRequest(endpoint, contentType).DELETE().build();
        return clientFactory.sendRequest(request);
    }
}

