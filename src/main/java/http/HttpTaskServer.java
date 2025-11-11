package http;

import com.sun.net.httpserver.HttpServer;
import http.handlers.*;
import managers.InMemoryTaskManager;
import managers.Managers;

import java.io.IOException;
import java.net.InetSocketAddress;

public class HttpTaskServer extends Managers {
    private static final int PORT = 8080;
    private static InMemoryTaskManager taskManager;
    private static HttpServer httpServer;


    public HttpTaskServer(InMemoryTaskManager taskManager) throws IOException {
        this.taskManager = taskManager;
        httpServer = HttpServer.create(new InetSocketAddress(PORT), 0);
    }

    public static void main(String[] args) throws IOException {
        new HttpTaskServer(new InMemoryTaskManager()).start();
    }

    public void start() {
        httpServer.createContext("/tasks", new TaskHandler(taskManager));
        httpServer.createContext("/subtasks", new SubTaskHandler(taskManager));
        httpServer.createContext("/epics", new EpicHandler(taskManager));
        httpServer.createContext("/history", new HistoryHandler(taskManager));
        httpServer.createContext("/prioritized", new PrioritizedHandler(taskManager));
        httpServer.start();
        System.out.println("HTTP-сервер запущен на " + PORT + " порту!");
    }

    public void stop() {
        httpServer.stop(0);
    }
}
