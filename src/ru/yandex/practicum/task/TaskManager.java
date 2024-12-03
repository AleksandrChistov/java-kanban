package ru.yandex.practicum.task;

import java.util.*;

public class TaskManager {
    private final Map<Integer, Task> tasksMap = new HashMap<>();
    private final Map<Integer, Epic> epicsMap = new HashMap<>();
    private final Map<Integer, Subtask> subtasksMap = new HashMap<>();
    private int lastTaskId = 0;

    public Task createTask(Task task) {
        task.setId(++lastTaskId);
        tasksMap.put(task.getId(), task);
        return task;
    }

    public Epic createEpic(Epic epic) {
        epic.setId(++lastTaskId);
        epicsMap.put(epic.getId(), epic);
        return epic;
    }

    public Subtask createSubtask(Subtask subtask) {
        subtask.setId(++lastTaskId);
        subtasksMap.put(subtask.getId(), subtask);

        Epic epic = epicsMap.get(subtask.getEpicId());
        epic.addSubtaskId(subtask.getId());

        List<Subtask> subtasksOfEpic = getSubtasksByEpic(epic);
        epic.calculateAndSetStatus(subtasksOfEpic);

        return subtask;
    }

    public Task updateTask(Task task) {
        return tasksMap.put(task.getId(), task);
    }

    public Epic updateEpic(Epic epic) {
        return epicsMap.put(epic.getId(), epic);
    }

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

    public List<Task> getAllTasks() {
        return new ArrayList<>(tasksMap.values());
    }

    public List<Epic> getAllEpics() {
        return new ArrayList<>(epicsMap.values());
    }

    public List<Subtask> getAllSubtasks() {
        return new ArrayList<>(subtasksMap.values());
    }

    public Task getTaskById(int id) {
        return tasksMap.get(id);
    }

    public Epic getEpicById(int id) {
        return epicsMap.get(id);
    }

    public Subtask getSubtaskById(int id) {
        return subtasksMap.get(id);
    }

    public void deleteAllTasks() {
        tasksMap.clear();
    }

    public void deleteAllEpics() {
        epicsMap.clear();
    }

    public void deleteAllSubtasks() {
        subtasksMap.clear();
    }

    public Task deleteTaskById(int id) {
        return tasksMap.remove(id);
    }

    public Epic deleteEpicById(int id) {
        Epic removedEpic = epicsMap.remove(id);
        if (removedEpic != null) {
            for (Integer subtaskId : removedEpic.getSubtaskIds()) {
                subtasksMap.remove(subtaskId);
            }
        }
        return removedEpic;
    }

    public Subtask deleteSubtaskById(int id) {
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

    private List<Subtask> getSubtasksByEpic(Epic epic) {
        List<Subtask> subtasksOfEpic = new ArrayList<>();
        for (Integer subtaskId : epic.getSubtaskIds()) {
            subtasksOfEpic.add(subtasksMap.get(subtaskId));
        }
        return subtasksOfEpic;
    }

}
