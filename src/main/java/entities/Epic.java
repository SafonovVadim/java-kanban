package entities;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static entities.Type.EPIC;

public class Epic extends Task {

    private List<Integer> subtaskIds = new ArrayList<>();
    private Duration duration;
    private LocalDateTime startTime;
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

    public Epic(int id, String title, String description, Status status, LocalDateTime startTime) {
        super(id, title, description, status);
        this.startTime = startTime;
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

    public void setStartTime(LocalDateTime startTime) {
        this.startTime = startTime;
    }

    @Override
    public String toString() {
        return "Epic{" +
                " subtasks.size = " + this.subtaskIds.size() +
                ", subtasks.ids = " + this.subtaskIds +
                ", title='" + this.getTitle() + '\'' +
                ", description='" + this.getDescription() + '\'' +
                ", status=" + this.getStatus() +
                ", duration=" + this.getDuration() +
                ", startTime=" + this.getStartTime() +
                ", endTime=" + this.getEndTime() +
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
                this.startTime,
                getEndTime());
    }

    public void updateFromSubtasks(List<SubTask> subtasks) {
        if (subtasks.isEmpty()) {
            this.startTime = null;
            this.duration = Duration.ZERO;
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

        this.startTime = earliestStart;
        this.duration = totalDuration;
        this.endTime = latestEnd;
    }
}
