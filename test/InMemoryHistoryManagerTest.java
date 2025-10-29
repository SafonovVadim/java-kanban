import entities.Epic;
import entities.Status;
import entities.SubTask;
import entities.Task;
import managers.InMemoryHistoryManager;
import managers.InMemoryTaskManager;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

public class InMemoryHistoryManagerTest {

    @Test
    void checkImmutableTaskAddHistoryManager() {
        InMemoryHistoryManager manager = new InMemoryHistoryManager();

        Task originalTask = new Task("Исходная задача", "Описание", Status.NEW);
        originalTask.setId(1);

        manager.add(originalTask);

        Task updatedTask = new Task("Обновлённая задача", "Новое описание", Status.IN_PROGRESS);
        updatedTask.setId(1);

        manager.add(updatedTask);
        Task taskFromHistory = manager.getHistory().getFirst();

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
        assertEquals(task2, history.getFirst());
    }

    @Test
    public void checkRemoveNode() {
        InMemoryHistoryManager manager = new InMemoryHistoryManager();

        Task task1 = new Task("Задача 1", "Описание");
        task1.setId(1);
        Task task2 = new Task("Задача 2", "Описание");
        task2.setId(2);

        manager.add(task1);
        manager.add(task2);

        manager.remove(1);

        List<Task> history = manager.getHistory();
        assertEquals(1, history.size());
        assertEquals(task2, history.getFirst());
    }

    @Test
    public void checkLinkLastAndGetTasks() {
        InMemoryHistoryManager manager = new InMemoryHistoryManager();

        Task task1 = new Task("Задача 1", "Описание");
        task1.setId(1);
        Task task2 = new Task("Задача 2", "Описание");
        task2.setId(2);

        manager.add(task1);
        manager.add(task2);

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

        manager.add(task1);
        manager.add(task2);
        manager.remove(1);

        List<Task> history = manager.getTasks();
        assertEquals(1, history.size());
        assertEquals(task2, history.getFirst());
    }

    @Test
    public void checkLinkLastAddsTaskToTheEnd() {
        InMemoryHistoryManager manager = new InMemoryHistoryManager();

        Task task1 = new Task("Задача 1", "Описание");
        task1.setId(1);
        Task task2 = new Task("Задача 2", "Описание");
        task2.setId(2);

        manager.add(task1);
        manager.add(task2);

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

        Optional<Epic> updatedEpic = manager.getEpicById(epic.getId());
        assertEquals(2, updatedEpic.get().getSubtaskIds().size(), "Эпик должен содержать обе подзадачи");
        manager.deleteSubtask(subtask1.getId());
        updatedEpic = manager.getEpicById(epic.getId());
        assertEquals(1, updatedEpic.get().getSubtaskIds().size(), "После удаления подзадачи, эпик должен содержать только одну");
        assertFalse(updatedEpic.get().getSubtaskIds().contains(subtask1.getId()),
                "ID удалённой подзадачи не должен быть в списке эпика");
    }

    @Test
    public void checkOriginalTaskChangeDoesNotAffectManager() {
        InMemoryTaskManager manager = new InMemoryTaskManager();

        Task original = new Task("Задача", "Описание");
        original.setId(1);
        manager.createTask(original);

        original.setStatus(Status.DONE);
        Optional<Task> fromManager = manager.getTaskById(1);
        assertNotEquals(Status.DONE, fromManager.get().getStatus());
    }

    @Test
    void checkEpicStatusAllNew() {
        InMemoryTaskManager manager = new InMemoryTaskManager();
        Epic epicTest = new Epic("Эпик", "epic", Status.NEW);
        manager.createEpic(epicTest);
        SubTask subtask1 = new SubTask("sub1", "", Status.NEW, epicTest);
        SubTask subtask2 = new SubTask("sub2", "", Status.NEW, epicTest);

        manager.createEpic(epicTest);
        manager.createSubtask(subtask1);
        manager.createSubtask(subtask2);
        assertEquals(Status.NEW, epicTest.getStatus());
    }

    @Test
    void testEpicStatus_allDone() {
        InMemoryTaskManager manager = new InMemoryTaskManager();
        Epic epicTest = new Epic("Эпик", "epic", Status.NEW);
        manager.createEpic(epicTest);
        SubTask subtask1 = new SubTask("sub1", "", Status.DONE, epicTest);
        SubTask subtask2 = new SubTask("sub2", "", Status.DONE, epicTest);

        manager.createSubtask(subtask1);
        manager.createSubtask(subtask2);
        epicTest.addSubtaskId(subtask1);
        epicTest.addSubtaskId(subtask2);
        manager.updateEpic(epicTest);

        assertEquals(Status.DONE, epicTest.getStatus());
    }

    @Test
    void checkEpicStatusmixedStatuses() {
        InMemoryTaskManager manager = new InMemoryTaskManager();
        Epic epicTest = new Epic("Эпик", "epic", Status.NEW);
        manager.createEpic(epicTest);
        SubTask subtask1 = new SubTask("sub1", "", Status.DONE, epicTest);
        SubTask subtask2 = new SubTask("sub2", "", Status.IN_PROGRESS, epicTest);

        manager.createSubtask(subtask1);
        manager.createSubtask(subtask2);
        epicTest.addSubtaskId(subtask1);
        epicTest.addSubtaskId(subtask2);
        manager.updateEpic(epicTest);

        assertEquals(Status.IN_PROGRESS, epicTest.getStatus());


    }

    @Test
    void testEpicStatus_withInProgress() {
        InMemoryTaskManager manager = new InMemoryTaskManager();
        Epic epicTest = new Epic("Эпик", "epic", Status.NEW);
        manager.createEpic(epicTest);
        SubTask subtask1 = new SubTask("sub1", "", Status.NEW, epicTest);
        SubTask subtask2 = new SubTask("sub2", "", Status.IN_PROGRESS, epicTest);
        SubTask subtask3 = new SubTask("sub3", "", Status.DONE, epicTest);

        manager.createSubtask(subtask1);
        manager.createSubtask(subtask2);
        manager.createSubtask(subtask3);
        epicTest.addSubtaskId(subtask1);
        epicTest.addSubtaskId(subtask2);
        epicTest.addSubtaskId(subtask3);
        manager.updateEpic(epicTest);
        assertEquals(Status.IN_PROGRESS, epicTest.getStatus());
    }
}
