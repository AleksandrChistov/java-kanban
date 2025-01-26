package ru.yandex.practicum.task.interfaces;

import ru.yandex.practicum.task.tasks.Task;

import java.util.List;

public interface HistoryManager {

    void add(Task task);

    void remove(int id);

    List<Task> getHistory();

}
