package http.handlers;

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

public class HistoryHandlerTest {
    InMemoryTaskManager manager = new InMemoryTaskManager();
    HttpTaskServer server = new HttpTaskServer(manager);

    HttpClient client = HttpClient.newHttpClient();

    public HistoryHandlerTest() throws IOException {
    }

    @BeforeEach
    public void setUp() throws InterruptedException {
        server.start();
    }

    @AfterEach
    public void tearDown() {
        server.stop();
    }

    @Test
    public void testGetHistoryShouldReturn200() throws IOException, InterruptedException {
        Task task = new Task("Task 1", "Desc", null,
                java.time.Duration.ofMinutes(10), java.time.LocalDateTime.now());
        manager.createTask(task);
        manager.getTaskById(1);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/history"))
                .GET()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode(), "Ожидался статус 200 при получении истории");
        assertTrue(response.body().contains("Task 1"), "История должна содержать задачу");
    }

    @Test
    public void testGetHistoryEmptyShouldReturn200() throws IOException, InterruptedException {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/history"))
                .GET()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode(), "Ожидался статус 200 даже при пустой истории");
        assertEquals("[]", response.body().trim(), "Тело ответа должно быть пустым массивом");
    }
}