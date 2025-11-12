package http.handlers;

import com.sun.net.httpserver.HttpExchange;
import entities.Endpoint;
import entities.SubTask;
import exceptions.ManagerSaveException;
import http.BaseHttpHandler;
import managers.InMemoryTaskManager;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Optional;

public class SubTaskHandler extends BaseHttpHandler {
    private final InMemoryTaskManager taskManager;

    public SubTaskHandler(InMemoryTaskManager taskManager) {
        this.taskManager = taskManager;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        Endpoint endpoint = getEndpoint(exchange.getRequestURI().getPath(), exchange.getRequestMethod());
        switch (endpoint) {
            case GET:
                getAllSubtasks(exchange);
                break;
            case POST:
                createSubtask(exchange);
                break;
            case GET_BY_ID:
                getSubtaskById(exchange);
                break;
            case DELETE:
                deleteSubtaskById(exchange);
                break;
            default:
                sendMethodNotAllowed(exchange);
                break;
        }
    }

    private void getAllSubtasks(HttpExchange exchange) throws IOException {
        String json = gson.toJson(taskManager.getAllSubtasks());
        sendText(exchange, json);
    }

    private void createSubtask(HttpExchange exchange) throws IOException {
        try {
            String body = new String(exchange.getRequestBody().readAllBytes(), StandardCharsets.UTF_8);
            if (body.isEmpty()) {
                sendError(exchange, "Пустое тело запроса");
                return;
            }

            SubTask task = gson.fromJson(body, SubTask.class);

            if (task == null) {
                sendBadRequestError(exchange, "Ошибка парсинга задачи");
                return;
            }
            if (task.getId() == 0) {
                taskManager.createSubtask(task);
            } else taskManager.updateSubtask(task);
            sendCreated(exchange);
        } catch (ManagerSaveException e) {
            sendHasOverlaps(exchange, e.getMessage());
        } catch (IOException | IllegalArgumentException e) {
            sendError(exchange, "Ошибка создания задачи: " + e.getMessage());
        }
    }

    private void getSubtaskById(HttpExchange exchange) throws IOException {
        int id = getId(exchange);
        Optional<SubTask> task = taskManager.getSubtaskById(id);
        if (task.isPresent()) {
            sendText(exchange, gson.toJson(task.get()));
        } else {
            sendNotFound(exchange);
        }
    }

    private void deleteSubtaskById(HttpExchange exchange) throws IOException {
        int id = getId(exchange);
        Optional<SubTask> task = taskManager.getSubtaskById(id);
        if (task.isPresent()) {
            taskManager.deleteSubtask(id);
            sendText(exchange, gson.toJson(task.get()));
        } else {
            sendNotFound(exchange);
        }
    }
}
