package http.handlers;

import entities.Status;
import entities.Task;
import http.HttpTaskServer;
import managers.InMemoryTaskManager;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class PrioritizedHandlerTest {
    InMemoryTaskManager manager = new InMemoryTaskManager();
    HttpTaskServer server = new HttpTaskServer(manager);
    HttpClient client = HttpClient.newHttpClient();

    public PrioritizedHandlerTest() throws IOException {
    }

    @BeforeEach
    public void setUp() throws IOException {
        server.start();
    }

    @AfterEach
    public void tearDown() {
        server.stop();
    }

    @Test
    public void testGetPrioritizedTasksShouldReturn200() throws IOException, InterruptedException {
        Task task = new Task("Task 1", "Desc", Status.NEW,
                java.time.Duration.ofMinutes(10), java.time.LocalDateTime.now());
        manager.createTask(task);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/prioritized"))
                .GET()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode(), "Ожидался статус 200 при получении задач по приоритету");
        assertTrue(response.body().contains("Task 1"), "Список должен включать задачу");
    }

    @Test
    public void testGetPrioritizedTasksNoTasksWithTimeShouldReturn200() throws IOException, InterruptedException {
        Task task = new Task("Task no time", "Desc", null, null, null);
        manager.createTask(task);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/prioritized"))
                .GET()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode(), "Ожидался статус 200");
        assertTrue(response.body().contains("[]"),
                "Ответ может быть пустым, если нет задач с временем");
    }
}