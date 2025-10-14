import entities.Status;
import entities.Task;
import exceptions.ManagerSaveException;
import managers.FileBackedTaskManager;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class FileManagerTest {
    private File tempFile;

    @BeforeEach
    void setUp() throws IOException {
        tempFile = File.createTempFile("tasks", ".csv");
        System.out.println("Создан временный файл: " + tempFile.getAbsolutePath());
    }

    @Test
    void checkEmptyManager() {
        FileBackedTaskManager manager = new FileBackedTaskManager(tempFile);

        FileBackedTaskManager loadedManager = FileBackedTaskManager.loadFromFile(tempFile);

        assertTrue(loadedManager.getAllTasks().isEmpty());
        assertTrue(loadedManager.getAllEpics().isEmpty());
        assertTrue(loadedManager.getAllTasks().isEmpty());
    }

    @Test
    void checkSavingMultipleTasks() {
        FileBackedTaskManager manager = new FileBackedTaskManager(tempFile);

        Task task1 = new Task(1, "Задача 1", "Описание 1", Status.NEW);
        Task task2 = new Task(2, "Задача 2", "Описание 2", Status.DONE);

        manager.createTask(task1);
        manager.createTask(task2);

        FileBackedTaskManager loadedManager = FileBackedTaskManager.loadFromFile(tempFile);

        assertEquals(2, loadedManager.getAllTasks().size());
        assertEquals("Задача 1", loadedManager.getAllTasks().get(0).getTitle());
        assertEquals("Задача 2", loadedManager.getAllTasks().get(1).getTitle());
    }

    @Test
    void checkLoadingMultipleTasks() {
        FileBackedTaskManager manager = new FileBackedTaskManager(tempFile);

        Task task1 = new Task(1, "Задача 1", "Описание 1", Status.NEW);
        Task task2 = new Task(2, "Задача 2", "Описание 2", Status.DONE);

        manager.createTask(task1);
        manager.createTask(task2);

        FileBackedTaskManager loadedManager = FileBackedTaskManager.loadFromFile(tempFile);

        assertEquals(2, loadedManager.getAllTasks().size());
        assertEquals("Задача 1", loadedManager.getAllTasks().get(0).getTitle());
        assertEquals("Задача 2", loadedManager.getAllTasks().get(1).getTitle());
    }
}
