package ru.yandex.practicum.task.http.handlers;

import com.sun.net.httpserver.HttpExchange;
import ru.yandex.practicum.task.error.NotFoundException;
import ru.yandex.practicum.task.error.TimeIntersectedException;
import ru.yandex.practicum.task.interfaces.TaskManager;
import ru.yandex.practicum.task.tasks.Subtask;

public class SubtasksHandler extends BaseHttpHandler {

    public SubtasksHandler(TaskManager taskManager) {
        super(taskManager, "subtasks");
    }

    @Override
    protected void handleGetItems(HttpExchange exchange) {
        sendResponse(exchange, taskManager.getAllSubtasks());
    }

    @Override
    protected void handleGetItemById(HttpExchange exchange) {
        try {
            Subtask subtask = taskManager.getSubtask(getId(exchange));
            sendResponse(exchange, subtask);
        } catch (NotFoundException e) {
            sendNotFound(exchange, e.getMessage());
        }
    }

    @Override
    protected void handlePostItem(HttpExchange exchange) {
        handlePostItemByConsumer(exchange, bodyStr -> {
            Subtask subtask = gson.fromJson(bodyStr, Subtask.class);
            try {
                if (subtask.getId() == null) {
                    taskManager.createSubtask(subtask);
                } else {
                    taskManager.updateSubtask(subtask);
                }
                sendSuccess(exchange);
            } catch (TimeIntersectedException e) {
                sendHasInteractions(exchange, e.getMessage());
            }
        });
    }

    @Override
    protected void handleDeleteItem(HttpExchange exchange) {
        try {
            int id = getId(exchange);
            taskManager.deleteSubtask(id);
            sendSuccess(exchange);
        } catch (NotFoundException e) {
            sendNotFound(exchange, e.getMessage());
        }
    }

}
