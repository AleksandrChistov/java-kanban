package ru.yandex.practicum.task.tasks;

import ru.yandex.practicum.task.enums.TaskStatus;
import ru.yandex.practicum.task.enums.TaskType;
import ru.yandex.practicum.task.utils.DateTimeTaskUtil;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Objects;

public class Task {
    private Integer id;
    private String name;
    private String description;
    private TaskStatus status;
    protected LocalDateTime startTime;
    protected Duration duration;

    public Task(String name, String description, TaskStatus status, LocalDateTime startTime, long durationMinute) {
        this(name, description, status);
        this.status = status;
        this.startTime = startTime;
        this.duration = Duration.ofMinutes(durationMinute);
    }

    public Task(String name, String description, TaskStatus status) {
        Objects.requireNonNull(name, "Имя не может быть пустым");
        Objects.requireNonNull(status, "Статус задачи не может быть пустым");
        this.name = name;
        this.description = description;
        this.status = status;
        this.duration = Duration.ofMinutes(0);
    }

    public Integer getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public TaskStatus getStatus() {
        return status;
    }

    public void setStatus(TaskStatus status) {
        this.status = status;
    }

    public LocalDateTime getStartTime() {
        return startTime;
    }

    public void setStartTime(LocalDateTime startTime) {
        this.startTime = startTime;
    }

    public Duration getDuration() {
        return duration;
    }

    public void setDuration(Duration duration) {
        this.duration = duration;
    }

    public LocalDateTime getEndTime() {
        return startTime.plusMinutes(duration.toMinutes());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Task task = (Task) o;
        return Objects.equals(id, task.id);
    }

    @Override
    public int hashCode() {
        return id;
    }

    @Override
    public String toString() {
        return String.format(
                "%d,%s,%s,%s,%s,%s,%s",
                getId(), TaskType.TASK, getName(), getStatus(), getDescription(), getStartTimeFormatted(), getDuration().toMinutes());
    }

    protected String getStartTimeFormatted() {
        return getStartTime() != null ? DateTimeTaskUtil.format(getStartTime()) : "";
    }

}
