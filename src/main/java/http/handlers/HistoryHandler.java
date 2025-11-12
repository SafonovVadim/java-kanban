package http.handlers;

import com.sun.net.httpserver.HttpExchange;
import entities.Endpoint;
import http.BaseHttpHandler;
import managers.InMemoryTaskManager;

import java.io.IOException;

public class HistoryHandler extends BaseHttpHandler {
    private final InMemoryTaskManager taskManager;

    public HistoryHandler(InMemoryTaskManager taskManager) {
        this.taskManager = taskManager;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        Endpoint endpoint = getEndpoint(exchange.getRequestURI().getPath(), exchange.getRequestMethod());
        switch (endpoint) {
            case GET:
                getHistory(exchange);
                break;
            default:
                sendMethodNotAllowed(exchange);
                break;
        }
    }

    private void getHistory(HttpExchange exchange) throws IOException {
        String json = gson.toJson(taskManager.getHistory());
        sendText(exchange, json);
    }
}
