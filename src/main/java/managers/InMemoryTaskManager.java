package managers;

import entities.Epic;
import entities.Status;
import entities.SubTask;
import entities.Task;
import exceptions.ManagerSaveException;
import interfaces.HistoryManager;
import interfaces.TaskManager;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

public class InMemoryTaskManager implements TaskManager {
    private int nextId = 1;
    private final Map<Integer, Task> tasks = new HashMap<>();
    private final Map<Integer, Epic> epics = new HashMap<>();
    private final Map<Integer, SubTask> subtasks = new HashMap<>();
    private final HistoryManager historyManager = Managers.getDefaultHistory();
    private final Set<Task> prioritizedTasks = new TreeSet<>(
            Comparator.comparing(
                    Task::getStartTime,
                    Comparator.nullsLast(Comparator.naturalOrder())
            ).thenComparing(Task::getId)
    );
    private final Map<LocalDateTime, Boolean> timeSlots = new HashMap<>();
    private static final int INTERVAL_MINUTES = 15;

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
    public Optional<Task> getTaskById(int id) {
        if (tasks.containsKey(id)) {
            historyManager.add(tasks.get(id));
            return Optional.ofNullable(tasks.get(id));
        } else return Optional.empty();
    }

    @Override
    public void createTask(Task task) {
        task.setId(getNextId());
        Task copy = new Task(task.getId(), task.getTitle(), task.getDescription(), task.getStatus(), task.getDuration(), task.getStartTime());
        if (hasIntersections(copy)) {
            throw new ManagerSaveException("Нельзя создать задачу — она пересекается с другой.");
        }
        historyManager.add(copy);
        prioritizedTasks.add(copy);
        tasks.put(copy.getId(), copy);
    }

    @Override
    public void updateTask(Task updatedTask) {
        if (tasks.containsKey(updatedTask.getId())) {
            if (hasIntersections(updatedTask)) {
                throw new ManagerSaveException("Нельзя обновить задачу — она пересекается с другой.");
            }
            tasks.put(updatedTask.getId(), updatedTask);
            prioritizedTasks.removeIf(t -> t.getId() == updatedTask.getId());
            prioritizedTasks.add(updatedTask);
        }

    }

    @Override
    public void deleteTask(int id) {
        tasks.remove(id);
        prioritizedTasks.removeIf(t -> t.getId() == id);
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
    public Optional<Epic> getEpicById(int id) {
        if (epics.containsKey(id)) {
            historyManager.add(epics.get(id));
            return Optional.ofNullable(epics.get(id));
        } else return Optional.empty();
    }

    @Override
    public void createEpic(Epic epic) {
        epic.setId(getNextId());
        Epic copy = new Epic(epic.getId(), epic.getTitle(), epic.getDescription(), epic.getStatus(), epic.getSubtaskIds(), epic.getDuration(), epic.getStartTime(), epic.getEndTime());
        if (hasIntersections(copy)) {
            throw new ManagerSaveException("Нельзя создать эпик — он пересекается с другой.");
        }
        epics.put(copy.getId(), copy);
        historyManager.add(copy);
        prioritizedTasks.add(copy);
    }

    @Override
    public void updateEpic(Epic updatedEpic) {
        if (epics.containsKey(updatedEpic.getId())) {
            if (hasIntersections(updatedEpic)) {
                throw new ManagerSaveException("Нельзя обновить эпик — он пересекается с другой.");
            }
            epics.put(updatedEpic.getId(), updatedEpic);
            updateStatus(updatedEpic);
            prioritizedTasks.removeIf(t -> t.getId() == updatedEpic.getId());
            prioritizedTasks.add(updatedEpic);
        }
    }

    @Override
    public void deleteEpic(int id) {
        epics.remove(id);
        prioritizedTasks.removeIf(t -> t.getId() == id);
        for (SubTask subtask : getSubtasksByEpicId(id)) {
            if (subtask != null) {
                deleteSubtask(subtask.getId());
                prioritizedTasks.removeIf(t -> t.getId() == subtask.getId());
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
    public Optional<SubTask> getSubtaskById(int id) {
        if (subtasks.containsKey(id)) {
            historyManager.add(subtasks.get(id));
            return Optional.ofNullable(subtasks.get(id));
        } else return Optional.empty();
    }

    @Override
    public void createSubtask(SubTask subtask) {
        subtask.setId(getNextId());
        SubTask copy = new SubTask(subtask.getId(), subtask.getTitle(), subtask.getDescription(), subtask.getStatus(), subtask.getDuration(), subtask.getStartTime(), subtask.getEpicId());
        if (hasIntersections(copy)) {
            throw new ManagerSaveException("Нельзя создать сабтаску — она пересекается с другой.");
        }
        subtasks.put(copy.getId(), copy);
        historyManager.add(copy);
        prioritizedTasks.add(copy);
        updateEpicStatus(copy.getEpicId());
        if (getEpicById(copy.getEpicId()).isPresent()) {
            getEpicById(copy.getEpicId()).get().addSubtaskId(copy);
            getEpicById(copy.getEpicId()).get().updateFromSubtasks(getSubtasksByEpicId(getEpicById(copy.getEpicId()).get().getId()));
        }
    }

    private void updateEpicStatus(int epicId) {
        Optional<Epic> epic = getEpicById(epicId);
        if (epic.isPresent()) {
            updateStatus(epic.get());
            prioritizedTasks.removeIf(t -> t.getId() == epicId);
            prioritizedTasks.add(epic.get());
        }
    }

    @Override
    public void updateSubtask(SubTask updatedSubtask) {
        if (subtasks.containsKey(updatedSubtask.getId())) {
            if (hasIntersections(updatedSubtask)) {
                throw new ManagerSaveException("Нельзя обновить сабтаску — она пересекается с другой.");
            }
            subtasks.put(updatedSubtask.getId(), updatedSubtask);
            updateEpicStatus(updatedSubtask.getEpicId());
            prioritizedTasks.removeIf(t -> t.getId() == updatedSubtask.getId());
            prioritizedTasks.add(updatedSubtask);
            getEpicById(updatedSubtask.getEpicId()).get().addSubtaskId(updatedSubtask);
            getEpicById(updatedSubtask.getEpicId()).get().updateFromSubtasks(getSubtasksByEpicId(getEpicById(updatedSubtask.getEpicId()).get().getId()));
        }
    }

    @Override
    public void deleteSubtask(int id) {
        subtasks.remove(id);
        getAllEpics().stream()
                .filter(epic -> epic.getSubtaskIds().contains(id))
                .forEach(epic -> {
                    List<Integer> subtaskIds = new ArrayList<>(epic.getSubtaskIds());
                    subtaskIds.remove((Integer) id);
                    epic.setSubtaskIds(subtaskIds);
                    prioritizedTasks.removeIf(t -> t.getId() == id);
                    epic.updateFromSubtasks(getSubtasksByEpicId(epic.getId()));
                });
    }

    @Override
    public List<SubTask> getSubtasksByEpicId(int epicId) {
        return getAllSubtasks().stream()
                .filter(subtask -> subtask.getEpicId() == epicId)
                .peek(historyManager::add)
                .toList();
    }

    private void updateStatus(Epic epic) {
        List<SubTask> subtasks = getSubtasksByEpicId(epic.getId());

        if (subtasks.isEmpty()) {
            epic.setStatus(Status.NEW);
            epic.setEndTime(null);
            return;
        }

        boolean hasInProgress = subtasks.stream()
                .anyMatch(subtask -> subtask.getStatus() == Status.IN_PROGRESS);

        if (hasInProgress) {
            epic.setStatus(Status.IN_PROGRESS);
            return;
        }

        boolean allDone = subtasks.stream()
                .allMatch(subtask -> subtask.getStatus() == Status.DONE);

        if (allDone) {
            epic.setStatus(Status.DONE);
        } else {
            epic.setStatus(Status.NEW);
        }

        LocalDateTime latestEndTime = subtasks.stream()
                .map(SubTask::getEndTime)
                .filter(Objects::nonNull)
                .max(LocalDateTime::compareTo)
                .orElse(null);

        epic.setEndTime(latestEndTime);
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

    @Override
    public List<Task> getPrioritizedTasks() {
        return prioritizedTasks.stream()
                .filter(task -> task.getStartTime() != null)
                .sorted(Comparator.comparing(Task::getStartTime))
                .collect(Collectors.toList());
    }

    @Override
    public boolean isOverlapping(Task task1, Task task2) {
        if (task1 == null || task2 == null) return false;

        LocalDateTime start1 = task1.getStartTime();
        LocalDateTime end1 = task1.getEndTime();
        LocalDateTime start2 = task2.getStartTime();
        LocalDateTime end2 = task2.getEndTime();

        if (start1 == null || end1 == null || start2 == null || end2 == null) {
            return false;
        }
        if (end1.equals(start2)) {
            return false;
        }
        return !(end1.isBefore(start2) || end2.isBefore(start1));
    }

    public boolean hasIntersections(Task newTask) {
        List<Task> sortedTasks = getPrioritizedTasks();
        return sortedTasks.stream()
                .anyMatch(task -> !task.equals(newTask) && isOverlapping(newTask, task));
    }
}
