package ru.yandex.practicum.task;

import ru.yandex.practicum.task.enums.TaskStatus;

public class Subtask extends Task {
    private final int epicId;

    public Subtask(String name, String description, TaskStatus status, int epicId) {
        super(name, description, status);
        this.epicId = epicId;
    }

    public int getEpicId() {
        return epicId;
    }

}
