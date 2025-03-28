package ru.yandex.practicum.task.managers;

import ru.yandex.practicum.task.error.NotFoundException;
import ru.yandex.practicum.task.error.TimeIntersectedException;
import ru.yandex.practicum.task.interfaces.HistoryManager;
import ru.yandex.practicum.task.interfaces.TaskManager;
import ru.yandex.practicum.task.tasks.Epic;
import ru.yandex.practicum.task.tasks.Subtask;
import ru.yandex.practicum.task.tasks.Task;
import ru.yandex.practicum.task.utils.TaskManagerUtil;

import java.util.*;
import java.util.stream.Collectors;

public class InMemoryTaskManager implements TaskManager {
    protected final Map<Integer, Task> tasksMap = new HashMap<>();
    protected final Map<Integer, Epic> epicsMap = new HashMap<>();
    protected final Map<Integer, Subtask> subtasksMap = new HashMap<>();
    protected final Set<Task> prioritizedTasks = new TreeSet<>(Comparator.comparing(Task::getStartTime));
    protected int lastTaskId = 0;
    private final HistoryManager historyManager = Managers.getDefaultHistory();

    @Override
    public Task createTask(Task task) {
        if (isTimeIntersected(task)) {
            throw new TimeIntersectedException(task.getId());
        }

        task.setId(++lastTaskId);
        Task newTask = TaskManagerUtil.getCopyTask(task);
        tasksMap.put(newTask.getId(), newTask);

        if (newTask.getStartTime() != null) {
            prioritizedTasks.add(newTask);
        }

        return task;
    }

    @Override
    public Epic createEpic(Epic epic) {
        epic.setId(++lastTaskId);
        Epic newEpic = TaskManagerUtil.getCopyTask(epic);
        epicsMap.put(newEpic.getId(), newEpic);
        return epic;
    }

    @Override
    public Subtask createSubtask(Subtask subtask) {
        if (isTimeIntersected(subtask)) {
            throw new TimeIntersectedException(subtask.getId());
        }

        subtask.setId(++lastTaskId);
        Subtask newSubtask = TaskManagerUtil.getCopyTask(subtask);
        subtasksMap.put(newSubtask.getId(), newSubtask);

        if (newSubtask.getStartTime() != null) {
            prioritizedTasks.add(newSubtask);
        }

        Epic epic = epicsMap.get(subtask.getEpicId());
        epic.addSubtaskId(newSubtask.getId());
        epic.calculateState(getSubtasksByEpic(epic));

        return subtask;
    }

    @Override
    public Task updateTask(Task task) {
        if (isTimeIntersected(task)) {
            throw new TimeIntersectedException(task.getId());
        }
        updateOrRemovePrioritizedTask(task);

        Task newTask = TaskManagerUtil.getCopyTask(task);
        tasksMap.put(newTask.getId(), newTask);

        return task;
    }

    @Override
    public Epic updateEpic(Epic epic) {
        Epic newEpic = TaskManagerUtil.getCopyTask(epic);

        subtasksMap.values().stream()
                .filter(t -> t.getEpicId() == newEpic.getId())
                .forEach(t -> {
                    epic.addSubtaskId(t.getId());
                    newEpic.addSubtaskId(t.getId());
                });

        List<Subtask> subtasks = getSubtasksByEpic(newEpic);
        epic.calculateState(subtasks);
        newEpic.calculateState(subtasks);

        epicsMap.put(newEpic.getId(), newEpic);

        return epic;
    }

    @Override
    public Subtask updateSubtask(Subtask subtask) {
        if (isTimeIntersected(subtask)) {
            throw new TimeIntersectedException(subtask.getId());
        }
        Subtask updatedSubtask = null;
        Epic epic = epicsMap.get(subtask.getEpicId());
        if (epic != null) {
            updateOrRemovePrioritizedTask(subtask);

            Subtask newSubtask = TaskManagerUtil.getCopyTask(subtask);
            subtasksMap.put(newSubtask.getId(), newSubtask);
            updatedSubtask = subtask;

            epic.calculateState(getSubtasksByEpic(epic));
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
        if (task == null) {
            throw new NotFoundException(String.valueOf(id));
        }
        historyManager.add(task);
        return task;
    }

    @Override
    public Epic getEpic(int id) {
        Epic epic = epicsMap.get(id);
        if (epic == null) {
            throw new NotFoundException(String.valueOf(id));
        }
        historyManager.add(epic);
        return epic;
    }

    @Override
    public Subtask getSubtask(int id) {
        Subtask subtask = subtasksMap.get(id);
        if (subtask == null) {
            throw new NotFoundException(String.valueOf(id));
        }
        historyManager.add(subtask);
        return subtask;
    }

    @Override
    public void deleteAllTasks() {
        prioritizedTasks.removeAll(tasksMap.values());
        tasksMap.clear();
    }

    @Override
    public void deleteAllEpics() {
        deleteAllSubtasks();
        epicsMap.clear();
    }

    @Override
    public void deleteAllSubtasks() {
        prioritizedTasks.removeAll(subtasksMap.values());
        subtasksMap.clear();
    }

    @Override
    public Task deleteTask(int id) {
        prioritizedTasks.removeIf(t -> t.getId() == id);
        historyManager.remove(id);
        Task removed = tasksMap.remove(id);

        if (removed == null) {
            throw new NotFoundException(String.valueOf(id));
        }

        return removed;
    }

    @Override
    public Epic deleteEpic(int id) {
        Epic removedEpic = epicsMap.remove(id);

        if (removedEpic != null) {
            historyManager.remove(id);
            for (Integer subtaskId : removedEpic.getSubtaskIds()) {
                prioritizedTasks.removeIf(t -> subtaskId.equals(t.getId()));
                historyManager.remove(subtaskId);
                subtasksMap.remove(subtaskId);
            }
        } else {
            throw new NotFoundException(String.valueOf(id));
        }

        return removedEpic;
    }

    @Override
    public Subtask deleteSubtask(int id) {
        Subtask removedSubtask = subtasksMap.remove(id);

        if (removedSubtask != null) {
            prioritizedTasks.removeIf(t -> t.getId() == id);
            historyManager.remove(id);
            Epic epic = epicsMap.get(removedSubtask.getEpicId());
            epic.removeSubtaskId(removedSubtask.getId());
            if (epic.getSubtaskIds().isEmpty()) {
                historyManager.remove(epic.getId());
                epicsMap.remove(epic.getId());
            } else {
                epic.calculateState(getSubtasksByEpic(epic));
            }
        } else {
            throw new NotFoundException(String.valueOf(id));
        }

        return removedSubtask;
    }

    @Override
    public List<Task> getHistory() {
        return historyManager.getHistory();
    }

    @Override
    public Set<Task> getPrioritizedTasks() {
        return prioritizedTasks;
    }

    @Override
    public List<Subtask> getSubtasksByEpic(Epic epic) {
        return epic.getSubtaskIds().stream()
                .map(subtasksMap::get)
                .collect(Collectors.toList());
    }

    private void updateOrRemovePrioritizedTask(Task task) {
        if (task.getStartTime() == null) {
            prioritizedTasks.remove(task);
        } else {
            prioritizedTasks.stream()
                    .filter(t -> t.equals(task))
                    .forEach(t -> {
                        t.setName(task.getName());
                        t.setDescription(task.getDescription());
                        t.setStatus(task.getStatus());
                        t.setStartTime(task.getStartTime());
                        t.setDuration(task.getDuration());
                    });
        }
    }

    private boolean isTimeIntersected(Task task) {
        return TaskManagerUtil.isTimeIntersected(task, prioritizedTasks);
    }

}
