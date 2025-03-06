package ru.yandex.practicum.task.utils;

import org.junit.jupiter.api.Test;
import ru.yandex.practicum.task.enums.TaskStatus;
import ru.yandex.practicum.task.tasks.Epic;
import ru.yandex.practicum.task.tasks.Subtask;
import ru.yandex.practicum.task.tasks.Task;

import java.time.LocalDateTime;
import java.time.Month;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class TaskManagerUtilTest {

    @Test
    void isTimeIntersected() {
        Task task1 = new Task(
                "Test prioritized task 1", "Test prioritized task description 1", TaskStatus.NEW,
                LocalDateTime.of(2025, Month.FEBRUARY, 16, 22, 0), 20);
        task1.setId(1);
        Task task2 = new Task(
                "Test prioritized task 2", "Test prioritized task description 2", TaskStatus.IN_PROGRESS,
                LocalDateTime.of(2025, Month.FEBRUARY, 16, 22, 30), 0);
        task2.setId(2);
        Subtask subtaskIntersected = new Subtask(
                "Test prioritized subtask 1", "Test prioritized subtask description 1", TaskStatus.NEW, 1,
                LocalDateTime.of(2025, Month.FEBRUARY, 16, 22, 15), 5);
        subtaskIntersected.setId(3);
        Subtask subtask2 = new Subtask(
                "Test prioritized subtask 2", "Test prioritized subtask description 2", TaskStatus.IN_PROGRESS, 2,
                LocalDateTime.of(2025, Month.FEBRUARY, 16, 20, 0), 15);
        subtask2.setId(4);

        boolean isIntersected = TaskManagerUtil.isTimeIntersected(subtaskIntersected, Set.of(task1, task2, subtaskIntersected, subtask2));

        assertTrue(isIntersected, "Задача не нашла пересечение по времени выполнения");
    }

    @Test
    void isTimeNotIntersected() {
        Task task1 = new Task(
                "Test prioritized task 1", "Test prioritized task description 1", TaskStatus.NEW,
                LocalDateTime.of(2025, Month.FEBRUARY, 16, 22, 0), 20);
        task1.setId(1);
        Task task2 = new Task(
                "Test prioritized task 2", "Test prioritized task description 2", TaskStatus.IN_PROGRESS,
                LocalDateTime.of(2025, Month.FEBRUARY, 16, 22, 30), 0);
        task2.setId(2);
        Epic epic = new Epic("Test prioritized epic 1", "Test prioritized epic description 1", TaskStatus.NEW);
        Subtask subtaskIntersected = new Subtask(
                "Test prioritized subtask 1", "Test prioritized subtask description 1", TaskStatus.NEW, 1,
                LocalDateTime.of(2025, Month.FEBRUARY, 16, 22, 20), 10);
        subtaskIntersected.setId(3);
        Subtask subtask2 = new Subtask(
                "Test prioritized subtask 2", "Test prioritized subtask description 2", TaskStatus.IN_PROGRESS, 2,
                LocalDateTime.of(2025, Month.FEBRUARY, 16, 20, 0), 15);
        subtask2.setId(4);

        boolean isIntersected = TaskManagerUtil.isTimeIntersected(subtaskIntersected, Set.of(task1, task2, subtaskIntersected, subtask2));

        assertFalse(isIntersected, "Задача пересекается по времени выполнения");
    }

}