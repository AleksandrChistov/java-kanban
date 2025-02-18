package ru.yandex.practicum.task.managers;

import ru.yandex.practicum.task.interfaces.HistoryManager;
import ru.yandex.practicum.task.interfaces.TaskManager;
import ru.yandex.practicum.task.tasks.Epic;
import ru.yandex.practicum.task.tasks.Subtask;
import ru.yandex.practicum.task.tasks.Task;
import ru.yandex.practicum.task.utils.TaskManagerUtil;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Stream;

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
            return null;
        }

        task.setId(++lastTaskId);
        Task newTask = TaskManagerUtil.getNewTask(task);
        tasksMap.put(newTask.getId(), newTask);

        if (newTask.getStartTime() != null) {
            prioritizedTasks.add(newTask);
        }

        return task;
    }

    @Override
    public Epic createEpic(Epic epic) {
        epic.setId(++lastTaskId);
        Epic newEpic = TaskManagerUtil.getNewTask(epic);
        epicsMap.put(newEpic.getId(), newEpic);
        return epic;
    }

    @Override
    public Subtask createSubtask(Subtask subtask) {
        if (isTimeIntersected(subtask)) {
            return null;
        }

        subtask.setId(++lastTaskId);
        Subtask newSubtask = TaskManagerUtil.getNewTask(subtask);
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
            return null;
        }
        updateOrRemovePrioritizedTask(task);

        Task newTask = TaskManagerUtil.getNewTask(task);
        tasksMap.put(newTask.getId(), newTask);

        return task;
    }

    @Override
    public Epic updateEpic(Epic epic) {
        Epic newEpic = TaskManagerUtil.getNewTask(epic);

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
            return null;
        }
        Subtask updatedSubtask = null;
        Epic epic = epicsMap.get(subtask.getEpicId());
        if (epic != null) {
            updateOrRemovePrioritizedTask(subtask);

            Subtask newSubtask = TaskManagerUtil.getNewTask(subtask);
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
        return tasksMap.remove(id);
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

    protected List<Subtask> getSubtasksByEpic(Epic epic) {
        List<Subtask> subtasksOfEpic = new ArrayList<>();
        for (Integer subtaskId : epic.getSubtaskIds()) {
            subtasksOfEpic.add(subtasksMap.get(subtaskId));
        }
        return subtasksOfEpic;
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
        return TaskManagerUtil.isTimeIntersected(task, tasksMap.values(), subtasksMap.values());
    }

}
