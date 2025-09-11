package interfaces;

import entities.Epic;
import entities.SubTask;
import entities.Task;

import java.util.List;

public interface TaskManager {

    List<Task> getAllTasks();

    void deleteAllTasks();

    Task getTaskById(int id);

    void createTask(Task task);

    void updateTask(Task updatedTask);

    void deleteTask(int id);

    List<Epic> getAllEpics();

    void deleteAllEpics();

    Epic getEpicById(int id);

    void createEpic(Epic epic);

    void updateEpic(Epic updatedEpic);

    void deleteEpic(int id);

    List<SubTask> getAllSubtasks();

    void deleteAllSubtasks();

    SubTask getSubtaskById(int id);

    void createSubtask(SubTask subtask);

    void updateSubtask(SubTask updatedSubtask);

    void deleteSubtask(int id);

    List<SubTask> getSubtasksByEpicId(int epicId);

    void printAllTasks();

    List<Task> getHistory();
}
