package ru.yandex.practicum.task.managers;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.task.enums.TaskStatus;
import ru.yandex.practicum.task.tasks.Epic;
import ru.yandex.practicum.task.tasks.Subtask;
import ru.yandex.practicum.task.tasks.Task;

import java.io.File;
import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class FileBackedTaskManagerTest extends AbstractTaskManagerTest {
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

        ((FileBackedTaskManager) taskManager).save();

        assertTrue(file.exists(), "После сохранения - файл не найден");
        assertEquals(0, file.length(), "После сохранение - файл не пустой");
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
        final Task task = new Task("Test saveTask", "Test saveTask description", TaskStatus.NEW);
        final Epic epic = new Epic("Test saveEpic", "Test saveEpic description", TaskStatus.NEW);
        final Epic createdEpic = taskManager.createEpic(epic);
        final Subtask subtask1 = new Subtask("Test saveSubtask 1", "Test saveSubtask description 1", TaskStatus.NEW, createdEpic.getId());
        final Subtask subtask2 = new Subtask("Test saveSubtask 2", "Test saveSubtask description 2", TaskStatus.NEW, createdEpic.getId());
        taskManager.createTask(task);
        taskManager.createSubtask(subtask1);
        taskManager.createSubtask(subtask2);
    }

}