package ru.yandex.practicum;

import java.util.ArrayList;
import java.util.List;

public class Epic extends Task {
    private final List<Subtask> subtasks = new ArrayList<>();

    public Epic(String name, String description, TaskStatus status) {
        super(name, description, status);
    }

    public List<Subtask> getSubtasks() {
        return subtasks;
    }

    public void addSubtask(Subtask subtask) {
        subtasks.add(subtask);
        super.setStatus(getStatusBySubtasks());
    }

    public void updateSubtask(Subtask subtask) {
        if (subtasks.remove(subtask)) {
            subtasks.add(subtask);
            super.setStatus(getStatusBySubtasks());
        }
    }

    public void removeSubtask(Subtask subtask) {
        subtasks.remove(subtask);
        super.setStatus(getStatusBySubtasks());
    }

    @Override
    public void setStatus(TaskStatus status) {
        System.out.println("У эпика нельзя поменять статус");
    }

    private TaskStatus getStatusBySubtasks() {
        if (subtasks.isEmpty()) {
            return TaskStatus.NEW;
        }

        boolean isAllDone = true;

        for (Subtask subtask : subtasks) {
            switch (subtask.getStatus()) {
                case IN_PROGRESS -> {
                    return TaskStatus.IN_PROGRESS;
                }
                case NEW -> isAllDone = false;
            }
        }

        return isAllDone ? TaskStatus.DONE : TaskStatus.NEW;
    }
}
