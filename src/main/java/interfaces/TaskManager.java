package interfaces;

import entities.Epic;
import entities.SubTask;
import entities.Task;

import java.util.List;
import java.util.Optional;

public interface TaskManager {

    List<Task> getAllTasks();

    void deleteAllTasks();

    Optional<Task> getTaskById(int id);

    void createTask(Task task);

    void updateTask(Task updatedTask);

    void deleteTask(int id);

    List<Epic> getAllEpics();

    void deleteAllEpics();

    Optional<Epic> getEpicById(int id);

    void createEpic(Epic epic);

    void updateEpic(Epic updatedEpic);

    void deleteEpic(int id);

    List<SubTask> getAllSubtasks();

    void deleteAllSubtasks();

    Optional<SubTask> getSubtaskById(int id);

    void createSubtask(SubTask subtask);

    void updateSubtask(SubTask updatedSubtask);

    void deleteSubtask(int id);

    List<SubTask> getSubtasksByEpicId(int epicId);

    void printAllTasks();

    List<Task> getHistory();

    List<Task> getPrioritizedTasks();

    boolean isOverlapping(Task task1, Task task2);
}
