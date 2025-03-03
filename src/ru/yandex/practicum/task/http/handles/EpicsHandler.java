package ru.yandex.practicum.task.http.handles;

import com.google.gson.JsonSyntaxException;
import com.sun.net.httpserver.HttpExchange;
import ru.yandex.practicum.task.error.NotFoundException;
import ru.yandex.practicum.task.interfaces.TaskManager;
import ru.yandex.practicum.task.tasks.Epic;

import java.util.Optional;

public class EpicsHandler extends BaseHttpHandler {

    public EpicsHandler(TaskManager taskManager) {
        super(taskManager, "epics");
    }

    @Override
    protected void handleGetItems(HttpExchange exchange) {
        sendResponse(exchange, taskManager.getAllEpics());
    }

    @Override
    protected void handleGetItemById(HttpExchange exchange) {
        try {
            Epic epic = taskManager.getEpic(getId(exchange));
            sendResponse(exchange, epic);
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
            Epic epic = gson.fromJson(bodyStr.get(), Epic.class);
            if (epic.getId() == null) {
                taskManager.createEpic(epic);
            } else {
                taskManager.updateEpic(epic);
            }
            sendSuccess(exchange);
        } catch (NullPointerException | JsonSyntaxException e) {
            sendBadRequest(exchange, "Неверное тело запроса");
        } catch (Exception e) {
            sendServerError(exchange);
        }
    }

    @Override
    protected void handleGetChildrenItemsById(HttpExchange exchange) {
        try {
            Epic epic = taskManager.getEpic(getId(exchange));
            sendResponse(exchange, taskManager.getSubtasksByEpic(epic));
        } catch (NotFoundException e) {
            sendNotFound(exchange, e.getMessage());
        }
    }

    @Override
    protected void handleDeleteItem(HttpExchange exchange) {
        try {
            int id = getId(exchange);
            taskManager.deleteEpic(id);
            sendSuccess(exchange);
        } catch (NotFoundException e) {
            sendNotFound(exchange, e.getMessage());
        }
    }

}
