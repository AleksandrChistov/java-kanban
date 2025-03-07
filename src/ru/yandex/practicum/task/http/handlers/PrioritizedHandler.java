package ru.yandex.practicum.task.http.handlers;

import com.sun.net.httpserver.HttpExchange;
import ru.yandex.practicum.task.interfaces.TaskManager;

public class PrioritizedHandler extends BaseHttpHandler {

    public PrioritizedHandler(TaskManager taskManager) {
        super(taskManager, "prioritized");
    }

    @Override
    protected void handleGetItems(HttpExchange exchange) {
        sendResponse(exchange, taskManager.getPrioritizedTasks());
    }

}
