package ru.yandex.practicum.task;

import org.junit.jupiter.api.Test;
import ru.yandex.practicum.task.interfaces.HistoryManager;
import ru.yandex.practicum.task.interfaces.TaskManager;
import ru.yandex.practicum.task.managers.Managers;

import static org.junit.jupiter.api.Assertions.assertInstanceOf;

class ManagersTest {

    @Test
    void getDefault() {
        assertInstanceOf(TaskManager.class, Managers.getDefault());
    }

    @Test
    void getDefaultHistory() {
        assertInstanceOf(HistoryManager.class, Managers.getDefaultHistory());
    }
}