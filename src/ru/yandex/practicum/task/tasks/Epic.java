package ru.yandex.practicum.task.tasks;

import ru.yandex.practicum.task.enums.TaskStatus;
import ru.yandex.practicum.task.enums.TaskType;

import java.util.ArrayList;
import java.util.List;

public class Epic extends Task {
    private final List<Integer> subtaskIds = new ArrayList<>();

    public Epic(String name, String description, TaskStatus status) {
        super(name, description, status);
    }

    public List<Integer> getSubtaskIds() {
        return subtaskIds;
    }

    public void addSubtaskId(Integer id) {
        subtaskIds.add(id);
    }

    public void removeSubtaskId(Integer id) {
        subtaskIds.remove(id);
    }

    public void calculateAndSetStatus(List<Subtask> subtasks) {
        super.setStatus(getStatusBySubtasks(subtasks));
    }

    @Override
    public void setStatus(TaskStatus status) {
        System.out.println("У эпика нельзя поменять статус");
    }

    @Override
    public String toString() {
        return String.format("%d,%s,%s,%s,%s", getId(), TaskType.EPIC, getName(), getStatus(), getDescription());
    }

    private TaskStatus getStatusBySubtasks(List<Subtask> subtasks) {
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
