package ru.yandex.practicum.task;

import ru.yandex.practicum.task.interfaces.HistoryManager;

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