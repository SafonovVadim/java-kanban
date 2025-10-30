import entities.Status;
import entities.Task;
import managers.FileBackedTaskManager;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class FileBackedTaskManagerTest extends TaskManagerTest<FileBackedTaskManager> {
    private File tempFile;
    FileBackedTaskManager manager = createTaskManager();

    @Override
    protected FileBackedTaskManager createTaskManager() {
        try {
            tempFile = File.createTempFile("tasks", ".csv");
        } catch (IOException e) {
            e.printStackTrace();
        }
        assert tempFile != null;
        System.out.println("Создан временный файл: " + tempFile.getAbsolutePath());
        return new FileBackedTaskManager(tempFile);
    }

    @Test
    void checkEmptyManager() {
        FileBackedTaskManager loadedManager = FileBackedTaskManager.loadFromFile(tempFile);
        assertTrue(loadedManager.getAllTasks().isEmpty());
        assertTrue(loadedManager.getAllEpics().isEmpty());
        assertTrue(loadedManager.getAllTasks().isEmpty());
    }

    @Test
    void checkSavingMultipleTasks() {
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
