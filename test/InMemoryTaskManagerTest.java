import entities.Epic;
import entities.Status;
import entities.SubTask;
import entities.Task;
import exceptions.ManagerSaveException;
import interfaces.HistoryManager;
import interfaces.TaskManager;
import managers.InMemoryTaskManager;
import managers.Managers;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Method;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;

class InMemoryTaskManagerTest extends TaskManagerTest<InMemoryTaskManager> {
    static Task task = new Task("New task", "java", Status.NEW);
    static Epic epic = new Epic("Переезд", "Планируем переезд в новую квартиру", Status.NEW);
    static Epic epic1 = new Epic("Переезд1", "Планируем переезд в новую квартиру", Status.NEW);
    TaskManager manager = createTaskManager();

    @Override
    protected InMemoryTaskManager createTaskManager() {
        return new InMemoryTaskManager();
    }

    @Test
    void checkSameTasks() {
        manager.createTask(task);
        Assertions.assertEquals(manager.getTaskById(1).get(), task);
    }

    @Test
    void checkSameExtendsTasks() {
        manager.createTask(task);
        manager.createEpic(epic);
        Assertions.assertEquals(manager.getEpicById(2).get(), epic);
    }

    @Test
    void checkEpicAddEpic() {
        epic1.addSubtaskId(epic);
        Assertions.assertEquals(Collections.emptyList(), manager.getSubtasksByEpicId(epic1.getId()));
    }

    @Test
    void checkSubtaskAddSubtask() {
        SubTask subtask1 = new SubTask("Упаковать вещи", "Собрать коробки", Status.NEW, epic);
        assertThrows(IllegalArgumentException.class, () ->
                new SubTask("Заказать машину", "Нанять грузчики", Status.NEW, subtask1));
    }

    @Test
    void checkUtilClass() {
        TaskManager manager1 = Managers.getDefault();
        assertNotNull(manager1);

        HistoryManager history1 = Managers.getDefaultHistory();
        assertNotNull(history1);
    }

    @Test
    void checkInMemoryTaskManagerAddTaskAndSearch() {
        manager.createTask(task);
        manager.createEpic(epic);
        SubTask subtask1 = new SubTask("Упаковать вещи", "Собрать коробки", Status.NEW, epic);
        manager.createSubtask(subtask1);

        assertNotNull(manager.getTaskById(1));
        Assertions.assertEquals(task, manager.getTaskById(1).get());
        assertInstanceOf(Task.class, manager.getTaskById(1).get());

        assertNotNull(manager.getEpicById(2));
        Assertions.assertEquals(epic, manager.getEpicById(2).get());
        assertInstanceOf(Epic.class, manager.getEpicById(2).get());

        assertNotNull(manager.getSubtaskById(3));
        Assertions.assertEquals(subtask1, manager.getSubtaskById(3).get());
        assertInstanceOf(SubTask.class, manager.getSubtaskById(3).get());
    }

    @Test
    void checkConflictId() {
        TaskManager manager1 = new InMemoryTaskManager();

        Task task1 = new Task("Задача 1", "Описание", Status.NEW);
        task1.setId(1);
        manager1.createTask(task1);

        Task task2 = new Task("Задача 2", "Описание", Status.NEW);
        manager1.createTask(task2);

        Assertions.assertNotEquals(task1.getId(), task2.getId());
    }

    @Test
    void checkImmutableTaskAddManger() {
        manager.createTask(task);
        Assertions.assertEquals(task, manager.getTaskById(1).get());
    }

    @Test
    void checkNoIntersection() throws Exception {
        Task task1 = new Task("Задача 1", "Описание", Status.NEW);
        task1.setStartTime(LocalDateTime.of(2025, 4, 1, 10, 0));
        task1.setDuration(Duration.ofMinutes(59));

        Task task2 = new Task("Задача 2", "Описание", Status.NEW);
        task2.setStartTime(LocalDateTime.of(2025, 4, 1, 11, 0));
        task2.setDuration(Duration.ofMinutes(60));

        assertFalse(invokeIsOverlapping(manager, task1, task2));
    }

    @Test
    void checkFullOverlap() throws Exception {
        Task task1 = new Task(1, "Задача 1", "Описание", Status.NEW);
        task1.setStartTime(LocalDateTime.of(2025, 4, 1, 10, 0));
        task1.setDuration(Duration.ofMinutes(59));

        Task task2 = new Task(2, "Задача 2", "Описание", Status.NEW);
        task2.setStartTime(LocalDateTime.of(2025, 4, 1, 10, 0));
        task2.setDuration(Duration.ofMinutes(60));

        assertTrue(invokeIsOverlapping(manager, task1, task2));
    }

    @Test
    void checkPartialOverlap() throws Exception {
        Task task1 = new Task(1, "Задача 1", "Описание", Status.NEW);
        task1.setStartTime(LocalDateTime.of(2025, 4, 1, 10, 0));
        task1.setDuration(Duration.ofMinutes(60));

        Task task2 = new Task(2, "Задача 2", "Описание", Status.NEW);
        task2.setStartTime(LocalDateTime.of(2025, 4, 1, 10, 30));
        task2.setDuration(Duration.ofMinutes(60));

        assertTrue(invokeIsOverlapping(manager, task1, task2));
    }

    @Test
    void checkNoOverlapReverse() throws Exception {
        Task task1 = new Task(1, "Задача 1", "Описание", Status.NEW);
        task1.setStartTime(LocalDateTime.of(2025, 4, 1, 11, 0));
        task1.setDuration(Duration.ofMinutes(60));

        Task task2 = new Task(2, "Задача 2", "Описание", Status.NEW);
        task2.setStartTime(LocalDateTime.of(2025, 4, 1, 10, 0));
        task2.setDuration(Duration.ofMinutes(59));

        assertFalse(invokeIsOverlapping(manager, task1, task2));
    }

    @Test
    void checkEdgeCaseTouching() throws Exception {
        Task task1 = new Task(1, "Задача 1", "Описание", Status.NEW);
        task1.setStartTime(LocalDateTime.of(2025, 4, 1, 10, 0));
        task1.setDuration(Duration.ofMinutes(60));

        Task task2 = new Task(2, "Задача 2", "Описание", Status.NEW);
        task2.setStartTime(LocalDateTime.of(2025, 4, 1, 11, 0));
        task2.setDuration(Duration.ofMinutes(60));
        assertFalse(invokeIsOverlapping(manager, task1, task2));
    }

    @Test
    void checkAddingIntersectingTaskThrowsException() {
        Task task1 = new Task("Задача 1", "Описание", Status.NEW);
        task1.setStartTime(LocalDateTime.of(2025, 4, 1, 10, 0));
        task1.setDuration(Duration.ofMinutes(60));

        Task task2 = new Task("Задача 2", "Описание", Status.NEW);
        task2.setStartTime(LocalDateTime.of(2025, 4, 1, 10, 30));
        task2.setDuration(Duration.ofMinutes(60));

        manager.createTask(task1);

        assertThrows(ManagerSaveException.class, () -> manager.createTask(task2));
    }

    private static boolean invokeIsOverlapping(TaskManager manager, Task task1, Task task2) throws Exception {
        Method method = manager.getClass().getDeclaredMethod("isOverlapping", Task.class, Task.class);
        method.setAccessible(true);
        return (boolean) method.invoke(manager, task1, task2);
    }
}