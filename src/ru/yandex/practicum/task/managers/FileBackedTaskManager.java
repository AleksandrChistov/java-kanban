package ru.yandex.practicum.task.managers;

import ru.yandex.practicum.task.enums.TaskStatus;
import ru.yandex.practicum.task.enums.TaskType;
import ru.yandex.practicum.task.error.ManagerSaveException;
import ru.yandex.practicum.task.tasks.Epic;
import ru.yandex.practicum.task.tasks.Subtask;
import ru.yandex.practicum.task.tasks.Task;
import ru.yandex.practicum.task.utils.DateTimeTaskUtil;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.Objects;
import java.util.TreeMap;

/**
 * Класс {@code FileBackedTaskManager} расширяет {@link InMemoryTaskManager},
 * добавляя функциональность сохранения данных о задачах в файл и восстановления данных из файла.
 * <p>
 * При каждом изменении состояния задач (создание, обновление, удаление) данные автоматически сохраняются в файл.
 */
public class FileBackedTaskManager extends InMemoryTaskManager {
    /**
     * Файл, в котором сохраняются данные о задачах.
     */
    private final File file;

    public FileBackedTaskManager(File file) {
        this.file = file;
    }

    @Override
    public Task createTask(Task task) {
        Task createdTask = super.createTask(task);
        save();
        return createdTask;
    }

    @Override
    public Epic createEpic(Epic epic) {
        Epic createdEpic = super.createEpic(epic);
        save();
        return createdEpic;
    }

    @Override
    public Subtask createSubtask(Subtask subtask) {
        Subtask createdSubtask = super.createSubtask(subtask);
        save();
        return createdSubtask;
    }

    @Override
    public Task updateTask(Task task) {
        Task prevTask = super.updateTask(task);
        save();
        return prevTask;
    }

    @Override
    public Epic updateEpic(Epic epic) {
        Epic prevEpic = super.updateEpic(epic);
        save();
        return prevEpic;
    }

    @Override
    public Subtask updateSubtask(Subtask subtask) {
        Subtask prevSubtask = super.updateSubtask(subtask);
        save();
        return prevSubtask;
    }

    @Override
    public void deleteAllTasks() {
        super.deleteAllTasks();
        save();
    }

    @Override
    public void deleteAllEpics() {
        super.deleteAllEpics();
        save();
    }

    @Override
    public void deleteAllSubtasks() {
        super.deleteAllSubtasks();
        save();
    }

    @Override
    public Task deleteTask(int id) {
        Task removedTask = super.deleteTask(id);
        save();
        return removedTask;
    }

    @Override
    public Epic deleteEpic(int id) {
        Epic removedEpic = super.deleteEpic(id);
        save();
        return removedEpic;
    }

    @Override
    public Subtask deleteSubtask(int id) {
        Subtask removedSubtask = super.deleteSubtask(id);
        save();
        return removedSubtask;
    }

    /**
     * Загружает данные о задачах из файла.
     * @param file Файл, из которого необходимо загрузить данные.
     * @return Новый {@code FileBackedTaskManager}, содержащий загруженные данные.
     */
    public static FileBackedTaskManager loadFromFile(File file) {
        FileBackedTaskManager manager = new FileBackedTaskManager(file);
        try {
            String content = Files.readString(file.toPath());

            if (content.isBlank()) {
                return manager;
            }

            String[] lines = content.split("\n");

            for (String line : lines) {
                if (line.startsWith("id")) {
                    continue;
                }
                Task task = fromString(line);

                if (task instanceof Epic) {
                    Epic newEpic = new Epic(task.getName(), task.getDescription(), task.getStatus());
                    newEpic.setId(++manager.lastTaskId);
                    manager.epicsMap.put(newEpic.getId(), newEpic);
                } else if (task instanceof Subtask) {
                    Subtask newSubtask = new Subtask(
                            task.getName(), task.getDescription(), task.getStatus(),
                            ((Subtask) task).getEpicId(), task.getStartTime(), task.getDuration().toMinutes());
                    newSubtask.setId(manager.lastTaskId++);
                    manager.subtasksMap.put(newSubtask.getId(), newSubtask);

                    Epic epic = manager.epicsMap.get(((Subtask) task).getEpicId());
                    epic.addSubtaskId(newSubtask.getId());
                    epic.calculateState(manager.getSubtasksByEpic(epic));
                } else {
                    Task newTask = new Task(
                            task.getName(), task.getDescription(), task.getStatus(),
                            task.getStartTime(), task.getDuration().toMinutes());
                    newTask.setId(manager.lastTaskId++);
                    manager.tasksMap.put(newTask.getId(), newTask);
                }
            }
        } catch (IOException e) {
            System.out.println("Ошибка парсинга файла");
        }

        return manager;
    }

    /**
     * Сохраняет текущее состояние задач в файл.
     * <p>
     * Сохраняет задачи, эпики и подзадачи в формате CSV.
     * @throws ManagerSaveException Если произошла ошибка при сохранении данных в файл.
     */
    protected void save() {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(file, StandardCharsets.UTF_8))) {
            Map<Integer, Task> sortedTaskMap = new TreeMap<>(tasksMap);
            sortedTaskMap.putAll(epicsMap);
            sortedTaskMap.putAll(subtasksMap);

            if (sortedTaskMap.isEmpty()) {
                bw.write("");
            } else {
                String header = "id,type,name,status,description,startTime,duration,epic\n";
                bw.write(header);
            }

            for (Task task : sortedTaskMap.values()) {
                bw.write(task + "\n");
            }
        } catch (IOException e) {
            throw new ManagerSaveException("Ошибка сохранения задачи в файл");
        }
    }

    /**
     * Создает задачу из строки в формате CSV.
     * @param value Строка, содержащая данные о задаче в формате CSV.
     * @return Созданная задача.
     */
    private static Task fromString(String value) {
        String[] parts = value.split(",");

        if (TaskType.EPIC.name().equals(parts[1])) {
            return new Epic(parts[2], parts[4], TaskStatus.valueOf(parts[3]));
        }

        LocalDateTime startTime = Objects.equals(parts[5], "") ? null : DateTimeTaskUtil.parse(parts[5]);
        long duration = Objects.equals(parts[6], "") ? 0 : Long.parseLong(parts[6]);

        if (TaskType.SUBTASK.name().equals(parts[1])) {
            return new Subtask(parts[2], parts[4], TaskStatus.valueOf(parts[3]), Integer.parseInt(parts[7]), startTime, duration);
        }

        return new Task(parts[2], parts[4], TaskStatus.valueOf(parts[3]), startTime, duration);
    }

}
