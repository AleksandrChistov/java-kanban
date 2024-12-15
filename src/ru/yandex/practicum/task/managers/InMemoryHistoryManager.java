package ru.yandex.practicum.task.managers;

import ru.yandex.practicum.task.interfaces.HistoryManager;
import ru.yandex.practicum.task.tasks.Task;

import java.util.ArrayList;
import java.util.List;

public class InMemoryHistoryManager implements HistoryManager {
    private static final int HISTORY_CAPACITY = 10;
    private final List<Task> historyTaskList = new ArrayList<>(HISTORY_CAPACITY);

    @Override
    public void add(Task task) {
        if (historyTaskList.size() == HISTORY_CAPACITY) {
            historyTaskList.removeFirst();
        }
        historyTaskList.add(task);
    }

    @Override
    public List<Task> getHistory() {
        return historyTaskList;
    }

}
