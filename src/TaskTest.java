import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;

class TaskTest {
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
        assertEquals(manager.getTaskById(1), task);
    }

    @Test
    void checkSameExtendsTasks() {
        assertEquals(manager.getEpicById(2), epic);
    }

    @Test
    void checkEpicAddEpic() {
        epic1.addSubtaskId(epic);
        assertEquals(Collections.emptyList(), manager.getSubtasksByEpicId(epic1.getId()));
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
        assertEquals(task, manager.getTaskById(1));
        assertInstanceOf(Task.class, manager.getTaskById(1));

        assertNotNull(manager.getEpicById(2));
        assertEquals(epic, manager.getEpicById(2));
        assertInstanceOf(Epic.class, manager.getEpicById(2));

        assertNotNull(manager.getSubtaskById(4));
        assertEquals(subtask1, manager.getSubtaskById(4));
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

        assertNotEquals(task1.getId(), task2.getId());
    }

    @Test
    void checkImmutableTaskAddManger() {
        manager.createTask(task);
        assertEquals(task, manager.getTaskById(1));
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
        assertEquals("Исходная задача", taskFromHistory.getTitle());
        assertEquals("Описание", taskFromHistory.getDescription());
        assertNotEquals(updatedTask, taskFromHistory);
    }

}