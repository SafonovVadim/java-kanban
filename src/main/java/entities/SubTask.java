package entities;

import java.time.Duration;
import java.time.LocalDateTime;

import static entities.Type.SUBTASK;

public class SubTask extends Task {
    private final int epicId; //id вместо епика
    private Duration duration;
    private LocalDateTime startTime;

    public SubTask(String title, String description, Status status, Task epic) {
        super(title, description, status);
        if (!(epic instanceof Epic)) {
            throw new IllegalArgumentException("Task must be an epic");
        } else this.epicId = epic.getId();
    }

    public SubTask(int id, String title, String description, Status status, Duration duration, LocalDateTime startTime, int epicId) {
        super(id, title, description, status, duration, startTime);
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
        return String.format("%d,%s,%s,%s,%s,%d,%s,%s,%d",
                this.getId(),
                SUBTASK.name(),
                this.getTitle(),
                this.getStatus(),
                this.getDescription(),
                (this.getDuration() != null) ? this.getDuration().toMinutes() : 0,
                (this.getStartTime() != null) ? this.getStartTime() : null,
                getEndTime(),
                this.epicId);
    }
}
