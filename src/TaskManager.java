import java.util.ArrayList;

public interface TaskManager {

    ArrayList<Task> getAllTasks();

    void deleteAllTasks();

    Task getTaskById(int id);

    void createTask(Task task);

    void updateTask(Task updatedTask);

    void deleteTask(int id);

    ArrayList<Epic> getAllEpics();

    void deleteAllEpics();

    Epic getEpicById(int id);

    void createEpic(Epic epic);

    void updateEpic(Epic updatedEpic);

    void deleteEpic(int id);

    ArrayList<SubTask> getAllSubtasks();

    void deleteAllSubtasks();

    SubTask getSubtaskById(int id);

    void createSubtask(SubTask subtask);

    void updateSubtask(SubTask updatedSubtask);

    void deleteSubtask(int id);

    ArrayList<SubTask> getSubtasksByEpicId(int epicId);

    void updateStatus(Epic epic);

    void printAllTasks();

}
