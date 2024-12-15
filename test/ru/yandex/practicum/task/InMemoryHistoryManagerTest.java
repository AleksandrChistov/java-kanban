package ru.yandex.practicum.task;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.task.enums.TaskStatus;
import ru.yandex.practicum.task.managers.InMemoryHistoryManager;
import ru.yandex.practicum.task.tasks.Epic;
import ru.yandex.practicum.task.tasks.Subtask;
import ru.yandex.practicum.task.tasks.Task;

import static org.junit.jupiter.api.Assertions.assertEquals;

class InMemoryHistoryManagerTest {
    InMemoryHistoryManager historyManager;

    @BeforeEach
    void beforeEach() {
        historyManager = new InMemoryHistoryManager();
    }

    @Test
    void add() {
        Task task = new Task("Task", "Some task", TaskStatus.NEW);
        Epic epic = new Epic("Epic", "Some epic", TaskStatus.NEW);
        Subtask subtask = new Subtask("Subtask", "Some subtask", TaskStatus.NEW, 1);

        historyManager.add(task);
        historyManager.add(epic);
        historyManager.add(subtask);

        assertEquals(3, historyManager.getHistory().size());
    }

}