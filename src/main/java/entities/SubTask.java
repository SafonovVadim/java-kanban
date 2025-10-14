package entities;

import static entities.Type.SUBTASK;

public class SubTask extends Task {
    private final int epicId; //id вместо епика

    public SubTask(String title, String description, Status status, Task epic) {
        super(title, description, status);
        if (!(epic instanceof Epic)) {
            throw new IllegalArgumentException("Task must be an epic");
        } else this.epicId = epic.getId();
    }

    public SubTask(int id, String title, String description, Status status, int epicId) {
        super(id, title, description, status);
        this.epicId = epicId;
    }

    public int getEpicId() {
        return epicId;
    }

    @Override
    public String toString() {
        return "SubTask: " + super.toString();
    }

    public String toString(SubTask subTask) {
        return String.format("%d,%s,%s,%s,%s,%d", subTask.getId(), SUBTASK.name(), subTask.getTitle(), subTask.getStatus(), subTask.getDescription(), epicId);
    }
}
