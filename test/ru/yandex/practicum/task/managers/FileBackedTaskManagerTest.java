package ru.yandex.practicum.task.managers;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.task.enums.TaskStatus;
import ru.yandex.practicum.task.error.ManagerSaveException;
import ru.yandex.practicum.task.tasks.Epic;
import ru.yandex.practicum.task.tasks.Subtask;
import ru.yandex.practicum.task.tasks.Task;

import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.Month;

import static org.junit.jupiter.api.Assertions.*;

class FileBackedTaskManagerTest extends AbstractTaskManagerTest<FileBackedTaskManager> {
    File file;

    @BeforeEach
    void beforeEach() throws IOException {
        file = File.createTempFile("temp", ".txt");
        if (file.exists()) {
            taskManager = new FileBackedTaskManager(file);
        } else {
            System.out.println("Файла не существует");
        }
    }

    @Test
    void saveEmptyFile() {
        assertTrue(file.exists(), "Файла не существует");
        assertEquals(0, file.length(), "Файл не пустой");

        taskManager.save();

        assertTrue(file.exists(), "После сохранения - файл не найден");
        assertEquals(0, file.length(), "После сохранение - файл не пустой");
    }

    @Test
    void saveFileWithException() {
        assertTrue(file.setWritable(false), "Доступ на запись в файл не был запрещён");
        taskManager = new FileBackedTaskManager(file);

        assertThrows(ManagerSaveException.class, () -> taskManager.save());
    }

    @Test
    void loadEmptyFile() {
        FileBackedTaskManager fileBackedTaskManager = FileBackedTaskManager.loadFromFile(file);

        assertEquals(0, fileBackedTaskManager.getAllTasks().size(), "Неверное количество задач.");
        assertEquals(0, fileBackedTaskManager.getAllEpics().size(), "Неверное количество эпиков.");
        assertEquals(0, fileBackedTaskManager.getAllSubtasks().size(), "Неверное количество подзадач.");
    }

    @Test
    void saveAndLoadTasksFromFile() {
        createAllTasks();

        FileBackedTaskManager fileBackedTaskManager = FileBackedTaskManager.loadFromFile(file);

        assertEquals(1, fileBackedTaskManager.getAllTasks().size(), "Неверное количество задач.");
        assertEquals(1, fileBackedTaskManager.getAllEpics().size(), "Неверное количество эпиков.");
        assertEquals(2, fileBackedTaskManager.getAllSubtasks().size(), "Неверное количество подзадач.");
    }

    @Test
    void deleteAllTasksInFile() {
        createAllTasks();

        taskManager.deleteAllTasks();
        taskManager.deleteAllEpics();
        taskManager.deleteAllSubtasks();

        FileBackedTaskManager fileBackedTaskManager = FileBackedTaskManager.loadFromFile(file);

        assertEquals(0, fileBackedTaskManager.getAllTasks().size(), "Неверное количество задач.");
        assertEquals(0, fileBackedTaskManager.getAllEpics().size(), "Неверное количество эпиков.");
        assertEquals(0, fileBackedTaskManager.getAllSubtasks().size(), "Неверное количество подзадач.");
    }

    private void createAllTasks() {
        final Task task = new Task(
                "Test saveTask", "Test saveTask description", TaskStatus.NEW,
                LocalDateTime.of(2025, Month.FEBRUARY, 16, 22, 0), 0);
        final Epic epic = new Epic("Test saveEpic", "Test saveEpic description", TaskStatus.NEW);
        final Epic createdEpic = taskManager.createEpic(epic);
        final Subtask subtask1 = new Subtask(
                "Test saveSubtask 1", "Test saveSubtask description 1", TaskStatus.NEW,
                createdEpic.getId(), LocalDateTime.of(2025, Month.FEBRUARY, 16, 22, 0), 0);
        final Subtask subtask2 = new Subtask(
                "Test saveSubtask 2", "Test saveSubtask description 2", TaskStatus.NEW,
                createdEpic.getId(), LocalDateTime.of(2025, Month.FEBRUARY, 16, 22, 0), 0);
        taskManager.createTask(task);
        taskManager.createSubtask(subtask1);
        taskManager.createSubtask(subtask2);
    }

}