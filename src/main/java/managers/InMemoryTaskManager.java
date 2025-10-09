package managers;

import entities.Epic;
import entities.Status;
import entities.SubTask;
import entities.Task;
import interfaces.HistoryManager;
import interfaces.TaskManager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class InMemoryTaskManager implements TaskManager {
    private int nextId = 1;
    private final Map<Integer, Task> tasks = new HashMap<>();
    private final Map<Integer, Epic> epics = new HashMap<>();
    private final Map<Integer, SubTask> subtasks = new HashMap<>();
    private final HistoryManager historyManager = Managers.getDefaultHistory();

    private int getNextId() {
        return nextId++;
    }

    @Override
    public List<Task> getAllTasks() {
        return new ArrayList<>(tasks.values());
    }

    @Override
    public void deleteAllTasks() {
        tasks.clear();
    }

    @Override
    public Task getTaskById(int id) {
        if (tasks.containsKey(id)) {
            historyManager.add(tasks.get(id));
            return tasks.get(id);
        } else return null;
    }

    @Override
    public void createTask(Task task) {
        task.setId(getNextId());
        Task copy = new Task(task.getId(), task.getTitle(), task.getDescription(), task.getStatus());
        tasks.put(copy.getId(), copy);
        historyManager.add(copy);
        tasks.put(copy.getId(), copy);
    }

    @Override
    public void updateTask(Task updatedTask) {
        if (tasks.containsKey(updatedTask.getId())) {
            tasks.put(updatedTask.getId(), updatedTask);
        }
    }

    @Override
    public void deleteTask(int id) {
        tasks.remove(id);
    }

    @Override
    public List<Epic> getAllEpics() {
        return new ArrayList<>(epics.values());
    }

    @Override
    public void deleteAllEpics() {
        epics.clear();
        subtasks.clear();
    }

    @Override
    public Epic getEpicById(int id) {
        if (epics.containsKey(id)) {
            historyManager.add(epics.get(id));
            return epics.get(id);
        } else return null;
    }

    @Override
    public void createEpic(Epic epic) {
        epic.setId(getNextId());
        Epic copy = new Epic(epic.getId(), epic.getTitle(), epic.getDescription(), epic.getStatus(), epic.getSubtaskIds());
        epics.put(copy.getId(), copy);
        historyManager.add(copy);
    }

    @Override
    public void updateEpic(Epic updatedEpic) {
        if (epics.containsKey(updatedEpic.getId())) {
            epics.put(updatedEpic.getId(), updatedEpic);
        }
    }

    @Override
    public void deleteEpic(int id) {
        epics.remove(id);
        for (SubTask subtask : getSubtasksByEpicId(id)) {
            if (subtask != null) {
                deleteSubtask(subtask.getId());
            }
        }
    }

    @Override
    public List<SubTask> getAllSubtasks() {
        return new ArrayList<>(subtasks.values());
    }

    @Override
    public void deleteAllSubtasks() {
        subtasks.clear();
        for (Epic epic : getAllEpics()) {
            updateEpic(epic);
        }
    }

    @Override
    public SubTask getSubtaskById(int id) {
        if (subtasks.containsKey(id)) {
            historyManager.add(subtasks.get(id));
            return subtasks.get(id);
        } else return null;
    }

    @Override
    public void createSubtask(SubTask subtask) {
        subtask.setId(getNextId());
        SubTask copy = new SubTask(subtask.getId(), subtask.getTitle(), subtask.getDescription(), subtask.getStatus(), subtask.getEpicId());
        subtasks.put(copy.getId(), copy);
        historyManager.add(copy);
        updateEpicStatus(subtask.getEpicId());
        getEpicById(subtask.getEpicId()).addSubtaskId(subtask);
    }

    private void updateEpicStatus(int epicId) {
        Epic epic = getEpicById(epicId);
        if (epic != null) {
            updateStatus(epic);
        }
    }

    @Override
    public void updateSubtask(SubTask updatedSubtask) {
        if (subtasks.containsKey(updatedSubtask.getId())) {
            subtasks.put(updatedSubtask.getId(), updatedSubtask);
            updateEpicStatus(updatedSubtask.getEpicId());
        }
    }

    @Override
    public void deleteSubtask(int id) {
        subtasks.remove(id);
        for (Epic epic : getAllEpics()) {
            if (epic.getSubtaskIds().contains(id)) {
                List<Integer> subtaskIds = epic.getSubtaskIds();
                subtaskIds.remove((Integer) id);
                epic.setSubtaskIds(subtaskIds);
            }
        }
    }

    @Override
    public List<SubTask> getSubtasksByEpicId(int epicId) {
        ArrayList<SubTask> result = new ArrayList<>();
        for (SubTask subtask : getAllSubtasks()) {
            if (subtask.getEpicId() == epicId) {
                historyManager.add(subtask);
                result.add(subtask);
            }
        }
        return result;
    }

    private void updateStatus(Epic epic) {

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

    @Override
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
        System.out.println("История:");
        for (Task task : getHistory()) {
            System.out.println(task);
        }
    }

    @Override
    public List<Task> getHistory() {
        return historyManager.getHistory();
    }
}
