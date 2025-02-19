package ru.yandex.practicum.task.tasks;

import ru.yandex.practicum.task.enums.TaskStatus;
import ru.yandex.practicum.task.enums.TaskType;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class Epic extends Task {
    private final List<Integer> subtaskIds = new ArrayList<>();
    private LocalDateTime endTime;

    public Epic(String name, String description, TaskStatus status) {
        super(name, description, status);
    }

    public List<Integer> getSubtaskIds() {
        return subtaskIds;
    }

    public void addSubtaskId(Integer id) {
        if (!subtaskIds.contains(id)) {
            subtaskIds.add(id);
        }
    }

    public void removeSubtaskId(Integer id) {
        subtaskIds.remove(id);
    }

    public void calculateState(List<Subtask> subtasks) {
        calculateAndSetStatus(subtasks);
        calculateAndSetTimeFields(subtasks);
    }

    @Override
    public void setStatus(TaskStatus status) {
        System.out.println("У эпика нельзя поменять статус");
    }

    @Override
    public LocalDateTime getEndTime() {
        return endTime;
    }

    @Override
    public String toString() {
        return String.format(
                "%d,%s,%s,%s,%s,%s,%s",
                getId(), TaskType.EPIC, getName(), getStatus(), getDescription(), getStartTimeFormatted(), getDuration().toMinutes());
    }

    private void calculateAndSetTimeFields(List<Subtask> subtasks) {
        duration = subtasks.stream()
                .map(Task::getDuration)
                .reduce(Duration.ZERO, Duration::plus);
        startTime = subtasks.stream()
                .map(Task::getStartTime)
                .min(Comparator.naturalOrder())
                .orElse(null);
        endTime = subtasks.stream()
                .map(t -> t.getStartTime().plus(duration))
                .max(Comparator.naturalOrder())
                .orElse(null);
    }

    private void calculateAndSetStatus(List<Subtask> subtasks) {
        super.setStatus(getStatusBySubtasks(subtasks));
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
