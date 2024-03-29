package http;

import manager.ManagerSaveException;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class KVTaskClient {
    private final String url;
    private final String apiToken;


    public KVTaskClient(int port) {
        this.url = "http://localhost:" + port + "/";
        this.apiToken = register(url);
    }

    private String register(String url) {
        try {
            HttpClient client = HttpClient.newHttpClient();
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(url + "register"))
                    .GET()
                    .build();
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() != 200)
                throw new ManagerSaveException("Возникло исключение при регистрации на сервере. Status code: "
                        + response.statusCode());
            return response.body();
        } catch (IOException | InterruptedException e) {
            throw new ManagerSaveException("Возникло исключение при регистрации на сервере.");
        }
    }

    public String load(String key) {
        try {
            HttpClient client = HttpClient.newHttpClient();
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(url + "load/" + key + "?API_TOKEN=" + apiToken))
                    .GET()
                    .build();
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() != 200)
                throw new ManagerSaveException("Возникло исключение при загрузке данных с сервера. Status code: "
                        + response.statusCode());
            return response.body();
        } catch (IOException | InterruptedException e) {
            throw new ManagerSaveException("Возникло исключение при загрузке данных с сервера.");
        }
    }

    public void put(String key, String json) {
        try {
            HttpClient client = HttpClient.newHttpClient();
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(url + "save/" + key + "?API_TOKEN=" + apiToken))
                    .POST(HttpRequest.BodyPublishers.ofString(json))
                    .build();

            HttpResponse<Void> response = client.send(request, HttpResponse.BodyHandlers.discarding());
            if (response.statusCode() != 200)
                throw new ManagerSaveException("Возникло исключение при сохранении данных на сервере. Status code: "
                        + response.statusCode());
        } catch (IOException | InterruptedException e) {
            throw new ManagerSaveException("Возникло исключение при сохранении данных на сервере.");
        }
    }

}
