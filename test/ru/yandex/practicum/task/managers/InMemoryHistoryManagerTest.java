package ru.yandex.practicum.task.managers;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.task.enums.TaskStatus;
import ru.yandex.practicum.task.tasks.Epic;
import ru.yandex.practicum.task.tasks.Subtask;
import ru.yandex.practicum.task.tasks.Task;

import java.time.LocalDateTime;
import java.time.Month;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class InMemoryHistoryManagerTest {
    InMemoryHistoryManager historyManager;

    @BeforeEach
    void beforeEach() {
        historyManager = new InMemoryHistoryManager();
    }

    @Test
    void add() {
        Task task = new Task(
                "Task", "Some task", TaskStatus.NEW,
                LocalDateTime.of(2025, Month.FEBRUARY, 16, 22, 0), 0);
        Epic epic = new Epic("Epic", "Some epic", TaskStatus.NEW);
        Subtask subtask = new Subtask(
                "Subtask", "Some subtask", TaskStatus.NEW, 1,
                LocalDateTime.of(2025, Month.FEBRUARY, 16, 22, 0), 0);

        task.setId(1);
        epic.setId(2);
        subtask.setId(3);

        historyManager.add(task);
        historyManager.add(epic);
        historyManager.add(subtask);

        assertEquals(3, historyManager.getHistory().size(), "Кол-во добавленных задач не совпадает");
    }

    @Test
    void repeatedAdd() {
        Task task = new Task(
                "Task", "Some task", TaskStatus.NEW,
                LocalDateTime.of(2025, Month.FEBRUARY, 16, 22, 0), 0);
        task.setId(1);

        historyManager.add(task);

        assertEquals(task, historyManager.getHistory().getFirst(), "Добавленная задача отличается от ожидаемой");
        assertEquals("Task", historyManager.getHistory().getFirst().getName(), "Имя добавленной задачи отличается от ожидаемого");

        Task taskNewName = new Task(
                "Task new", "Some task", TaskStatus.NEW,
                LocalDateTime.of(2025, Month.FEBRUARY, 16, 22, 0), 0);
        taskNewName.setId(1);

        historyManager.add(taskNewName);

        assertEquals(taskNewName, historyManager.getHistory().getFirst(), "Обновленная задача отличается от ожидаемой");
        assertEquals("Task new", historyManager.getHistory().getFirst().getName(), "Имя обновленной задачи отличается от ожидаемого");
    }

    @Test
    void removeFirst() {
        createAndAddTasks();

        assertEquals(3, historyManager.getHistory().size(), "Кол-во добавленных задач не совпадает");

        historyManager.remove(1);
        Optional<Task> found = historyManager.getHistory().stream().filter(t -> t.getId() == 1).findFirst();

        assertEquals(2, historyManager.getHistory().size(), "Кол-во задач после удаления не совпадает");
        assertTrue(found.isEmpty(), "Задача была найдена в списке после удаления");
    }

    @Test
    void removeInTheMiddle() {
        createAndAddTasks();

        assertEquals(3, historyManager.getHistory().size(), "Кол-во добавленных задач не совпадает");

        historyManager.remove(2);
        Optional<Task> found = historyManager.getHistory().stream().filter(t -> t.getId() == 2).findFirst();

        assertEquals(2, historyManager.getHistory().size(), "Кол-во задач после удаления не совпадает");
        assertTrue(found.isEmpty(), "Задача была найдена в списке после удаления");
    }

    @Test
    void removeLast() {
        createAndAddTasks();

        assertEquals(3, historyManager.getHistory().size(), "Кол-во добавленных задач не совпадает");

        historyManager.remove(3);
        Optional<Task> found = historyManager.getHistory().stream().filter(t -> t.getId() == 3).findFirst();

        assertEquals(2, historyManager.getHistory().size(), "Кол-во задач после удаления не совпадает");
        assertTrue(found.isEmpty(), "Задача была найдена в списке после удаления");
    }

    @Test
    void checkDouble() {
        Task task = new Task(
                "Task", "Some task", TaskStatus.NEW,
                LocalDateTime.of(2025, Month.FEBRUARY, 16, 22, 0), 0);

        task.setId(1);

        historyManager.add(task);

        assertEquals(1, historyManager.getHistory().size(), "Кол-во добавленных задач не совпадает");

        historyManager.add(task);

        assertEquals(1, historyManager.getHistory().size(), "Одна задача не должна быть добавлена дважды");
    }

    private void createAndAddTasks() {
        Task task1 = new Task(
                "Task 1", "Some task 1", TaskStatus.NEW,
                LocalDateTime.of(2025, Month.FEBRUARY, 16, 22, 0), 20);
        Task task2 = new Task(
                "Task 2", "Some task 2", TaskStatus.NEW,
                LocalDateTime.of(2025, Month.FEBRUARY, 16, 22, 30), 10);
        Task task3 = new Task(
                "Task 3", "Some task 3", TaskStatus.NEW,
                LocalDateTime.of(2025, Month.FEBRUARY, 16, 22, 30), 10);

        task1.setId(1);
        task2.setId(2);
        task3.setId(3);

        historyManager.add(task1);
        historyManager.add(task2);
        historyManager.add(task3);
    }

}