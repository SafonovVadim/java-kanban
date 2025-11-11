package http.handlers;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import entities.Endpoint;
import http.BaseHttpHandler;
import managers.InMemoryTaskManager;

import java.io.IOException;

public class PrioritizedHandler extends BaseHttpHandler implements HttpHandler {
    private final InMemoryTaskManager taskManager;

    public PrioritizedHandler(InMemoryTaskManager taskManager) {
        this.taskManager = taskManager;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        Endpoint endpoint = getEndpoint(exchange.getRequestURI().getPath(), exchange.getRequestMethod());
        switch (endpoint) {
            case GET:
                getPrioritized(exchange);
                break;
            default:
                sendMethodNotAllowed(exchange);
                break;
        }
    }

    private void getPrioritized(HttpExchange exchange) throws IOException {
        String json = createGson().toJson(taskManager.getPrioritizedTasks());
        sendText(exchange, json);
    }
}
