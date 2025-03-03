package ru.yandex.practicum.task.http.handles;

import com.google.gson.JsonSyntaxException;
import com.sun.net.httpserver.HttpExchange;
import ru.yandex.practicum.task.error.NotFoundException;
import ru.yandex.practicum.task.error.TimeIntersectedException;
import ru.yandex.practicum.task.interfaces.TaskManager;
import ru.yandex.practicum.task.tasks.Subtask;

import java.util.Optional;

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
        Optional<String> bodyStr = getBody(exchange);
        try {
            if (bodyStr.get().isBlank()) {
                throw new JsonSyntaxException("Тело запроса не может быть пустым");
            }
            Subtask subtask = gson.fromJson(bodyStr.get(), Subtask.class);
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
            taskManager.deleteSubtask(id);
            sendSuccess(exchange);
        } catch (NotFoundException e) {
            sendNotFound(exchange, e.getMessage());
        }
    }

}
