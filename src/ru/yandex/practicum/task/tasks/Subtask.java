package ru.yandex.practicum.task.tasks;

import ru.yandex.practicum.task.enums.TaskStatus;
import ru.yandex.practicum.task.enums.TaskType;

public class Subtask extends Task {
    private final int epicId;

    public Subtask(String name, String description, TaskStatus status, int epicId) {
        super(name, description, status);
        this.epicId = epicId;
    }

    public int getEpicId() {
        return epicId;
    }

    @Override
    public String toString() {
        return String.format("%d,%s,%s,%s,%s,%d", getId(), TaskType.SUBTASK, getName(), getStatus(), getDescription(), getEpicId());
    }

}
