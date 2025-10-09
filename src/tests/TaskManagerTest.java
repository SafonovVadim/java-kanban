package tests;

import entities.Epic;
import entities.Status;
import entities.SubTask;
import entities.Task;
import interfaces.HistoryManager;
import interfaces.TaskManager;
import managers.InMemoryHistoryManager;
import managers.InMemoryTaskManager;
import managers.Managers;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class TaskManagerTest {
    static TaskManager manager = Managers.getDefault();
    static Task task = new Task("New task", "test", Status.NEW);
    static Epic epic = new Epic("Переезд", "Планируем переезд в новую квартиру", Status.NEW);
    static Epic epic1 = new Epic("Переезд1", "Планируем переезд в новую квартиру", Status.NEW);


    @BeforeAll
    static void setUp() {
        manager.createTask(task);
        manager.createEpic(epic);
        manager.createEpic(epic1);
    }

    @Test
    void checkSameTasks() {
        Assertions.assertEquals(manager.getTaskById(1), task);
    }

    @Test
    void checkSameExtendsTasks() {
        Assertions.assertEquals(manager.getEpicById(2), epic);
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
        SubTask subtask1 = new SubTask("Упаковать вещи", "Собрать коробки", Status.NEW, epic);
        manager.createSubtask(subtask1);

        assertNotNull(manager.getTaskById(1));
        Assertions.assertEquals(task, manager.getTaskById(1));
        assertInstanceOf(Task.class, manager.getTaskById(1));

        assertNotNull(manager.getEpicById(2));
        Assertions.assertEquals(epic, manager.getEpicById(2));
        assertInstanceOf(Epic.class, manager.getEpicById(2));

        assertNotNull(manager.getSubtaskById(4));
        Assertions.assertEquals(subtask1, manager.getSubtaskById(4));
        assertInstanceOf(SubTask.class, manager.getSubtaskById(4));
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
        Assertions.assertEquals(task, manager.getTaskById(1));
    }

    @Test
    void checkImmutableTaskAddHistoryManager() {
        InMemoryHistoryManager historyManager = new InMemoryHistoryManager();

        Task originalTask = new Task("Исходная задача", "Описание", Status.NEW);
        originalTask.setId(1);

        historyManager.add(originalTask);

        Task updatedTask = new Task("Обновлённая задача", "Новое описание", Status.IN_PROGRESS);
        updatedTask.setId(1);

        historyManager.add(updatedTask);
        Task taskFromHistory = historyManager.getHistory().get(0);

        assertNotNull(taskFromHistory);
        Assertions.assertEquals("Исходная задача", taskFromHistory.getTitle());
        Assertions.assertEquals("Описание", taskFromHistory.getDescription());
        assertNotEquals(updatedTask, taskFromHistory);
    }

    @Test
    public void checkRemoveTaskFromHistory() {
        InMemoryHistoryManager manager = new InMemoryHistoryManager();

        Task task1 = new Task("Задача 1", "Описание", Status.NEW);
        task1.setId(1);
        Task task2 = new Task("Задача 2", "Описание", Status.NEW);
        task2.setId(2);

        manager.add(task1);
        manager.add(task2);
        manager.remove(1);

        List<Task> history = manager.getHistory();
        assertEquals(1, history.size());
        assertEquals(task2, history.get(0));
    }

    @Test
    public void checkRemoveNode() {
        InMemoryHistoryManager manager = new InMemoryHistoryManager();

        Task task1 = new Task("Задача 1", "Описание");
        task1.setId(1);
        Task task2 = new Task("Задача 2", "Описание");
        task2.setId(2);

        manager.linkLast(task1);
        manager.linkLast(task2);

        InMemoryHistoryManager.Node node = manager.getHistoryMap().get(1);
        manager.removeNode(node);

        List<Task> history = manager.getHistory();
        assertEquals(1, history.size());
        assertEquals(task2, history.get(0));
    }

    @Test
    public void checkLinkLastAndGetTasks() {
        InMemoryHistoryManager manager = new InMemoryHistoryManager();

        Task task1 = new Task("Задача 1", "Описание");
        task1.setId(1);
        Task task2 = new Task("Задача 2", "Описание");
        task2.setId(2);

        manager.linkLast(task1);
        manager.linkLast(task2);

        List<Task> history = manager.getTasks();
        assertEquals(2, history.size());
        assertEquals(task1, history.get(0));
        assertEquals(task2, history.get(1));
    }

    @Test
    public void checkRemoveById() {
        InMemoryHistoryManager manager = new InMemoryHistoryManager();

        Task task1 = new Task("Задача 1", "Описание");
        task1.setId(1);
        Task task2 = new Task("Задача 2", "Описание");
        task2.setId(2);

        manager.linkLast(task1);
        manager.linkLast(task2);
        manager.remove(1);

        List<Task> history = manager.getTasks();
        assertEquals(1, history.size());
        assertEquals(task2, history.get(0));
    }

    @Test
    public void checkLinkLastAddsTaskToTheEnd() {
        InMemoryHistoryManager manager = new InMemoryHistoryManager();

        Task task1 = new Task("Задача 1", "Описание");
        task1.setId(1);
        Task task2 = new Task("Задача 2", "Описание");
        task2.setId(2);

        manager.linkLast(task1);
        manager.linkLast(task2);

        List<Task> history = manager.getHistory();
        assertEquals(2, history.size());
        assertEquals(task1.getTitle(), history.get(0).getTitle());
        assertEquals(task2.getTitle(), history.get(1).getTitle());
    }

    @Test
    public void checkRemoveSubtaskIdFromEpicWhenSubtaskIsDeleted() {
        InMemoryTaskManager manager = new InMemoryTaskManager();
        Epic epic = new Epic("Эпик", "Описание", Status.NEW);
        manager.createEpic(epic);

        SubTask subtask1 = new SubTask("Подзадача 1", "Описание", Status.NEW, epic);
        SubTask subtask2 = new SubTask("Подзадача 2", "Описание", Status.NEW, epic);

        manager.createSubtask(subtask1);
        manager.createSubtask(subtask2);

        Epic updatedEpic = manager.getEpicById(epic.getId());
        assertEquals(2, updatedEpic.getSubtaskIds().size(), "Эпик должен содержать обе подзадачи");
        manager.deleteSubtask(subtask1.getId());
        updatedEpic = manager.getEpicById(epic.getId());
        assertEquals(1, updatedEpic.getSubtaskIds().size(), "После удаления подзадачи, эпик должен содержать только одну");
        assertFalse(updatedEpic.getSubtaskIds().contains(subtask1.getId()),
                "ID удалённой подзадачи не должен быть в списке эпика");
    }

    @Test
    public void checkOriginalTaskChangeDoesNotAffectManager() {
        InMemoryTaskManager manager = new InMemoryTaskManager();

        Task original = new Task("Задача", "Описание");
        original.setId(1);
        manager.createTask(original);


        original.setStatus(Status.DONE);
        Task fromManager = manager.getTaskById(1);
        assertNotEquals(Status.DONE, fromManager.getStatus());
    }
}