package entities;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static entities.Type.EPIC;

public class Epic extends Task {

    private List<Integer> subtaskIds = new ArrayList<>();
    private LocalDateTime endTime;

    public Epic(String title, String description, Status status) {
        super(title, description, status);
    }

    public Epic(int id, String title, String description, Status status, List<Integer> subtaskIds, Duration duration, LocalDateTime startTime, LocalDateTime endTime) {
        super(id, title, description, status, duration, startTime);
        this.subtaskIds = subtaskIds;
        this.endTime = endTime;
    }

    public Epic(int id, String title, String description, Status status, Duration duration, LocalDateTime startTime, LocalDateTime endTime) {
        super(id, title, description, status, duration, startTime);
        this.endTime = endTime;
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

    public void setEndTime(LocalDateTime endTime) {
        this.endTime = endTime;
    }

    @Override
    public String toString() {
        return "Epic{" +
                " subtasks.size = " + this.subtaskIds.size() +
                ", subtasks.ids = " + this.subtaskIds +
                ", title='" + this.getTitle() + '\'' +
                ", description='" + this.getDescription() + '\'' +
                ", status=" + this.getStatus() +
                ", startTime=" + (this.getStartTime() != null ? this.getStartTime().toString() : "null") +
                ", duration=" + (this.getDuration() != null ? this.getDuration().toString() : "null") +
                ", endTime=" + (getEndTime() != null ? getEndTime().toString() : "null") +
                '}';
    }

    public String toString(Epic epic) {
        return String.format("%d,%s,%s,%s,%s,%d,%s,%s",
                this.getId(),
                EPIC.name(),
                this.getTitle(),
                this.getStatus(),
                this.getDescription(),
                this.getDuration().toMinutes(),
                this.getStartTime(),
                getEndTime());
    }

    public void updateFromSubtasks(List<SubTask> subtasks) {
        if (subtasks.isEmpty()) {
            this.endTime = null;
            return;
        }

        LocalDateTime earliestStart = subtasks.stream()
                .map(SubTask::getStartTime)
                .filter(Objects::nonNull)
                .min(LocalDateTime::compareTo)
                .orElse(null);

        LocalDateTime latestEnd = subtasks.stream()
                .map(SubTask::getEndTime)
                .filter(Objects::nonNull)
                .max(LocalDateTime::compareTo)
                .orElse(null);

        Duration totalDuration = subtasks.stream()
                .map(SubTask::getDuration)
                .reduce(Duration.ZERO, Duration::plus);

        this.setStartTime(earliestStart);
        this.setDuration(totalDuration);
        this.setEndTime(latestEnd);
    }
}
