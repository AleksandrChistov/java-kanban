package ru.yandex.practicum.task.http.handlers;

import com.sun.net.httpserver.HttpExchange;
import ru.yandex.practicum.task.interfaces.TaskManager;

public class HistoryHandler extends BaseHttpHandler {

    public HistoryHandler(TaskManager taskManager) {
        super(taskManager, "history");
    }

    @Override
    protected void handleGetItems(HttpExchange exchange) {
        sendResponse(exchange, taskManager.getHistory());
    }

}
