package ru.yandex.practicum.task.server.handles;

import com.sun.net.httpserver.HttpExchange;
import ru.yandex.practicum.task.interfaces.TaskManager;
import ru.yandex.practicum.task.tasks.Task;

import java.util.stream.Collectors;

public class PrioritizedHandler extends BaseHttpHandler {

    public PrioritizedHandler(TaskManager taskManager) {
        super(taskManager, "prioritized");
    }

    @Override
    protected void handleGetItems(HttpExchange exchange) {
        String response = taskManager.getPrioritizedTasks().stream()
                .map(Task::toString)
                .collect(Collectors.joining("\n"));
        writeResponse(exchange, response.isBlank() ? "[]" : response, 200);
    }

}
