package ru.yandex.practicum.test;

import ru.yandex.practicum.task.*;
import ru.yandex.practicum.task.interfaces.TaskManager;

public class Main {

    public static void main(String[] args) {
        TaskManager taskManager = Managers.getDefault();

        Task task1 = new Task("Task 1", "Description for Task 1", TaskStatus.NEW);
        Task task2 = new Task("Task 2", "Description for Task 2", TaskStatus.NEW);

        Task createdTask1 = taskManager.createTask(task1);
        Task createdTask2 = taskManager.createTask(task2);

        Epic epic1 = new Epic("Epic with 2 subtasks", "Description for Epic with 2 subtasks", TaskStatus.NEW);
        Epic createdEpic1 = taskManager.createEpic(epic1);

        Subtask subtask1 = new Subtask("Subtask 1", "Description for Subtask 1", TaskStatus.NEW, createdEpic1.getId());
        Subtask subtask2 = new Subtask("Subtask 2", "Description for Subtask 2", TaskStatus.NEW, createdEpic1.getId());

        Subtask createdSubtask1 = taskManager.createSubtask(subtask1);
        Subtask createdSubtask2 = taskManager.createSubtask(subtask2);

        Epic epic2 = new Epic("Epic with one Subtask", "Description for Epic with one Subtask", TaskStatus.NEW);
        Epic createdEpic2 = taskManager.createEpic(epic2);

        Subtask subtask3 = new Subtask("Subtask 3", "Description for Subtask 3", TaskStatus.NEW, createdEpic2.getId());
        Subtask createdSubtask3 = taskManager.createSubtask(subtask3);

        System.out.println("All tasks > " + taskManager.getAllTasks());
        System.out.println("All epics > " + taskManager.getAllEpics());
        System.out.println("All subtasks > " + taskManager.getAllSubtasks());

        createdTask1.setStatus(TaskStatus.IN_PROGRESS);
        createdTask2.setStatus(TaskStatus.DONE);
        taskManager.updateTask(createdTask1);
        taskManager.updateTask(createdTask2);

        System.out.println("-----------  Update tasks -------------");

        createdSubtask1.setStatus(TaskStatus.IN_PROGRESS);
        createdSubtask2.setStatus(TaskStatus.DONE);
        createdSubtask3.setStatus(TaskStatus.DONE);
        taskManager.updateSubtask(createdSubtask1);
        taskManager.updateSubtask(createdSubtask2);
        taskManager.updateSubtask(createdSubtask3);

        System.out.println("-----------  Update subtasks -------------");

        epic1.setStatus(TaskStatus.DONE);
        taskManager.updateEpic(epic1);

        System.out.println("-----------  Update epic -------------");

        System.out.println("All tasks " + taskManager.getAllTasks());
        System.out.println("All epics " + taskManager.getAllEpics());
        System.out.println("All subtasks " + taskManager.getAllSubtasks());

        taskManager.deleteTaskById(createdTask2.getId());
        taskManager.deleteSubtaskById(createdSubtask1.getId());
        taskManager.deleteEpicById(createdEpic2.getId());

        System.out.println("-----------  Delete task -------------");
        System.out.println("-----------  Delete subtask -------------");
        System.out.println("-----------  Delete epic -------------");

        System.out.println("All tasks " + taskManager.getAllTasks());
        System.out.println("All epics " + taskManager.getAllEpics());
        System.out.println("All subtasks " + taskManager.getAllSubtasks());

        taskManager.deleteSubtaskById(createdSubtask2.getId());

        System.out.println("-----------  Delete subtask -------------");

        System.out.println("All tasks " + taskManager.getAllTasks());
        System.out.println("All epics " + taskManager.getAllEpics());
        System.out.println("All subtasks " + taskManager.getAllSubtasks());
    }
}
