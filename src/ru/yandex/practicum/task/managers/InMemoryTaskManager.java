package ru.yandex.practicum.task.managers;

import ru.yandex.practicum.task.interfaces.HistoryManager;
import ru.yandex.practicum.task.interfaces.TaskManager;
import ru.yandex.practicum.task.tasks.Epic;
import ru.yandex.practicum.task.tasks.Subtask;
import ru.yandex.practicum.task.tasks.Task;

import java.util.*;

public class InMemoryTaskManager implements TaskManager {
    private final Map<Integer, Task> tasksMap = new HashMap<>();
    private final Map<Integer, Epic> epicsMap = new HashMap<>();
    private final Map<Integer, Subtask> subtasksMap = new HashMap<>();
    private final HistoryManager historyManager = Managers.getDefaultHistory();
    private int lastTaskId = 0;

    @Override
    public Task createTask(Task task) {
        task.setId(++lastTaskId);

        Task newTask = new Task(task.getName(), task.getDescription(), task.getStatus());
        newTask.setId(task.getId());
        tasksMap.put(newTask.getId(), newTask);

        return task;
    }

    @Override
    public Epic createEpic(Epic epic) {
        epic.setId(++lastTaskId);

        Epic newEpic = new Epic(epic.getName(), epic.getDescription(), epic.getStatus());
        newEpic.setId(epic.getId());
        epicsMap.put(newEpic.getId(), newEpic);

        return epic;
    }

    @Override
    public Subtask createSubtask(Subtask subtask) {
        subtask.setId(++lastTaskId);

        Subtask newSubtask = new Subtask(subtask.getName(), subtask.getDescription(), subtask.getStatus(), subtask.getEpicId());
        newSubtask.setId(subtask.getId());
        subtasksMap.put(newSubtask.getId(), newSubtask);

        Epic epic = epicsMap.get(subtask.getEpicId());
        epic.addSubtaskId(newSubtask.getId());

        List<Subtask> subtasksOfEpic = getSubtasksByEpic(epic);
        epic.calculateAndSetStatus(subtasksOfEpic);

        return subtask;
    }

    @Override
    public Task updateTask(Task task) {
        return tasksMap.put(task.getId(), task);
    }

    @Override
    public Epic updateEpic(Epic epic) {
        return epicsMap.put(epic.getId(), epic);
    }

    @Override
    public Subtask updateSubtask(Subtask subtask) {
        Subtask updatedSubtask = null;
        Epic epic = epicsMap.get(subtask.getEpicId());
        if (epic != null) {
            updatedSubtask = subtasksMap.put(subtask.getId(), subtask);
            List<Subtask> subtasksOfEpic = getSubtasksByEpic(epic);
            epic.calculateAndSetStatus(subtasksOfEpic);
        }
        return updatedSubtask;
    }

    @Override
    public List<Task> getAllTasks() {
        return new ArrayList<>(tasksMap.values());
    }

    @Override
    public List<Epic> getAllEpics() {
        return new ArrayList<>(epicsMap.values());
    }

    @Override
    public List<Subtask> getAllSubtasks() {
        return new ArrayList<>(subtasksMap.values());
    }

    @Override
    public Task getTask(int id) {
        Task task = tasksMap.get(id);
        if (task != null) {
            historyManager.add(task);
        }
        return task;
    }

    @Override
    public Epic getEpic(int id) {
        Epic epic = epicsMap.get(id);
        if (epic != null) {
            historyManager.add(epic);
        }
        return epic;
    }

    @Override
    public Subtask getSubtask(int id) {
        Subtask subtask = subtasksMap.get(id);
        if (subtask != null) {
            historyManager.add(subtask);
        }
        return subtask;
    }

    @Override
    public void deleteAllTasks() {
        tasksMap.clear();
    }

    @Override
    public void deleteAllEpics() {
        epicsMap.clear();
    }

    @Override
    public void deleteAllSubtasks() {
        subtasksMap.clear();
    }

    @Override
    public Task deleteTask(int id) {
        return tasksMap.remove(id);
    }

    @Override
    public Epic deleteEpic(int id) {
        Epic removedEpic = epicsMap.remove(id);
        if (removedEpic != null) {
            for (Integer subtaskId : removedEpic.getSubtaskIds()) {
                subtasksMap.remove(subtaskId);
            }
        }
        return removedEpic;
    }

    @Override
    public Subtask deleteSubtask(int id) {
        Subtask removedSubtask = subtasksMap.remove(id);
        if (removedSubtask != null) {
            Epic epic = epicsMap.get(removedSubtask.getEpicId());
            epic.removeSubtaskId(removedSubtask.getId());
            if (epic.getSubtaskIds().isEmpty()) {
                epicsMap.remove(epic.getId());
            } else {
                List<Subtask> subtasksOfEpic = getSubtasksByEpic(epic);
                epic.calculateAndSetStatus(subtasksOfEpic);
            }
        }
        return removedSubtask;
    }

    @Override
    public List<Task> getHistory() {
        return historyManager.getHistory();
    }

    private List<Subtask> getSubtasksByEpic(Epic epic) {
        List<Subtask> subtasksOfEpic = new ArrayList<>();
        for (Integer subtaskId : epic.getSubtaskIds()) {
            subtasksOfEpic.add(subtasksMap.get(subtaskId));
        }
        return subtasksOfEpic;
    }

}
