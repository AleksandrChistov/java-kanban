package ru.yandex.practicum;

import java.util.*;

public class TaskManager {
    private final Map<Integer, Task> tasksMap = new HashMap<>();
    private final Map<Integer, Epic> epicsMap = new HashMap<>();
    private final Map<Integer, Subtask> subtasksMap = new HashMap<>();
    private int lastTaskId = 0;

    public static void main(String[] args) {
        System.out.println("Добро пожаловать в Трекер задач");
    }

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
        epic.addSubtask(subtask);

        return subtask;
    }

    public Task updateTask(Task task) {
        return tasksMap.put(task.getId(), task);
    }

    public Epic updateEpic(Epic epic) {
        return epicsMap.put(epic.getId(), epic);
    }

    public Subtask updateSubtask(Subtask subtask) {
        Epic epic = epicsMap.get(subtask.getEpicId());
        if (epic != null) {
            epic.updateSubtask(subtask);
        }
        return subtasksMap.put(subtask.getId(), subtask);
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
        if (epicsMap.containsKey(id)) {
            for (Subtask subtask : epicsMap.get(id).getSubtasks()) {
                subtasksMap.remove(subtask.getId());
            }
        }
        return epicsMap.remove(id);
    }

    public Subtask deleteSubtaskById(int id) {
        Subtask subtask = subtasksMap.get(id);
        if (subtask != null) {
            Epic epic = epicsMap.get(subtask.getEpicId());
            epic.removeSubtask(subtask);
            if (epic.getSubtasks().isEmpty()) {
                epicsMap.remove(epic.getId());
            }
        }
        return subtasksMap.remove(id);
    }

}
