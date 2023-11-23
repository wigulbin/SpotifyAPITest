package org.example;

import com.fasterxml.jackson.core.JsonParser;
import jakarta.ws.rs.client.*;
import jakarta.ws.rs.core.Form;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.io.IOException;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

public class Main {

    public static final URI TOKEN_URI = URI.create("https://accounts.spotify.com/api/token");
    public static final String CLIENT_ID = "";
    public static final String CLIENT_SECRET = "";


    public static void main(String[] args) throws IOException, InterruptedException {
        callSpotifyNoLibrary();
        callSpotifyJAXRS();

    }

    public static void callSpotifyNoLibrary() throws IOException, InterruptedException {
        String auth = Base64.getEncoder().encodeToString((CLIENT_ID + ":" + CLIENT_SECRET).getBytes());

        Map<String, String> parameters = new HashMap<>();
        parameters.put("grant_type", "client_credentials");
        String form = parameters.keySet().stream()
                .map(key -> key + "=" + URLEncoder.encode(parameters.get(key), StandardCharsets.UTF_8))
                .collect(Collectors.joining("&"));

        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(TOKEN_URI)
                .setHeader("Authorization", "Basic " + auth)
                .setHeader("Content-Type", "application/x-www-form-urlencoded")
                .POST(HttpRequest.BodyPublishers.ofString(form))
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        System.out.println(response.body());
    }

    public static void callSpotifyJAXRS() throws IOException, InterruptedException {
        String auth = Base64.getEncoder().encodeToString((CLIENT_ID + ":" + CLIENT_SECRET).getBytes());
        try(Client client = ClientBuilder.newClient();)
        {
            WebTarget target = client.target(TOKEN_URI);

            Form form = new Form();
            form.param("grant_type", "client_credentials");

            Invocation.Builder invocationBuilder = target.request(MediaType.APPLICATION_JSON_TYPE);
            invocationBuilder.header("Authorization", "Basic " + auth);
            String accessTokenJson;
            try (Response response = invocationBuilder.post(Entity.entity(form, MediaType.APPLICATION_FORM_URLENCODED_TYPE))) {
                accessTokenJson = response.readEntity(String.class);
            }

            System.out.println(accessTokenJson);
        }
    }
}