package ru.yandex.practicum.task.http.handlers;

import com.sun.net.httpserver.HttpExchange;
import ru.yandex.practicum.task.error.NotFoundException;
import ru.yandex.practicum.task.interfaces.TaskManager;
import ru.yandex.practicum.task.tasks.Epic;

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
        handlePostItemByConsumer(exchange, bodyStr -> {
            Epic epic = gson.fromJson(bodyStr, Epic.class);
            if (epic.getId() == null) {
                taskManager.createEpic(epic);
            } else {
                taskManager.updateEpic(epic);
            }
            sendSuccess(exchange);
        });
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
