package http.handlers;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import entities.Endpoint;
import entities.Epic;
import exceptions.ManagerSaveException;
import http.BaseHttpHandler;
import managers.InMemoryTaskManager;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Optional;

public class EpicHandler extends BaseHttpHandler implements HttpHandler {
    private final InMemoryTaskManager taskManager;

    public EpicHandler(InMemoryTaskManager taskManager) {
        this.taskManager = taskManager;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        Endpoint endpoint = getEndpoint(exchange.getRequestURI().getPath(), exchange.getRequestMethod());
        switch (endpoint) {
            case GET:
                getAllEpics(exchange);
                break;
            case POST:
                createEpic(exchange);
                break;
            case GET_BY_ID:
                getEpicById(exchange);
                break;
            case GET_SUBTASK_BY_EPIC_ID:
                getSubtaskByEpicId(exchange);
                break;
            case DELETE:
                deleteEpicById(exchange);
                break;
            default:
                sendMethodNotAllowed(exchange);
                break;
        }
    }

    private void getAllEpics(HttpExchange exchange) throws IOException {
        String json = createGson().toJson(taskManager.getAllEpics());
        sendText(exchange, json);
    }

    private void createEpic(HttpExchange exchange) throws IOException {
        try {
            String body = new String(exchange.getRequestBody().readAllBytes(), StandardCharsets.UTF_8);
            if (body.isEmpty()) {
                sendError(exchange, "Пустое тело запроса");
                return;
            }

            Gson gson = createGson();
            Epic task = gson.fromJson(body, Epic.class);

            if (task == null) {
                sendBadRequestError(exchange, "Ошибка парсинга задачи");
                return;
            }
            if (task.getId() == 0) {
                taskManager.createEpic(task);
            } else taskManager.updateEpic(task);
            sendCreated(exchange);
        } catch (ManagerSaveException e) {
            sendHasOverlaps(exchange, e.getMessage());
        } catch (IOException | IllegalArgumentException e) {
            sendError(exchange, "Ошибка создания задачи: " + e.getMessage());
        }
    }

    private void getEpicById(HttpExchange exchange) throws IOException {
        int id = getId(exchange);
        Optional<Epic> task = taskManager.getEpicById(id);
        if (task.isPresent()) {
            sendText(exchange, createGson().toJson(task.get()));
        } else {
            sendNotFound(exchange);
        }
    }

    private void deleteEpicById(HttpExchange exchange) throws IOException {
        int id = getId(exchange);
        Optional<Epic> task = taskManager.getEpicById(id);
        if (task.isPresent()) {
            taskManager.deleteEpic(id);
            sendText(exchange, createGson().toJson(task.get()));
        } else {
            sendNotFound(exchange);
        }
    }

    private void getSubtaskByEpicId(HttpExchange exchange) throws IOException {
        int id = getId(exchange);
        Optional<Epic> task = taskManager.getEpicById(id);
        if (task.isPresent()) {
            taskManager.getSubtasksByEpicId(id);
            sendText(exchange, createGson().toJson(task.get()));
        } else {
            sendNotFound(exchange);
        }
    }
}
