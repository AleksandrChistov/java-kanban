package ru.yandex.practicum.task.interfaces;

import ru.yandex.practicum.task.tasks.Epic;
import ru.yandex.practicum.task.tasks.Subtask;
import ru.yandex.practicum.task.tasks.Task;

import java.util.List;
import java.util.Set;

public interface TaskManager {
    Task createTask(Task task);

    Epic createEpic(Epic epic);

    Subtask createSubtask(Subtask subtask);

    Task updateTask(Task task);

    Epic updateEpic(Epic epic);

    Subtask updateSubtask(Subtask subtask);

    List<Task> getAllTasks();

    List<Epic> getAllEpics();

    List<Subtask> getAllSubtasks();

    Task getTask(int id);

    Epic getEpic(int id);

    Subtask getSubtask(int id);

    void deleteAllTasks();

    void deleteAllEpics();

    void deleteAllSubtasks();

    List<Subtask> getSubtasksByEpic(Epic epic);

    Task deleteTask(int id);

    Epic deleteEpic(int id);

    Subtask deleteSubtask(int id);

    List<Task> getHistory();

    Set<Task> getPrioritizedTasks();
}
