package entities;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Objects;

import static entities.Type.TASK;

public class Task {

    private String title;
    private int id;
    private String description;
    private Status status;
    private Duration duration;
    private LocalDateTime startTime;

    public Task(String title, String description, Status status) {
        this.title = title;
        this.description = description;
        this.status = status;
    }

    public Task(String title, String description) {
        this.title = title;
        this.description = description;
        this.status = Status.NEW;
    }

    public Task(int id, String title, String description, Status status) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.status = status;
    }

    public Task(int id, String title, String description, Status status, Duration duration, LocalDateTime startTime) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.status = status;
        this.duration = duration;
        this.startTime = startTime;
    }

    public Task(String title, String description, Status status, Duration duration, LocalDateTime startTime) {
        this.title = title;
        this.description = description;
        this.status = status;
        this.duration = duration;
        this.startTime = startTime;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public LocalDateTime getStartTime() {
        return startTime != null ? null : startTime;
    }

    public void setDuration(Duration duration) {
        this.duration = duration;
    }

    public void setStartTime(LocalDateTime startTime) {
        this.startTime = startTime;
    }

    public Duration getDuration() {
        return duration != null ? duration : Duration.ZERO;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Task task = (Task) o;
        return Objects.equals(title, task.title) && Objects.equals(description, task.description) && status == task.status;
    }

    @Override
    public int hashCode() {
        return Objects.hash(title, description, status);
    }

    @Override
    public String toString() {
        return "Task{" +
                " id=" + this.id +
                ", title=" + title +
                ", description=" + description +
                ", status=" + status +
                ", duration=" + duration +
                ", startTime=" + startTime +
                '}';
    }

    public String toString(Task task) {
        return String.format("%d,%s,%s,%s,%s,%d,%s,%s",
                this.id,
                TASK.name(),
                this.title,
                this.status,
                this.description,
                (this.duration != null) ? this.duration.toMinutes() : 0,
                (this.startTime != null) ? this.startTime : null,
                getEndTime());
    }

    public LocalDateTime getEndTime() {
        if (this.startTime == null || this.duration.isZero()) {
            return null;
        }
        return this.startTime.plus(this.duration);
    }
}
