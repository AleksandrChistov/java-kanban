package ru.yandex.practicum.task.utils;

import ru.yandex.practicum.task.tasks.Epic;
import ru.yandex.practicum.task.tasks.Subtask;
import ru.yandex.practicum.task.tasks.Task;

public class TaskManagerUtil {

    @SuppressWarnings("unchecked")
    public static <T extends Task> T getNewTask(T task) {
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

}
