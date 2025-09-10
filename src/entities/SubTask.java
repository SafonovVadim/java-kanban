package entities;

public class SubTask extends Task {
    private final int epicId; //id вместо епика

    public SubTask(String title, String description, Status status, Task epic) {
        super(title, description, status);
        if (!(epic instanceof Epic)) {
            throw new IllegalArgumentException("Task must be an epic");
        } else this.epicId = epic.getId();
    }

    public int getEpicId() {
        return epicId;
    }

    @Override
    public String toString() {
        return "SubTask: " + super.toString();
    }
}
