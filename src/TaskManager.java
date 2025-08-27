import java.util.ArrayList;
import java.util.HashMap;

public class TaskManager {
    private int nextId = 1;
    private final HashMap<Integer, Task> tasks = new HashMap<>();
    private final HashMap<Integer, Epic> epics = new HashMap<>();
    private final HashMap<Integer, SubTask> subtasks = new HashMap<>();

    private int getNextId() {
        return nextId++;
    }

    public ArrayList<Task> getAllTasks() {
        return new ArrayList<>(tasks.values());
    }

    public void deleteAllTasks() {
        tasks.clear();
    }

    public Task getTaskById(int id) {
        return tasks.get(id);
    }

    public void createTask(Task task) {
        task.setId(getNextId());
        tasks.put(task.getId(), task);
    }

    public void updateTask(Task updatedTask) {
        if (tasks.containsKey(updatedTask.getId())) {
            tasks.put(updatedTask.getId(), updatedTask);
        }
    }

    public void deleteTask(int id) {
        tasks.remove(id);
    }

    public ArrayList<Epic> getAllEpics() {
        return new ArrayList<>(epics.values());
    }

    public void deleteAllEpics() {
        epics.clear();
        subtasks.clear(); //добавил отчистку сабтасок
    }

    public Epic getEpicById(int id) {
        return epics.get(id);
    }

    public void createEpic(Epic epic) {
        epic.setId(getNextId());
        epics.put(epic.getId(), epic);
    }

    public void updateEpic(Epic updatedEpic) {
        if (epics.containsKey(updatedEpic.getId())) {
            epics.put(updatedEpic.getId(), updatedEpic);
        }
    }

    public void deleteEpic(int id) {
        epics.remove(id);
        for (SubTask subtask : getSubtasksByEpicId(id)) {
            if (subtask != null) {
                deleteSubtask(subtask.getId());
            }
        }//добавил удаление сабтасок
    }

    public ArrayList<SubTask> getAllSubtasks() {
        return new ArrayList<>(subtasks.values());
    }

    public void deleteAllSubtasks() {
        subtasks.clear();
        for (Epic epic : getAllEpics()) {
            updateEpic(epic); //добавил обновление статуса эпика
        }
    }

    public SubTask getSubtaskById(int id) {
        return subtasks.get(id);
    }

    public void createSubtask(SubTask subtask) {
        subtask.setId(getNextId());
        subtasks.put(subtask.getId(), subtask);
        updateEpicStatus(subtask.getEpicId());
        getEpicById(subtask.getEpicId()).addSubtaskId(subtask.getId());
        //добавил обновление статуса эпика
    }

    public void updateSubtask(SubTask updatedSubtask) {
        if (subtasks.containsKey(updatedSubtask.getId())) {
            subtasks.put(updatedSubtask.getId(), updatedSubtask);
            updateEpicStatus(updatedSubtask.getEpicId()); //добавил обновление статуса эпика
        }
    }

    public void deleteSubtask(int id) {
        subtasks.remove(id);
    }

    public ArrayList<SubTask> getSubtasksByEpicId(int epicId) {
        ArrayList<SubTask> result = new ArrayList<>();
        for (SubTask subtask : getAllSubtasks()) {
            if (subtask.getEpicId() == epicId) {
                result.add(subtask);
            }
        }
        return result;
    }

    // Обновление статуса эпика при изменении подзадачи
    private void updateEpicStatus(int epicId) {
        Epic epic = getEpicById(epicId);
        if (epic != null) {
            updateStatus(epic);
        }
    }

    public void updateStatus(Epic epic) {

        boolean allDone = true;

        for (SubTask subtask : getSubtasksByEpicId(epic.getId())) {
            if (subtask == null) continue; // если подзадача удалена
            if (subtask.getStatus() != Status.DONE) {
                allDone = false;
            }
            if (subtask.getStatus() == Status.IN_PROGRESS) {
                epic.setStatus(Status.IN_PROGRESS);// сразу проставляется в процессе
                return;
            } else {
                epic.setStatus(Status.NEW);
            }
        }
        if (allDone) {
            epic.setStatus(Status.DONE);
        } else {
            epic.setStatus(Status.NEW);
        }
    }

    // Вывод всех задач
    public void printAllTasks() {
        System.out.println("Список задач:");
        for (Task task : getAllTasks()) {
            System.out.println(task);
        }
        System.out.println();

        System.out.println("Список эпиков:");
        for (Epic epic : getAllEpics()) {
            System.out.println(epic);
        }
        System.out.println();

        System.out.println("Список подзадач:");
        for (SubTask subtask : getAllSubtasks()) {
            System.out.println(subtask);
        }
    }
}
