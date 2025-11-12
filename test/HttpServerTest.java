import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import entities.Epic;
import entities.Status;
import entities.SubTask;
import entities.Task;
import http.HttpTaskServer;
import http.adapters.DurationAdapter;
import http.adapters.LocalDateTimeAdapter;
import managers.InMemoryTaskManager;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class HttpServerTest {
    InMemoryTaskManager manager = new InMemoryTaskManager();
    HttpTaskServer taskServer;
    Gson gson = new GsonBuilder()
            .registerTypeAdapter(Duration.class, new DurationAdapter())
            .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter())
            .create();
    HttpClient client = HttpClient.newHttpClient();

    @BeforeEach
    public void setUp() throws IOException {
        taskServer = new HttpTaskServer(manager);
        taskServer.start();
    }

    @AfterEach
    public void shutDown() {
        taskServer.stop();
    }

    // ----------------- TASK TESTS -----------------

    @Test
    public void testCreateTaskShouldReturn201() throws IOException, InterruptedException {
        Task task = new Task("Test Task", "Description", Status.NEW,
                Duration.ofMinutes(30), LocalDateTime.now());

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/tasks"))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(gson.toJson(task)))
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(201, response.statusCode(), "Ожидался статус 201 при создании задачи");
        assertFalse(manager.getAllTasks().isEmpty(), "Задача должна быть добавлена");
    }

    @Test
    public void testCreateTaskWithInvalidJsonShouldReturn400() throws IOException, InterruptedException {
        String invalidJson = "{ \"title\": \"Invalid\", \"duration\": \"abc\" }";

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/tasks"))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(invalidJson))
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(400, response.statusCode(), "Ожидался статус 400 при некорректном JSON");
    }

    @Test
    public void testGetAllTasksShouldReturn200() throws IOException, InterruptedException {
        Task task = new Task("Task 1", "Desc", Status.NEW, Duration.ofMinutes(10), LocalDateTime.now());
        manager.createTask(task);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/tasks"))
                .GET()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode(), "Ожидался статус 200 для получения всех задач");
        assertTrue(response.body().contains("Task 1"), "Тело ответа должно содержать задачу");
    }

    @Test
    public void testGetTaskByIdShouldReturn200() throws IOException, InterruptedException {
        Task task = new Task("Task", "Desc", Status.NEW, Duration.ofMinutes(10), LocalDateTime.now());
        manager.createTask(task);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/tasks/1"))
                .GET()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode(), "Ожидался статус 200 для получения задачи по id");
        assertTrue(response.body().contains("Task"), "Ответ должен содержать данные задачи");
    }

    @Test
    public void testGetTaskByIdNotFoundShouldReturn404() throws IOException, InterruptedException {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/tasks/999"))
                .GET()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(404, response.statusCode(), "Ожидался статус 404 для несуществующей задачи");
    }

    @Test
    public void testDeleteTaskByIdShouldReturn200() throws IOException, InterruptedException {
        Task task = new Task("Task", "Desc", Status.NEW, Duration.ofMinutes(10), LocalDateTime.now());
        manager.createTask(task);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/tasks/1"))
                .DELETE()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode(), "Ожидался статус 200 при удалении задачи");
        assertTrue(manager.getAllTasks().isEmpty(), "Задача должна быть удалена");
    }

    @Test
    public void testDeleteTaskByIdNotFoundShouldReturn404() throws IOException, InterruptedException {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/tasks/999"))
                .DELETE()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(404, response.statusCode(), "Ожидался статус 404 при удалении несуществующей задачи");
    }

    // ----------------- EPIC TESTS -----------------

    @Test
    public void testCreateEpicShouldReturn201() throws IOException, InterruptedException {
        Epic epic = new Epic("Epic 1", "Description", Status.NEW);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/epics"))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(gson.toJson(epic)))
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(201, response.statusCode(), "Ожидался статус 201 при создании эпика");
        assertFalse(manager.getAllEpics().isEmpty(), "Эпик должен быть добавлен");
    }

    @Test
    public void testGetAllEpicsShouldReturn200() throws IOException, InterruptedException {
        Epic epic = new Epic("Epic 1", "Desc", Status.NEW);
        manager.createEpic(epic);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/epics"))
                .GET()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode(), "Ожидался статус 200 для получения всех эпиков");
        assertTrue(response.body().contains("Epic 1"), "Тело ответа должно содержать эпик");
    }

    @Test
    public void testGetEpicByIdShouldReturn200() throws IOException, InterruptedException {
        Epic epic = new Epic("Epic", "Desc", Status.NEW);
        manager.createEpic(epic);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/epics/1"))
                .GET()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode(), "Ожидался статус 200 для получения эпика по id");
        assertTrue(response.body().contains("Epic"), "Ответ должен содержать данные эпика");
    }

    @Test
    public void testGetEpicByIdNotFoundShouldReturn404() throws IOException, InterruptedException {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/epics/999"))
                .GET()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(404, response.statusCode(), "Ожидался статус 404 для несуществующего эпика");
    }

    @Test
    public void testDeleteEpicByIdShouldReturn200() throws IOException, InterruptedException {
        Epic epic = new Epic("Epic", "Desc", Status.NEW);
        manager.createEpic(epic);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/epics/1"))
                .DELETE()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode(), "Ожидался статус 200 при удалении эпика");
        assertTrue(manager.getAllEpics().isEmpty(), "Эпик должен быть удалён");
    }

    @Test
    public void testDeleteEpicByIdNotFoundShouldReturn404() throws IOException, InterruptedException {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/epics/999"))
                .DELETE()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(404, response.statusCode(), "Ожидался статус 404 при удалении несуществующего эпика");
    }

    @Test
    public void testGetSubtasksByEpicIdShouldReturn200() throws IOException, InterruptedException {
        Epic epic = new Epic("Epic 1", "Desc", Status.NEW);
        manager.createEpic(epic);

        SubTask subtask1 = new SubTask("Subtask 1", "Desc", null,
                java.time.Duration.ofMinutes(10), java.time.LocalDateTime.now(), 1);
        manager.createSubtask(subtask1);

        List<SubTask> allSubtasks = manager.getAllSubtasks();
        assertFalse(allSubtasks.isEmpty(), "Подзадача должна быть создана");
        SubTask saved = allSubtasks.getFirst();
        int id = saved.getId();

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/subtasks/" + id))
                .GET()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode(), "Ожидался статус 200 для получения подзадачи по id");
        assertTrue(response.body().contains("Subtask"), "Ответ должен содержать данные подзадачи");
    }

    @Test
    public void testGetSubtasksByEpicIdEpicNotFoundShouldReturn404() throws IOException, InterruptedException {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/epics/999/subtasks"))
                .GET()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(404, response.statusCode(), "Ожидался статус 404 при запросе подзадач несуществующего эпика");
    }

    @Test
    public void testGetSubtasksByEpicIdNoSubtasksShouldReturn404() throws IOException, InterruptedException {
        Epic epic = new Epic("Epic", "Desc", Status.NEW);
        manager.createEpic(epic);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/epics/1/subtasks"))
                .GET()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(404, response.statusCode(), "Ожидался статус 404 даже если подзадач нет");
        assertEquals("Сущность не найдена", response.body().trim(), "Тело ответа должно быть пустым массивом");
    }

    // ----------------- SUBTASK TESTS -----------------

    @Test
    public void testCreateSubtaskShouldReturn201() throws IOException, InterruptedException {
        Epic epic = new Epic("Epic", "Desc", Status.NEW);
        manager.createEpic(epic);

        SubTask subtask = new SubTask("Subtask 1", "Desc", Status.NEW,
                Duration.ofMinutes(15), LocalDateTime.now(), 1);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/subtasks"))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(gson.toJson(subtask)))
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(201, response.statusCode(), "Ожидался статус 201 при создании подзадачи");
        assertFalse(manager.getAllSubtasks().isEmpty(), "Подзадача должна быть добавлена");
    }

    @Test
    public void testCreateSubtaskInvalidEpicIdShouldReturn201() throws IOException, InterruptedException {
        SubTask subtask = new SubTask("Subtask", "Desc", Status.NEW,
                Duration.ofMinutes(10), LocalDateTime.now(), 999);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/subtasks"))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(gson.toJson(subtask)))
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(201, response.statusCode(), "Ожидался статус 201 при создании подзадачи с несуществующим эпиком");
    }

    @Test
    public void testGetAllSubtasksShouldReturn200() throws IOException, InterruptedException {
        Epic epic = new Epic("Epic", "Desc", Status.NEW);
        manager.createEpic(epic);

        SubTask subtask = new SubTask("Subtask", "Desc", Status.NEW,
                Duration.ofMinutes(10), LocalDateTime.now(), 1);
        manager.createSubtask(subtask);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/subtasks"))
                .GET()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode(), "Ожидался статус 200 для получения всех подзадач");
        assertTrue(response.body().contains("Subtask"), "Тело ответа должно содержать подзадачу");
    }

    @Test
    public void testGetSubtaskByIdShouldReturn200() throws IOException, InterruptedException {
        Epic epic = new Epic("Epic", "Desc", Status.NEW);
        manager.createEpic(epic);

        SubTask subtask = new SubTask("Subtask", "Desc", Status.NEW,
                Duration.ofMinutes(10), LocalDateTime.now(), 1);
        manager.createSubtask(subtask);

        List<SubTask> allSubtasks = manager.getAllSubtasks();
        assertFalse(allSubtasks.isEmpty(), "Подзадача должна быть создана");
        SubTask saved = allSubtasks.getFirst();
        int id = saved.getId();

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/subtasks/" + id))
                .GET()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode(), "Ожидался статус 200 для получения подзадачи по id");
        assertTrue(response.body().contains("Subtask"), "Ответ должен содержать данные подзадачи");
    }

    @Test
    public void testGetSubtaskByIdNotFoundShouldReturn404() throws IOException, InterruptedException {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/subtasks/999"))
                .GET()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(404, response.statusCode(), "Ожидался статус 404 для несуществующей подзадачи");
    }

    @Test
    public void testDeleteSubtaskByIdShouldReturn200() throws IOException, InterruptedException {
        Epic epic = new Epic("Epic", "Desc", Status.NEW);
        manager.createEpic(epic);

        SubTask subtask = new SubTask("Subtask", "Desc", Status.NEW,
                Duration.ofMinutes(10), LocalDateTime.now(), 1);
        manager.createSubtask(subtask);

        List<SubTask> allSubtasks = manager.getAllSubtasks();
        assertFalse(allSubtasks.isEmpty(), "Подзадача должна быть создана");
        int id = allSubtasks.getFirst().getId();

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/subtasks/" + id))
                .DELETE()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode(), "Ожидался статус 200 при удалении подзадачи");
        assertTrue(manager.getAllSubtasks().isEmpty(), "Подзадача должна быть удалена");
    }

    @Test
    public void testDeleteSubtaskByIdNotFoundShouldReturn404() throws IOException, InterruptedException {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/subtasks/999"))
                .DELETE()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(404, response.statusCode(), "Ожидался статус 404 при удалении несуществующей подзадачи");
    }

    @Test
    public void testUnsupportedMethodShouldReturn405() throws IOException, InterruptedException {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/tasks/1"))
                .method("PATCH", HttpRequest.BodyPublishers.noBody())
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(405, response.statusCode(), "Ожидался статус 405 для неподдерживаемого метода");
    }
}
