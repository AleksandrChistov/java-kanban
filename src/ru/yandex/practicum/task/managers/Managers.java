package ru.yandex.practicum.task.managers;

import ru.yandex.practicum.task.interfaces.HistoryManager;
import ru.yandex.practicum.task.interfaces.TaskManager;

import java.io.File;

public class Managers {
    public static File FILE = new File("resources/tasks.txt");

    public static FileBackedTaskManager getDefault() {
        return new FileBackedTaskManager(FILE);
    }

    public static HistoryManager getDefaultHistory() {
        return new InMemoryHistoryManager();
    }

    public static TaskManager getInMemoryManager() {
        return new InMemoryTaskManager();
    }

}
