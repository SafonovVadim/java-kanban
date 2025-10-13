package entities;

import java.util.ArrayList;
import java.util.List;

import static entities.Type.EPIC;

public class Epic extends Task {

    private List<Integer> subtaskIds = new ArrayList<>();

    public Epic(String title, String description, Status status) {
        super(title, description, status);
    }

    public Epic(int id, String title, String description, Status status, List<Integer> subtaskIds) {
        super(id, title, description, status);
        this.subtaskIds = subtaskIds;
    }

    public Epic(int id, String title, String description, Status status) {
        super(id, title, description, status);
    }

    public List<Integer> getSubtaskIds() {
        return subtaskIds;
    }

    public void setSubtaskIds(List<Integer> subtasks) {
        subtaskIds = subtasks;
    }

    public void addSubtaskId(Task subtask) {
        if (subtask instanceof SubTask) {
            subtaskIds.add(subtask.getId());
        }
    }

    @Override
    public String toString() {
        return "Epic{" +
                " subtasks.size = " + subtaskIds.size() +
                ", subtasks.ids = " + subtaskIds +
                ", title='" + this.getTitle() + '\'' +
                ", description='" + this.getDescription() + '\'' +
                ", status=" + this.getStatus() +
                '}';
    }

    public String toString(Epic epic) {
        return String.format("%d,%s,%s,%s,%s,", epic.getId(), EPIC.name(), epic.getTitle(), epic.getStatus(), epic.getDescription());
    }
}
