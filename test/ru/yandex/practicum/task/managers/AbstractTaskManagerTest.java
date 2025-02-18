package ru.yandex.practicum.task.managers;

import org.junit.jupiter.api.Test;
import ru.yandex.practicum.task.enums.TaskStatus;
import ru.yandex.practicum.task.interfaces.TaskManager;
import ru.yandex.practicum.task.tasks.Epic;
import ru.yandex.practicum.task.tasks.Subtask;
import ru.yandex.practicum.task.tasks.Task;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.Month;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

abstract class AbstractTaskManagerTest<T extends TaskManager> {
    protected T taskManager;

    @Test
    void createTask() {
        Task task = new Task("Test createTask", "Test createTask description", TaskStatus.NEW);

        final Task createdTask = taskManager.createTask(task);
        final Task savedTask = taskManager.getTask(createdTask.getId());

        assertNotNull(savedTask, "Задача не найдена.");
        assertEquals(task, savedTask, "Задачи не совпадают.");

        final List<Task> tasks = taskManager.getAllTasks();

        assertEquals(1, tasks.size(), "Неверное количество задач.");
        assertEquals(task, tasks.getFirst(), "Задачи не совпадают.");
    }

    @Test
    void createEpic() {
        Epic epic = new Epic("Test createEpic", "Test createEpic description", TaskStatus.NEW);

        final Epic createdEpic = taskManager.createEpic(epic);
        final Epic savedEpic = taskManager.getEpic(createdEpic.getId());

        assertNotNull(savedEpic, "Эпик не найден.");
        assertEquals(epic, savedEpic, "Эпики не совпадают.");

        final List<Epic> epics = taskManager.getAllEpics();

        assertEquals(1, epics.size(), "Неверное количество эпиков.");
        assertEquals(epic, epics.getFirst(), "Эпики не совпадают.");
    }

    @Test
    void createSubtask() {
        Epic epic = new Epic("Epic", "Epic description", TaskStatus.NEW);
        final Epic createdEpic = taskManager.createEpic(epic);
        Subtask subtask = new Subtask(
                "Test createSubtask", "Test createSubtask description", TaskStatus.NEW, createdEpic.getId(),
                LocalDateTime.of(2025, Month.FEBRUARY, 16, 22, 0), 0);

        final Subtask createdSubtask = taskManager.createSubtask(subtask);
        final Subtask savedSubtask = taskManager.getSubtask(createdSubtask.getId());

        assertNotNull(savedSubtask, "Подзадача не найдена.");
        assertEquals(subtask, savedSubtask, "Подзадачи не совпадают.");

        final List<Subtask> subtasks = taskManager.getAllSubtasks();
        final Epic epicForSubtask = taskManager.getEpic(savedSubtask.getEpicId());

        assertEquals(1, subtasks.size(), "Неверное количество подзадач.");
        assertEquals(subtask, subtasks.getFirst(), "Подзадачи не совпадают.");
        assertEquals(createdEpic, epicForSubtask, "Связанный с подзадачей эпик не совпадает.");
    }

    @Test
    void updateTask() {
        Task task = new Task(
                "Test updateTask", "Test updateTask description", TaskStatus.NEW,
                LocalDateTime.of(2025, Month.FEBRUARY, 16, 22, 0), 0);
        final Task createdTask = taskManager.createTask(task);

        LocalDateTime updatedStartTime = LocalDateTime.of(2025, Month.FEBRUARY, 16, 22, 20);
        Duration updatedDuration = Duration.ofMinutes(15);

        createdTask.setName("New name");
        createdTask.setDescription("New description");
        createdTask.setStatus(TaskStatus.IN_PROGRESS);
        createdTask.setStartTime(updatedStartTime);
        createdTask.setDuration(updatedDuration);

        LocalDateTime calculatedEndTime = updatedStartTime.plus(updatedDuration);

        taskManager.updateTask(createdTask);
        Task updatedTask = taskManager.getTask(createdTask.getId());

        assertEquals(createdTask, updatedTask, "Задачи не совпадают.");
        assertEquals(createdTask.getName(), updatedTask.getName(), "Имена задач не совпадают.");
        assertEquals(createdTask.getDescription(), updatedTask.getDescription(), "Описание задач не совпадают.");
        assertEquals(createdTask.getStatus(), updatedTask.getStatus(), "Статусы задач не совпадают.");
        assertEquals(createdTask.getStartTime(), updatedTask.getStartTime(), "Время начала задач не совпадают.");
        assertEquals(createdTask.getDuration(), updatedTask.getDuration(), "Продолжительность задач не совпадает.");
        assertEquals(calculatedEndTime, updatedTask.getEndTime(), "Время окончания задач не совпадают.");

        final List<Task> tasks = taskManager.getAllTasks();

        assertEquals(1, tasks.size(), "Неверное количество задач.");
        assertEquals(task, tasks.getFirst(), "Задачи не совпадают.");
    }

    @Test
    void updateEpic() {
        Epic epic = new Epic("Test updateEpic", "Test updateEpic description", TaskStatus.NEW);
        final Epic createdEpic = taskManager.createEpic(epic);

        createdEpic.setName("New name");
        createdEpic.setDescription("New description");

        taskManager.updateEpic(createdEpic);
        Epic updatedEpic = taskManager.getEpic(createdEpic.getId());

        assertEquals(createdEpic, updatedEpic, "Эпик не совпадают.");
        assertEquals(createdEpic.getName(), updatedEpic.getName(), "Имена эпиков не совпадают.");
        assertEquals(createdEpic.getDescription(), updatedEpic.getDescription(), "Описание эпиков не совпадают.");
        assertEquals(createdEpic.getStatus(), updatedEpic.getStatus(), "Статусы эпиков не совпадают.");

        final List<Epic> epics = taskManager.getAllEpics();

        assertEquals(1, epics.size(), "Неверное количество эпиков.");
        assertEquals(epic, epics.getFirst(), "Эпик не совпадают.");
    }

    @Test
    void updateSubtask() {
        Epic epic = new Epic("Epic", "Epic description", TaskStatus.NEW);
        final Epic createdEpic = taskManager.createEpic(epic);
        Subtask subtask = new Subtask(
                "Test updateSubtask", "Test updateSubtask description", TaskStatus.NEW, createdEpic.getId(),
                LocalDateTime.of(2025, Month.FEBRUARY, 16, 22, 0), 0);
        final Subtask createdSubtask = taskManager.createSubtask(subtask);

        LocalDateTime updatedStartTime = LocalDateTime.of(2025, Month.FEBRUARY, 16, 22, 30);
        Duration updatedDuration = Duration.ofMinutes(30);

        createdSubtask.setName("New name");
        createdSubtask.setDescription("New description");
        createdSubtask.setStartTime(updatedStartTime);
        createdSubtask.setDuration(updatedDuration);

        LocalDateTime calculatedEndTime = updatedStartTime.plus(updatedDuration);

        taskManager.updateSubtask(createdSubtask);
        Subtask updatedSubtask = taskManager.getSubtask(createdSubtask.getId());

        assertEquals(createdSubtask, updatedSubtask, "Подзадачи не совпадают.");
        assertEquals(createdSubtask.getName(), updatedSubtask.getName(), "Имена подзадач не совпадают.");
        assertEquals(createdSubtask.getDescription(), updatedSubtask.getDescription(), "Описание подзадач не совпадают.");
        assertEquals(createdSubtask.getStatus(), updatedSubtask.getStatus(), "Статусы подзадач не совпадают.");
        assertEquals(createdSubtask.getStartTime(), updatedSubtask.getStartTime(), "Время начала подзадач не совпадают.");
        assertEquals(createdSubtask.getDuration(), updatedSubtask.getDuration(), "Продолжительность подзадач не совпадает.");
        assertEquals(calculatedEndTime, updatedSubtask.getEndTime(), "Время окончания подзадач не совпадают.");

        final List<Subtask> subtasks = taskManager.getAllSubtasks();

        assertEquals(1, subtasks.size(), "Неверное количество подзадач.");
        assertEquals(subtask, subtasks.getFirst(), "Подзадачи не совпадают.");
    }

    @Test
    void cannotUpdateTaskViaCreatedTask() {
        Task task = new Task(
                "Test task", "Test task description", TaskStatus.NEW,
                LocalDateTime.of(2025, Month.FEBRUARY, 16, 22, 0), 0);
        final Task createdTask = taskManager.createTask(task);

        createdTask.setName("New name");
        createdTask.setDescription("New description");
        createdTask.setStatus(TaskStatus.IN_PROGRESS);
        createdTask.setStartTime(LocalDateTime.of(2025, Month.FEBRUARY, 16, 22, 30));
        createdTask.setDuration(Duration.ofMinutes(30));

        Task taskFromStore = taskManager.getTask(createdTask.getId());

        assertEquals(createdTask, taskFromStore, "Задачи не совпадают.");
        assertNotEquals(createdTask.getName(), taskFromStore.getName(), "Имена задач совпадают.");
        assertNotEquals(createdTask.getDescription(), taskFromStore.getDescription(), "Описание задач совпадают.");
        assertNotEquals(createdTask.getStatus(), taskFromStore.getStatus(), "Статусы задач совпадают.");
        assertNotEquals(createdTask.getStartTime(), taskFromStore.getStartTime(), "Время начала задач совпадают.");
        assertNotEquals(createdTask.getDuration(), taskFromStore.getDuration(), "Продолжительность задач совпадает.");
    }

    @Test
    void cannotUpdateEpicViaCreatedEpic() {
        Epic epic = new Epic("Test epic", "Test epic description", TaskStatus.NEW);
        final Epic createdEpic = taskManager.createEpic(epic);

        createdEpic.setName("New name");
        createdEpic.setDescription("New description");
        createdEpic.setStatus(TaskStatus.IN_PROGRESS);
        createdEpic.setStartTime(LocalDateTime.of(2025, Month.FEBRUARY, 16, 22, 30));
        createdEpic.setDuration(Duration.ofMinutes(30));

        Epic epicFromStore = taskManager.getEpic(createdEpic.getId());

        assertEquals(createdEpic, epicFromStore, "Эпики не совпадают.");
        assertNotEquals(createdEpic.getName(), epicFromStore.getName(), "Имена эпиков совпадают.");
        assertNotEquals(createdEpic.getDescription(), epicFromStore.getDescription(), "Описание эпиков совпадают.");
        assertEquals(createdEpic.getStatus(), epicFromStore.getStatus(), "У эпика нельзя поменять статус.");
        assertNotEquals(createdEpic.getStartTime(), epicFromStore.getStartTime(), "Время начала эпиков совпадают.");
        assertNotEquals(createdEpic.getDuration(), epicFromStore.getDuration(), "Продолжительность эпиков совпадает.");
    }

    @Test
    void cannotUpdateSubtaskViaCreatedSubtask() {
        Epic epic = new Epic("Test epic", "Test epic description", TaskStatus.NEW);
        final Epic createdEpic = taskManager.createEpic(epic);
        Subtask subtask = new Subtask(
                "Test subtask", "Test subtask description", TaskStatus.NEW, createdEpic.getId(),
                LocalDateTime.of(2025, Month.FEBRUARY, 16, 22, 0), 0);
        final Subtask createdSubtask = taskManager.createSubtask(subtask);

        createdSubtask.setName("New name");
        createdSubtask.setDescription("New description");
        createdSubtask.setStatus(TaskStatus.IN_PROGRESS);
        createdSubtask.setStartTime(LocalDateTime.of(2025, Month.FEBRUARY, 16, 22, 30));
        createdSubtask.setDuration(Duration.ofMinutes(30));

        Subtask subtaskFromStore = taskManager.getSubtask(createdSubtask.getId());

        assertEquals(createdSubtask, subtaskFromStore, "Подзадачи не совпадают.");
        assertNotEquals(createdSubtask.getName(), subtaskFromStore.getName(), "Имена подзадач совпадают.");
        assertNotEquals(createdSubtask.getDescription(), subtaskFromStore.getDescription(), "Описание подзадач совпадают.");
        assertNotEquals(createdSubtask.getStatus(), subtaskFromStore.getStatus(), "Статусы подзадач совпадают.");
        assertNotEquals(createdSubtask.getStartTime(), subtaskFromStore.getStartTime(), "Время начала подзадач совпадают.");
        assertNotEquals(createdSubtask.getDuration(), subtaskFromStore.getDuration(), "Продолжительность подзадач совпадает.");
    }

    @Test
    void changeEpicStatusNew() {
        Epic epic = new Epic("Test epic", "Test epic description", TaskStatus.NEW);
        final Epic createdEpic = taskManager.createEpic(epic);

        Epic epicFromStore1 = taskManager.getEpic(createdEpic.getId());

        assertEquals(TaskStatus.NEW, epicFromStore1.getStatus(), "После создания эпика статусы не совпадают.");

        Subtask subtask1 = new Subtask(
                "Test subtask1", "Test subtask description1", TaskStatus.NEW, createdEpic.getId(),
                LocalDateTime.of(2025, Month.FEBRUARY, 16, 22, 0), 30);
        Subtask subtask2 = new Subtask(
                "Test subtask2", "Test subtask description2", TaskStatus.NEW, createdEpic.getId(),
                LocalDateTime.of(2025, Month.FEBRUARY, 16, 22, 30), 20);
        taskManager.createSubtask(subtask1);
        taskManager.createSubtask(subtask2);

        Epic epicFromStore2 = taskManager.getEpic(createdEpic.getId());

        assertEquals(TaskStatus.NEW, epicFromStore2.getStatus(), "После добавления подзадач статусы эпика не совпадают.");
    }

    @Test
    void changeEpicStatusDone() {
        Epic epic = new Epic("Test epic", "Test epic description", TaskStatus.NEW);
        final Epic createdEpic = taskManager.createEpic(epic);

        Epic epicFromStore1 = taskManager.getEpic(createdEpic.getId());

        assertEquals(TaskStatus.NEW, epicFromStore1.getStatus(), "После создания эпика статусы не совпадают.");

        Subtask subtask1 = new Subtask(
                "Test subtask1", "Test subtask description1", TaskStatus.DONE, createdEpic.getId(),
                LocalDateTime.of(2025, Month.FEBRUARY, 16, 22, 0), 30);
        Subtask subtask2 = new Subtask(
                "Test subtask2", "Test subtask description2", TaskStatus.DONE, createdEpic.getId(),
                LocalDateTime.of(2025, Month.FEBRUARY, 16, 22, 30), 20);
        taskManager.createSubtask(subtask1);
        taskManager.createSubtask(subtask2);

        Epic epicFromStore2 = taskManager.getEpic(createdEpic.getId());

        assertEquals(TaskStatus.DONE, epicFromStore2.getStatus(), "После добавления подзадач статусы эпика не совпадают.");
    }

    @Test
    void changeEpicStatusNewAndDone() {
        Epic epic = new Epic("Test epic", "Test epic description", TaskStatus.NEW);
        final Epic createdEpic = taskManager.createEpic(epic);

        Epic epicFromStore1 = taskManager.getEpic(createdEpic.getId());

        assertEquals(TaskStatus.NEW, epicFromStore1.getStatus(), "После создания эпика статусы не совпадают.");

        Subtask subtask1 = new Subtask(
                "Test subtask1", "Test subtask description1", TaskStatus.NEW, createdEpic.getId(),
                LocalDateTime.of(2025, Month.FEBRUARY, 16, 22, 0), 30);
        Subtask subtask2 = new Subtask(
                "Test subtask2", "Test subtask description2", TaskStatus.DONE, createdEpic.getId(),
                LocalDateTime.of(2025, Month.FEBRUARY, 16, 22, 30), 20);
        taskManager.createSubtask(subtask1);
        taskManager.createSubtask(subtask2);

        Epic epicFromStore2 = taskManager.getEpic(createdEpic.getId());

        assertEquals(TaskStatus.NEW, epicFromStore2.getStatus(), "После добавления подзадач статусы эпика не совпадают.");
    }

    @Test
    void changeEpicStatusInProgress() {
        Epic epic = new Epic("Test epic", "Test epic description", TaskStatus.NEW);
        final Epic createdEpic = taskManager.createEpic(epic);

        Epic epicFromStore1 = taskManager.getEpic(createdEpic.getId());

        assertEquals(TaskStatus.NEW, epicFromStore1.getStatus(), "После создания эпика статусы не совпадают.");

        Subtask subtask1 = new Subtask(
                "Test subtask1", "Test subtask description1", TaskStatus.NEW, createdEpic.getId(),
                LocalDateTime.of(2025, Month.FEBRUARY, 16, 22, 0), 30);
        Subtask subtask2 = new Subtask(
                "Test subtask2", "Test subtask description2", TaskStatus.IN_PROGRESS, createdEpic.getId(),
                LocalDateTime.of(2025, Month.FEBRUARY, 16, 22, 30), 20);
        taskManager.createSubtask(subtask1);
        taskManager.createSubtask(subtask2);

        Epic epicFromStore2 = taskManager.getEpic(createdEpic.getId());

        assertEquals(TaskStatus.IN_PROGRESS, epicFromStore2.getStatus(), "После добавления подзадач статусы эпика не совпадают.");
    }

    @Test
    void updateEpicTimeState() {
        Epic epic = new Epic("Test epic", "Test epic description", TaskStatus.NEW);
        final Epic createdEpic = taskManager.createEpic(epic);

        Epic epicFromStore1 = taskManager.getEpic(createdEpic.getId());

        assertNull(epicFromStore1.getStartTime(), "После создания эпика время начала эпиков не совпадают.");
        assertEquals(Duration.ZERO, epicFromStore1.getDuration(), "После создания эпика продолжительность эпиков не совпадают.");
        assertNull(epicFromStore1.getEndTime(), "После создания эпика время окончания эпиков не совпадают.");

        LocalDateTime startTimeMore = LocalDateTime.of(2025, Month.FEBRUARY, 16, 22, 30);
        Duration newDuration1 = Duration.ofMinutes(30);
        Subtask subtask = new Subtask(
                "Test subtask", "Test subtask description", TaskStatus.NEW,
                createdEpic.getId(), startTimeMore, newDuration1.toMinutes());
        taskManager.createSubtask(subtask);

        LocalDateTime startTimeLess = LocalDateTime.of(2025, Month.FEBRUARY, 16, 21, 30);
        Duration newDuration2 = Duration.ofMinutes(60);
        Subtask subtask2 = new Subtask(
                "Test subtask2", "Test subtask2 description", TaskStatus.NEW,
                createdEpic.getId(), startTimeLess, newDuration2.toMinutes());
        taskManager.createSubtask(subtask2);

        Epic epicFromStore2 = taskManager.getEpic(createdEpic.getId());

        Duration calculatedDuration = newDuration1.plus(newDuration2);
        LocalDateTime calculatedEndTime = startTimeMore.plus(calculatedDuration);

        assertEquals(startTimeLess, epicFromStore2.getStartTime(), "После добавления подзадач время начала эпиков не совпадают.");
        assertEquals(calculatedDuration, epicFromStore2.getDuration(), "После добавления подзадач продолжительность эпиков не совпадают.");
        assertEquals(calculatedEndTime, epicFromStore2.getEndTime(), "После добавления подзадач время окончания эпиков не совпадают.");
    }

    @Test
    void deleteAllTasks() {
        Task task1 = new Task(
                "Test deleteAllTasks 1", "Test deleteAllTasks description 1", TaskStatus.NEW,
                LocalDateTime.of(2025, Month.FEBRUARY, 16, 22, 0), 0);
        Task task2 = new Task(
                "Test deleteAllTasks 2", "Test deleteAllTasks description 2", TaskStatus.NEW,
                LocalDateTime.of(2025, Month.FEBRUARY, 16, 22, 0), 0);
        taskManager.createTask(task1);
        taskManager.createTask(task2);

        assertFalse(taskManager.getAllTasks().isEmpty(), "Задачи не были добавлены");

        taskManager.deleteAllTasks();

        assertTrue(taskManager.getAllTasks().isEmpty(), "Задачи не были удалены");
    }

    @Test
    void deleteAllEpics() {
        Epic epic1 = new Epic("Test deleteAllEpics 1", "Test deleteAllEpics description 1", TaskStatus.NEW);
        Epic epic2 = new Epic("Test deleteAllEpics 2", "Test deleteAllEpics description 2", TaskStatus.NEW);
        taskManager.createEpic(epic1);
        taskManager.createEpic(epic2);

        assertFalse(taskManager.getAllEpics().isEmpty(), "Эпики не были добавлены");

        taskManager.deleteAllEpics();

        assertTrue(taskManager.getAllEpics().isEmpty(), "Эпики не были удалены");
    }

    @Test
    void deleteAllSubtasks() {
        Epic epic1 = new Epic("Test deleteAllSubtasks 1", "Test deleteAllSubtasks description 1", TaskStatus.NEW);
        Epic epic2 = new Epic("Test deleteAllSubtasks 2", "Test deleteAllSubtasks description 2", TaskStatus.NEW);
        Epic createdEpic1 = taskManager.createEpic(epic1);
        Epic createdEpic2 = taskManager.createEpic(epic2);
        Subtask subtask1 = new Subtask(
                "Test deleteAllSubtasks 1", "Test deleteAllSubtasks description 1", TaskStatus.NEW, createdEpic1.getId(),
                LocalDateTime.of(2025, Month.FEBRUARY, 16, 22, 0), 0);
        Subtask subtask2 = new Subtask(
                "Test deleteAllSubtasks 2", "Test deleteAllSubtasks description 2", TaskStatus.NEW, createdEpic2.getId(),
                LocalDateTime.of(2025, Month.FEBRUARY, 16, 22, 0), 0);
        taskManager.createSubtask(subtask1);
        taskManager.createSubtask(subtask2);

        assertFalse(taskManager.getAllSubtasks().isEmpty(), "Подзадачи не были добавлены");

        taskManager.deleteAllSubtasks();

        assertTrue(taskManager.getAllSubtasks().isEmpty(), "Подзадачи не были удалены");
    }

    @Test
    void deleteTask() {
        Task task = new Task("Test deleteTask", "Test deleteTask description", TaskStatus.NEW);
        taskManager.createTask(task);

        assertEquals(1, taskManager.getAllTasks().size(), "Задача не была добавлена");

        taskManager.deleteTask(task.getId());

        assertTrue(taskManager.getAllTasks().isEmpty(), "Задача не была удалена");
    }

    @Test
    void deleteEpic() {
        Epic epic = new Epic("Test deleteEpic", "Test deleteEpic description", TaskStatus.NEW);
        taskManager.createEpic(epic);

        assertEquals(1, taskManager.getAllEpics().size(), "Эпик не был добавлен");

        taskManager.deleteEpic(epic.getId());

        assertTrue(taskManager.getAllEpics().isEmpty(), "Эпик не был удален");
    }

    @Test
    void deleteSubtask() {
        Epic epic = new Epic("Test deleteSubtask", "Test deleteSubtask description", TaskStatus.NEW);
        Epic createdEpic = taskManager.createEpic(epic);
        Subtask subtask = new Subtask(
                "Test deleteSubtask 1", "Test deleteSubtask description 1", TaskStatus.NEW, createdEpic.getId(),
                LocalDateTime.of(2025, Month.FEBRUARY, 16, 22, 0), 0);
        taskManager.createSubtask(subtask);

        assertEquals(1, taskManager.getAllSubtasks().size(), "Подзадача не была добавлена");

        taskManager.deleteSubtask(subtask.getId());

        assertTrue(taskManager.getAllSubtasks().isEmpty(), "Подзадача не была удалена");
        assertTrue(epic.getSubtaskIds().isEmpty(), "ID подзадачи внутри эпика не был удален");
    }

    @Test
    void getPrioritizedTasks() {
        Task task1 = new Task(
                "Test prioritized task 1", "Test prioritized task description 1", TaskStatus.NEW,
                LocalDateTime.of(2025, Month.FEBRUARY, 16, 22, 0), 0);
        Task task2 = new Task(
                "Test prioritized task 2", "Test prioritized task description 2", TaskStatus.NEW,
                LocalDateTime.of(2025, Month.FEBRUARY, 16, 21, 10), 0);
        taskManager.createTask(task1);
        taskManager.createTask(task2);

        Epic epic1 = new Epic("Test prioritized epic 1", "Test prioritized epic description 1", TaskStatus.NEW);
        Epic epic2 = new Epic("Test prioritized epic 2", "Test prioritized epic description 2", TaskStatus.NEW);
        Epic createdEpic1 = taskManager.createEpic(epic1);
        Epic createdEpic2 = taskManager.createEpic(epic2);
        Subtask subtask1 = new Subtask(
                "Test prioritized subtask 1", "Test prioritized subtask description 1", TaskStatus.NEW, createdEpic1.getId(),
                LocalDateTime.of(2025, Month.FEBRUARY, 16, 22, 10), 0);
        Subtask subtask2 = new Subtask(
                "Test prioritized subtask 2", "Test prioritized subtask description 2", TaskStatus.NEW, createdEpic2.getId(),
                LocalDateTime.of(2025, Month.FEBRUARY, 16, 21, 0), 0);
        taskManager.createSubtask(subtask1);
        taskManager.createSubtask(subtask2);

        Set<Task> expectedPrioritizedTasks = new LinkedHashSet<>(Arrays.asList(subtask2, task2, task1, subtask1));

        assertIterableEquals(expectedPrioritizedTasks, taskManager.getPrioritizedTasks(), "Задачи были остортированы неверно");
    }

    @Test
    void removePrioritizedTask() {
        Task task = new Task(
                "Test prioritized task 1", "Test prioritized task description 1", TaskStatus.NEW,
                LocalDateTime.of(2025, Month.FEBRUARY, 16, 22, 0), 0);
        taskManager.createTask(task);

        Epic epic = new Epic("Test prioritized epic 1", "Test prioritized epic description 1", TaskStatus.NEW);
        Epic createdEpic = taskManager.createEpic(epic);
        Subtask subtask = new Subtask(
                "Test prioritized subtask 1", "Test prioritized subtask description 1", TaskStatus.NEW, createdEpic.getId(),
                LocalDateTime.of(2025, Month.FEBRUARY, 16, 22, 10), 0);
        taskManager.createSubtask(subtask);

        assertIterableEquals(new LinkedHashSet<>(Arrays.asList(task, subtask)), taskManager.getPrioritizedTasks(), "Задачи были остортированы неверно");

        taskManager.deleteTask(task.getId());

        assertIterableEquals(Set.of(subtask), taskManager.getPrioritizedTasks(), "Приоритизированная задача не была удалена");
    }
}