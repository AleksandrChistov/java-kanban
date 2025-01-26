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

        task.setId(1);
        epic.setId(2);
        subtask.setId(3);

        historyManager.add(task);
        historyManager.add(epic);
        historyManager.add(subtask);

        assertEquals(3, historyManager.getHistory().size(), "Кол-во добавленных задач не совпадает");
    }

    @Test
    void remove() {
        Task task = new Task("Task", "Some task", TaskStatus.NEW);

        task.setId(1);

        historyManager.add(task);

        assertEquals(1, historyManager.getHistory().size(), "Кол-во добавленных задач не совпадает");

        historyManager.remove(task.getId());

        assertEquals(0, historyManager.getHistory().size(), "Кол-во задач после удаления не совпадает");
    }

    @Test
    void checkDouble() {
        Task task = new Task("Task", "Some task", TaskStatus.NEW);

        task.setId(1);

        historyManager.add(task);

        assertEquals(1, historyManager.getHistory().size(), "Кол-во добавленных задач не совпадает");

        historyManager.add(task);

        assertEquals(1, historyManager.getHistory().size(), "Одна задача не должна быть добавлена дважды");
    }

}