package ru.yandex.practicum.task.utils;

import ru.yandex.practicum.task.tasks.Epic;
import ru.yandex.practicum.task.tasks.Subtask;
import ru.yandex.practicum.task.tasks.Task;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Optional;

public class TaskManagerUtil {

    @SuppressWarnings("unchecked")
    public static <T extends Task> T getCopyTask(T task) {
        if (task instanceof Epic) {
            Epic newEpic = new Epic(task.getName(), task.getDescription(), task.getStatus());
            newEpic.setId(task.getId());
            return (T) newEpic;
        }

        if (task instanceof Subtask) {
            Subtask newSubtask = new Subtask(task.getName(), task.getDescription(), task.getStatus(),
                    ((Subtask) task).getEpicId(), task.getStartTime(), task.getDuration().toMinutes());
            newSubtask.setId(task.getId());
            return (T) newSubtask;
        }

        Task newTask = new Task(task.getName(), task.getDescription(), task.getStatus(),
                task.getStartTime(), task.getDuration().toMinutes());
        newTask.setId(task.getId());
        return (T) newTask;
    }

    public static boolean isTimeIntersected(Task task, Collection<Task> tasks) {
        Optional<Task> found = tasks.stream()
                .filter(t -> !task.equals(t))
                .filter(t -> isIntersectedByTime(t, task))
                .findAny();

        return found.isPresent();
    }

    private static boolean isIntersectedByTime(Task task1, Task task2) {
        LocalDateTime start1 = task1.getStartTime();
        LocalDateTime start2 = task2.getStartTime();
        LocalDateTime end1 = task1.getStartTime().plus(task1.getDuration());
        LocalDateTime end2 = task2.getStartTime().plus(task2.getDuration());

        if (start1 == null || start2 == null) {
            return false;
        }

        return !((start1.isAfter(end2) || start1.isEqual(end2)) || (end1.isBefore(start2) || end1.isEqual(start2)));
    }

}
