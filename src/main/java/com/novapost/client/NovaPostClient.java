package com.novapost.client;

import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.novapost.model.*;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.List;

public class NovaPostClient {
    public static final String DEFAULT_API_URL = "https://api.novaposhta.ua/v2.0/json/";
    private static final String MODEL_ADDRESS_GENERAL = "AddressGeneral";
    private static final String MODEL_ADDRESS = "Address";

    private final String apiKey;
    private final String apiUrl;
    private final HttpClient httpClient;
    private final ObjectMapper objectMapper;

    public NovaPostClient(String apiKey) {
        this(apiKey, DEFAULT_API_URL, HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(15))
                .build(), createDefaultMapper());
    }

    public NovaPostClient(String apiKey, String apiUrl, HttpClient httpClient, ObjectMapper objectMapper) {
        this.apiKey = apiKey;
        this.apiUrl = apiUrl;
        this.httpClient = httpClient;
        this.objectMapper = objectMapper;
    }

    public static ObjectMapper createDefaultMapper() {
        return new ObjectMapper()
                .registerModule(new Jdk8Module())
                .registerModule(new JavaTimeModule());
    }

    public NpResponse<Settlement> getSettlements(SettlementFilter filter) throws IOException, InterruptedException {
        return execute(MODEL_ADDRESS_GENERAL, "getSettlements", filter, Settlement.class);
    }

    public NpResponse<Warehouse> getWarehouses(WarehouseFilter filter) throws IOException, InterruptedException {
        return execute(MODEL_ADDRESS_GENERAL, "getWarehouses", filter, Warehouse.class);
    }

    public NpResponse<SearchSettlementItem> searchSettlements(SearchSettlementFilter filter) throws IOException, InterruptedException {
        return execute(MODEL_ADDRESS, "searchSettlements", filter, SearchSettlementItem.class);
    }

    public NpResponse<SearchSettlementItem> searchSettlements(String cityName, int page, int limit) throws IOException, InterruptedException {
        return searchSettlements(SearchSettlementFilter.of(cityName, page, limit));
    }

    public NpResponse<SettlementStreet> searchSettlementStreets(SettlementStreetFilter filter) throws IOException, InterruptedException {
        return execute(MODEL_ADDRESS, "searchSettlementStreets", filter, SettlementStreet.class);
    }

    public <T, R> NpResponse<R> execute(String modelName, String calledMethod, T properties, Class<R> itemClass)
            throws IOException, InterruptedException {
        NpRequest<T> request = new NpRequest<>(apiKey, modelName, calledMethod, properties);
        String requestJson = objectMapper.writeValueAsString(request);
        JavaType responseType = objectMapper.getTypeFactory()
                .constructParametricType(NpResponse.class, itemClass);

        int maxRetries = 5;
        long backoffMs = 1500;

        for (int attempt = 1; attempt <= maxRetries; attempt++) {
            HttpRequest httpRequest = HttpRequest.newBuilder()
                    .uri(URI.create(apiUrl))
                    .timeout(Duration.ofSeconds(30))
                    .header("Content-Type", "application/json; charset=UTF-8")
                    .header("Accept", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(requestJson))
                    .build();

            HttpResponse<String> response = httpClient.send(httpRequest, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() == 429) {
                if (attempt == maxRetries) {
                    throw new IOException("HTTP 429 rate limit exceeded after " + maxRetries + " attempts");
                }
                System.out.println("Rate limit (HTTP 429) hit for " + calledMethod + ". Backing off for " + backoffMs + "ms (attempt " + attempt + "/" + maxRetries + ")...");
                Thread.sleep(backoffMs);
                backoffMs *= 2;
                continue;
            }

            if (response.statusCode() < 200 || response.statusCode() >= 300) {
                throw new IOException("HTTP error from Nova Post API: status " + response.statusCode() + ", body: " + response.body());
            }

            NpResponse<R> npResponse = objectMapper.readValue(response.body(), responseType);

            boolean isRateLimitError = npResponse.errors() != null && npResponse.errors().stream()
                    .anyMatch(err -> err != null && (err.toLowerCase().contains("to many requests") || err.toLowerCase().contains("too many requests")));

            if (isRateLimitError) {
                if (attempt == maxRetries) {
                    return npResponse;
                }
                System.out.println("Nova Post rate limit [Too many requests] hit for " + calledMethod + ". Backing off for " + backoffMs + "ms (attempt " + attempt + "/" + maxRetries + ")...");
                Thread.sleep(backoffMs);
                backoffMs *= 2;
                continue;
            }

            return npResponse;
        }

        throw new IOException("Failed to execute request after " + maxRetries + " attempts");
    }

    public String getApiKey() {
        return apiKey;
    }

    public String getApiUrl() {
        return apiUrl;
    }

    public ObjectMapper getObjectMapper() {
        return objectMapper;
    }
}
