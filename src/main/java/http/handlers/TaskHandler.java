package http.handlers;

import com.sun.net.httpserver.HttpExchange;
import entities.Endpoint;
import entities.Task;
import exceptions.ManagerSaveException;
import http.BaseHttpHandler;
import managers.InMemoryTaskManager;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Optional;

public class TaskHandler extends BaseHttpHandler {
    private final InMemoryTaskManager taskManager;

    public TaskHandler(InMemoryTaskManager taskManager) {
        this.taskManager = taskManager;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        Endpoint endpoint = getEndpoint(exchange.getRequestURI().getPath(), exchange.getRequestMethod());
        switch (endpoint) {
            case GET:
                getAllTasks(exchange);
                break;
            case POST:
                createTaskRs(exchange);
                break;
            case GET_BY_ID:
                getTaskByIdRs(exchange);
                break;
            case DELETE:
                deleteTaskRs(exchange);
                break;
            default:
                sendMethodNotAllowed(exchange);
                break;
        }
    }

    private void getAllTasks(HttpExchange exchange) throws IOException {
        String json = gson.toJson(taskManager.getAllTasks());
        sendText(exchange, json);
    }

    private void createTaskRs(HttpExchange exchange) throws IOException {
        try {
            String body = new String(exchange.getRequestBody().readAllBytes(), StandardCharsets.UTF_8);
            if (body.isEmpty()) {
                sendError(exchange, "Пустое тело запроса");
                return;
            }

            Task task = null;
            try {
                task = gson.fromJson(body, Task.class);
            } catch (Exception e) {
                e.printStackTrace();
            }
            if (task == null) {
                sendBadRequestError(exchange, "Ошибка парсинга задачи");
                return;
            }
            if (task.getId() == 0) {
                taskManager.createTask(task);
            } else taskManager.updateTask(task);
            sendCreated(exchange);
        } catch (ManagerSaveException e) {
            sendHasOverlaps(exchange, e.getMessage());
        } catch (IOException | IllegalArgumentException e) {
            sendError(exchange, "Ошибка создания задачи: " + e.getMessage());
        }
    }

    private void getTaskByIdRs(HttpExchange exchange) throws IOException {
        int id = getId(exchange);
        Optional<Task> task = taskManager.getTaskById(id);
        if (task.isPresent()) {
            sendText(exchange, gson.toJson(task.get()));
        } else {
            sendNotFound(exchange);
        }
    }

    private void deleteTaskRs(HttpExchange exchange) throws IOException {
        int id = getId(exchange);
        Optional<Task> task = taskManager.getTaskById(id);
        if (task.isPresent()) {
            taskManager.deleteTask(id);
            sendText(exchange, gson.toJson(task.get()));
        } else {
            sendNotFound(exchange);
        }
    }
}
