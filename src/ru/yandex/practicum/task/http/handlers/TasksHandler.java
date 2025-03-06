package ru.yandex.practicum.task.http.handlers;

import com.google.gson.JsonSyntaxException;
import com.sun.net.httpserver.HttpExchange;
import ru.yandex.practicum.task.error.NotFoundException;
import ru.yandex.practicum.task.error.TimeIntersectedException;
import ru.yandex.practicum.task.interfaces.TaskManager;
import ru.yandex.practicum.task.tasks.Task;

import java.util.Optional;

public class TasksHandler extends BaseHttpHandler {

    public TasksHandler(TaskManager taskManager) {
        super(taskManager, "tasks");
    }

    @Override
    protected void handleGetItems(HttpExchange exchange) {
        sendResponse(exchange, taskManager.getAllTasks());
    }

    @Override
    protected void handleGetItemById(HttpExchange exchange) {
        try {
            Task task = taskManager.getTask(getId(exchange));
            sendResponse(exchange, task);
        } catch (NotFoundException e) {
            sendNotFound(exchange, e.getMessage());
        }
    }

    @Override
    protected void handlePostItem(HttpExchange exchange) {
        Optional<String> bodyStr = getBody(exchange);
        try {
            if (bodyStr.get().isBlank()) {
                throw new JsonSyntaxException("Тело запроса не может быть пустым");
            }
            Task task = gson.fromJson(bodyStr.get(), Task.class);
            try {
                if (task.getId() == null) {
                    taskManager.createTask(task);
                } else {
                    taskManager.updateTask(task);
                }
                sendSuccess(exchange);
            } catch (TimeIntersectedException e) {
                sendHasInteractions(exchange, e.getMessage());
            }
        } catch (NullPointerException | JsonSyntaxException e) {
            sendBadRequest(exchange, "Неверное тело запроса");
        } catch (Exception e) {
            sendServerError(exchange);
        }
    }

    @Override
    protected void handleDeleteItem(HttpExchange exchange) {
        try {
            int id = getId(exchange);
            taskManager.deleteTask(id);
            sendSuccess(exchange);
        } catch (NotFoundException e) {
            sendNotFound(exchange, e.getMessage());
        }
    }

}
