package http;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.sun.net.httpserver.HttpExchange;
import entities.Endpoint;
import http.adapters.DurationAdapter;
import http.adapters.LocalDateTimeAdapter;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.LocalDateTime;

public class BaseHttpHandler {

    protected void sendText(HttpExchange exchange, String message) throws IOException {
        byte[] resp = message.getBytes(StandardCharsets.UTF_8);
        exchange.getResponseHeaders().add("Content-Type", "application/json;charset=utf-8");
        exchange.sendResponseHeaders(200, resp.length);
        exchange.getResponseBody().write(resp);
        exchange.getResponseBody().close();
    }

    protected void sendNotFound(HttpExchange exchange) throws IOException {
        String resp = "Сущность не найдена";
        byte[] bytes = resp.getBytes(StandardCharsets.UTF_8);
        exchange.sendResponseHeaders(404, bytes.length);
        exchange.getResponseBody().write(bytes);
        exchange.getResponseBody().close();
    }

    protected void sendHasOverlaps(HttpExchange exchange, String message) throws IOException {
        byte[] bytes = message.getBytes(StandardCharsets.UTF_8);
        exchange.sendResponseHeaders(406, bytes.length);
        exchange.getResponseBody().write(bytes);
        exchange.getResponseBody().close();
    }

    protected void sendError(HttpExchange exchange, String message) throws IOException {
        byte[] bytes = message.getBytes(StandardCharsets.UTF_8);
        exchange.sendResponseHeaders(500, bytes.length);
        exchange.getResponseBody().write(bytes);
        exchange.getResponseBody().close();
    }

    protected void sendBadRequestError(HttpExchange exchange, String message) throws IOException {
        byte[] bytes = message.getBytes(StandardCharsets.UTF_8);
        exchange.sendResponseHeaders(400, bytes.length);
        exchange.getResponseBody().write(message.getBytes(StandardCharsets.UTF_8));
        exchange.getResponseBody().close();
    }

    protected void sendCreated(HttpExchange exchange) throws IOException {
        exchange.sendResponseHeaders(201, -1);
        exchange.getResponseBody().close();
    }

    protected void sendMethodNotAllowed(HttpExchange exchange) throws IOException {
        exchange.sendResponseHeaders(405, -1);
    }

    public Endpoint getEndpoint(String requestPath, String requestMethod) {
        int size = requestPath.split("/").length;
        if (size == 2 && requestMethod.equals("GET")) {
            return Endpoint.GET;
        } else if (size == 3 && requestMethod.equals("GET")) {
            return Endpoint.GET_BY_ID;
        } else if (size == 4 && requestMethod.equals("GET")) {
            return Endpoint.GET_SUBTASK_BY_EPIC_ID;
        } else if (size == 2 && requestMethod.equals("POST")) {
            return Endpoint.POST;
        } else if (size == 3 && requestMethod.equals("DELETE")) {
            return Endpoint.DELETE;
        }
        return Endpoint.UNKNOWN;
    }

    public static int getId(HttpExchange exchange) {
        if (exchange.getRequestURI().getPath().split("/").length == 3) {
            return Integer.parseInt(exchange.getRequestURI().getPath().split("/")[2]);
        } else {
            return -1;
        }
    }

    public static Gson createGson() {
        return new GsonBuilder()
                .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter())
                .registerTypeAdapter(Duration.class, new DurationAdapter())
                .create();
    }
}
