package ru.yandex.practicum.task.server.handles;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonSyntaxException;
import com.sun.net.httpserver.HttpExchange;
import ru.yandex.practicum.task.enums.TaskStatus;
import ru.yandex.practicum.task.error.NotFoundException;
import ru.yandex.practicum.task.error.TimeIntersectedException;
import ru.yandex.practicum.task.interfaces.TaskManager;
import ru.yandex.practicum.task.tasks.Task;

import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;
import java.util.Optional;
import java.util.stream.Collectors;

public class TasksHandler extends BaseHttpHandler {

    public TasksHandler(TaskManager taskManager) {
        super(taskManager, "tasks");
    }

    @Override
    protected void handleGetItems(HttpExchange exchange) {
        String response = taskManager.getAllTasks().stream()
                .map(Task::toString)
                .collect(Collectors.joining("\n"));
        writeResponse(exchange, response.isBlank() ? "[]" : response, 200);
    }

    @Override
    protected void handleGetItemById(HttpExchange exchange) {
        try {
            Task task = taskManager.getTask(getId(exchange));
            writeResponse(exchange, task.toString(), 200);
        } catch (NotFoundException e) {
            sendNotFound(exchange, e.getMessage());
        }
    }

    @Override
    protected void handlePostItem(HttpExchange exchange) {
        Optional<String> bodyStr = getBody(exchange);

        if (bodyStr.isPresent()) {
            JsonElement jsonElement = JsonParser.parseString(bodyStr.get());
            try {
                if (!jsonElement.isJsonObject()) {
                    throw new JsonSyntaxException("Неверное тело запроса");
                }

                Task task = getTaskFromJson(jsonElement);

                try {
                    if (task.getId() == null) {
                        taskManager.createTask(task);
                    } else {
                        taskManager.updateTask(task);
                    }
                    sendText(exchange);
                } catch (TimeIntersectedException e) {
                    sendHasInteractions(exchange, e.getMessage());
                }
            } catch (NullPointerException | DateTimeParseException | JsonSyntaxException e) {
                writeResponse(exchange, "Неверное тело запроса", 400);
            } catch (Exception e) {
                writeResponse(exchange, "Внутренняя ошибка сервера", 500);
            }
        } else {
            writeResponse(exchange, "Тело запроса не может быть пустым", 400);
        }
    }


    @Override
    protected void handleDeleteItem(HttpExchange exchange) {
        try {
            int id = getId(exchange);
            Task task = taskManager.deleteTask(id);

            if (task == null) {
                throw new NotFoundException(String.valueOf(id));
            }

            sendSuccess(exchange);
        } catch (NotFoundException e) {
            sendNotFound(exchange, e.getMessage());
        }
    }

    private Task getTaskFromJson(JsonElement jsonElement) throws NullPointerException, DateTimeParseException {
        JsonObject jsonObject = jsonElement.getAsJsonObject();

        Integer id = jsonObject.get("id") != null ? jsonObject.get("id").getAsInt() : null;
        String name = jsonObject.get("name").getAsString();
        String description = jsonObject.get("description").getAsString();
        TaskStatus status = TaskStatus.valueOf(jsonObject.get("status").getAsString());

        String startTimeStr = jsonObject.get("startTime") != null ? jsonObject.get("startTime").getAsString() : null;
        Long durationMinute = jsonObject.get("duration") != null ? jsonObject.get("duration").getAsLong() : null;

        Task newTask;

        if (startTimeStr == null || durationMinute == null) {
            newTask = new Task(name, description, status);
        } else {
            LocalDateTime startTime = LocalDateTime.parse(startTimeStr);
            newTask = new Task(name, description, status, startTime, durationMinute);
        }

        if (id != null) {
            newTask.setId(id);
        }

        return newTask;
    }
}
