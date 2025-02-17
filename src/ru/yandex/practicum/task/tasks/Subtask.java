package ru.yandex.practicum.task.tasks;

import ru.yandex.practicum.task.enums.TaskStatus;
import ru.yandex.practicum.task.enums.TaskType;

import java.time.LocalDateTime;

public class Subtask extends Task {
    private final int epicId;

    public Subtask(String name, String description, TaskStatus status, int epicId, LocalDateTime startTime, long durationMinute) {
        super(name, description, status, startTime, durationMinute);
        this.epicId = epicId;
    }

    public int getEpicId() {
        return epicId;
    }

    @Override
    public String toString() {
        return String.format(
                "%d,%s,%s,%s,%s,%s,%s,%d",
                getId(), TaskType.SUBTASK, getName(), getStatus(), getDescription(), getStartTimeFormatted(), getDuration().toMinutes(), getEpicId());
    }

}
