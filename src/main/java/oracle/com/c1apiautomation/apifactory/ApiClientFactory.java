package oracle.com.c1apiautomation.apifactory;

import org.jetbrains.annotations.Nullable;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Base64;

public class ApiClientFactory {

    private String baseUrl;
    private String basicAuth;
    private String jwtToken;

    public void setBaseUrl(String baseUrl) {
        this.baseUrl = baseUrl;
    }

    public void setJwtToken(String jwtToken) {
        this.jwtToken = jwtToken;
    }

    public void setBasicAuth(String username, String password) {
        this.basicAuth = Base64.getEncoder().encodeToString((username + ":" + password).getBytes());;
    }

    // Constructor for no authentication
    public ApiClientFactory() {
        this(null);
    }

    // Constructor for no authentication
    public ApiClientFactory(String baseUrl) {
        this.baseUrl = baseUrl;
    }

    // Constructor for Basic Auth
    public ApiClientFactory(String baseUrl, String username, String password) {
        this.baseUrl = baseUrl;
        this.basicAuth = Base64.getEncoder().encodeToString((username + ":" + password).getBytes());
    }

    // Constructor for JWT Auth
    public ApiClientFactory(String baseUrl, String jwtToken) {
        this.baseUrl = baseUrl;
        this.jwtToken = jwtToken;
    }

    public HttpRequest.Builder createRequest(String endpoint, @Nullable String contentType) {
        HttpRequest.Builder requestBuilder = HttpRequest.newBuilder()
                .uri(URI.create(baseUrl == null?endpoint:baseUrl + endpoint));

        if (contentType != null && !contentType.isEmpty()) {
            requestBuilder.header("Content-Type", contentType);
        } else {
            requestBuilder.header("Content-Type", ContentType.APPLICATION_JSON.getValue());
        }

        if (basicAuth != null) {
            requestBuilder.header("Authorization", "Basic " + basicAuth);
        } else if (jwtToken != null) {
            requestBuilder.header("Authorization", "Bearer " + jwtToken);
        }

        return requestBuilder;
    }

    public HttpResponse<String> sendRequest(HttpRequest request) throws Exception {
        try (HttpClient client = HttpClient.newHttpClient()) {
            return client.send(request, HttpResponse.BodyHandlers.ofString());
        }
    }
}
